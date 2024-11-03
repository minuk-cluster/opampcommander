package dev.minuk.opampcommander.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.minuk.opampcommander.adapter.primary.http.api.v1.admin.agent.MultipleAgentResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
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
    @Disabled("This test is disabled because it is not working because opampsupervisor does not support http")
    fun `opampsupervisor by http`(
        @TempDir dockerBuildDir: Path,
        @TempDir testDir: Path,
    ) {
        val file = File.createTempFile("Dockerfile", "", dockerBuildDir.toFile())
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
              directory: /tmp'\
            >> /config.yaml

            FROM otel/opentelemetry-collector-contrib:0.112.0
            COPY --from=builder /opentelemetry-collector-contrib-0.112.0/cmd/opampsupervisor/opampsupervisor /opampsupervisor
            COPY --from=builder /config.yaml /etc/opampsupervisor/config.yaml
            ENTRYPOINT ["/opampsupervisor"]
            CMD ["--config", "/etc/opampsupervisor/config.yaml"]
            """.trimIndent()
        )
        val opampImage = ImageFromDockerfile()
            .withDockerfile(Path.of(file.absolutePath))
        val opampsupervisorConfigYaml =
            """
            server:
              endpoint: ws://host.testcontainers.internal:$localPort/v1/opamp
              tls:
                insecure_skip_verify: true
            capabilities:
              reports_effective_config: true
              reports_own_metrics: true
              reports_health: true
              accepts_remote_config: true
              reports_remote_config: true
            agent:
              executable: /otelcol-contrib
            storage:
              directory: /tmp
            """.trimIndent()

        org.testcontainers.Testcontainers.exposeHostPorts(localPort!!);

        val otelCollectorContainer =
            GenericContainer(opampImage)
                .withTmpFs(mapOf("/tmp" to "rw"))
                .withCopyToContainer(
                    Transferable.of(opampsupervisorConfigYaml),
                    "/etc/opampsupervisor/config.yaml",
                ).withStartupTimeout(Duration.ofSeconds(30))
        otelCollectorContainer.start()
        Thread.sleep(1000 * 60 * 60)

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
    fun `opampsupervisor by websocket`(
        @TempDir dockerBuildDir: Path,
        @TempDir testDir: Path,
    ) {
        val file = File.createTempFile("Dockerfile", "", dockerBuildDir.toFile())
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
              directory: /tmp'\
            >> /config.yaml

            FROM otel/opentelemetry-collector-contrib:0.112.0
            COPY --from=builder /opentelemetry-collector-contrib-0.112.0/cmd/opampsupervisor/opampsupervisor /opampsupervisor
            COPY --from=builder /config.yaml /etc/opampsupervisor/config.yaml
            ENTRYPOINT ["/opampsupervisor"]
            CMD ["--config", "/etc/opampsupervisor/config.yaml"]
            """.trimIndent()
        )
        val opampImage = ImageFromDockerfile()
            .withDockerfile(Path.of(file.absolutePath))
        val opampsupervisorConfigYaml =
            """
            server:
              endpoint: ws://host.testcontainers.internal:$localPort/ws/v1/opamp
              tls:
                insecure_skip_verify: true
            capabilities:
              reports_effective_config: true
              reports_own_metrics: true
              reports_health: true
              accepts_remote_config: true
              reports_remote_config: true
            agent:
              executable: /otelcol-contrib
            storage:
              directory: /tmp
            """.trimIndent()

        org.testcontainers.Testcontainers.exposeHostPorts(localPort!!);

        val otelCollectorContainer =
            GenericContainer(opampImage)
                .withTmpFs(mapOf("/tmp" to "rw"))
                .withCopyToContainer(
                    Transferable.of(opampsupervisorConfigYaml),
                    "/etc/opampsupervisor/config.yaml",
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
