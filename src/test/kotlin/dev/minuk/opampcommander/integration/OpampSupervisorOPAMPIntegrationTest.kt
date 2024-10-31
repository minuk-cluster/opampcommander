package dev.minuk.opampcommander.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.minuk.opampcommander.adapter.primary.http.api.v1.admin.agent.MultipleAgentResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.images.builder.Transferable
import org.testcontainers.images.builder.dockerfile.statement.MultiArgsStatement
import org.testcontainers.images.builder.dockerfile.statement.SingleArgumentStatement
import org.testcontainers.images.builder.dockerfile.statement.Statement
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.nio.file.Path
import java.time.Duration

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpampSupervisorOPAMPIntegrationTest(
    @Autowired
    private val webClient: WebTestClient,
) {
    init {
        val file = File.createTempFile("Dockerfile", "")
        file.writeText(
            """
            FROM golang:1.23 as builder
            WORKDIR /
            RUN wget https://github.com/open-telemetry/opentelemetry-collector-contrib/archive/refs/tags/v0.112.0.tar.gz
            RUN tar -zxvf v0.112.0.tar.gz
            WORKDIR opentelemetry-collector-contrib-0.112.0/cmd/opampsupervisor
            RUN CGO_ENABLED=0 go build
            RUN echo '\
            server:\n\
              endpoint: wss://127.0.0.1:4320/v1/opamp\n\
              tls:\n\
                insecure_skip_verify: true\n\
            capabilities:\n\
              reports_effective_config: true\n\
              reports_own_metrics: true\n\
              reports_health: true\n\
              accepts_remote_config: true\n\
              reports_remote_config: true\n\
            agent:\n\ 
              executable: /otelcol-contrib\n\
            storage:\n\ 
              directory: . '\
            >> /config.yaml

            FROM otel/opentelemetry-collector-contrib:0.112.0
            #FROM ubuntu:20.04
            COPY --chmod=755 --from=builder /opentelemetry-collector-contrib-0.112.0/cmd/opampsupervisor/opampsupervisor /opampsupervisor
            COPY --from=builder /config.yaml /etc/opampsupervisor/config.yaml
            ENTRYPOINT ["/opampsupervisor"]
            CMD ["--config", "/etc/opampsupervisor/config.yaml"]
            """.trimIndent()
        )
        ImageFromDockerfile()
            .withDockerfile(Path.of(file.absolutePath))
    }

    companion object {
        val otelCollectorContainerImage = "otel/opentelemetry-collector-contrib:0.110.0"

        @Container
        @JvmStatic
        val mongoDbContainer =
            MongoDBContainer("mongo:latest")
                .withReuse(true)
                .withStartupTimeout(Duration.ofSeconds(30))

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri") {
                mongoDbContainer.replicaSetUrl
            }
        }
    }

    @LocalServerPort
    private var localPort: Int? = null

    @Test
    fun `otel-collector-contrib initialization with opamp extension by http`() {
        val otelConfigYaml =
            """
            extensions:
              health_check:
              opamp:
                server:
                  http:
                    endpoint: "http://host.testcontainers.internal:$localPort/v1/opamp"
            receivers:
              nop:
            processors:
              batch:
            exporters:
              nop:
            service:
              extensions: [opamp]
              telemetry:
                metrics:
                  level: detailed
                logs:
                  level: DEBUG
              pipelines:
                metrics:
                  receivers: [nop]
                  processors: [batch]
                  exporters: [nop]
                
            """.trimIndent()

        org.testcontainers.Testcontainers.exposeHostPorts(localPort!!);
        val otelCollectorContainer =
            GenericContainer(otelCollectorContainerImage)
                .withCopyToContainer(
                    Transferable.of(otelConfigYaml),
                    "/etc/otelcol-contrib/config.yaml",
                ).withStartupTimeout(Duration.ofSeconds(30))
        otelCollectorContainer.start()

        var success = false
        for (i in 1..10) {
            webClient
                .get()
                .uri("/api/v1/admin/agent")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .consumeWith { resp ->
                    val response = jacksonObjectMapper().readValue(resp.responseBody, MultipleAgentResponse::class.java)
                    if (response.totalCount == 1L) {
                        success = true
                    }
                }
            if (success) {
                break
            }
            Thread.sleep(1000 * 5)
        }

        assertTrue(success)
        otelCollectorContainer.stop()
    }

    @Test
    fun `otel-collector-contrib initialization with opamp extension by websocket`() {
        val otelConfigYaml =
            """
            extensions:
              health_check:
              opamp:
                server:
                  ws:
                    endpoint: "ws://host.testcontainers.internal:$localPort/ws/v1/opamp"
                    tls:
                      insecure: true
            receivers:
              nop:
            processors:
              batch:
            exporters:
              nop:
            service:
              extensions: [opamp]
              telemetry:
                metrics:
                  level: detailed
                logs:
                  level: DEBUG
              pipelines:
                metrics:
                  receivers: [nop]
                  processors: [batch]
                  exporters: [nop]
                
            """.trimIndent()

        org.testcontainers.Testcontainers.exposeHostPorts(localPort!!);
        val otelCollectorContainer =
            GenericContainer(otelCollectorContainerImage)
                .withCopyToContainer(
                    Transferable.of(otelConfigYaml),
                    "/etc/otelcol-contrib/config.yaml",
                ).withStartupTimeout(Duration.ofSeconds(30))
        otelCollectorContainer.start()
        var success = false
        for (i in 1..10) {
            webClient
                .get()
                .uri("/api/v1/admin/agent")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .consumeWith { resp ->
                    val response = jacksonObjectMapper().readValue(resp.responseBody, MultipleAgentResponse::class.java)
                    if (response.totalCount == 1L) {
                        success = true
                    }
                }
            if (success) {
                break
            }
            Thread.sleep(1000 * 5)
        }

        assertTrue(success)
        otelCollectorContainer.stop()
    }
}
