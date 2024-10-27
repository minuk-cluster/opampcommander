package dev.minuk.opampcommander.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.minuk.opampcommander.adapter.primary.http.api.v1.admin.agent.MultipleAgentResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.images.builder.Transferable
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpampCollectorOPAMPIntegrationTest(
    @Autowired
    private val webClient: WebTestClient,
) {
    companion object {
        val otelCollectorContainerImage = "otel/opentelemetry-collector-contrib:0.110.0"

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: org.springframework.test.context.DynamicPropertyRegistry) {
            val mongoDbContainer = MongoDBContainer("mongo:latest")
            mongoDbContainer.start()
            registry.add("spring.data.mongodb.uri") { mongoDbContainer.replicaSetUrl }
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
                    endpoint: "http://host.docker.internal:$localPort/v1/opamp"
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
                .consumeWith { response ->
                    val response = jacksonObjectMapper().readValue(response.responseBody, MultipleAgentResponse::class.java)
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
                    endpoint: "ws://host.docker.internal:$localPort/ws/v1/opamp"
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
                .consumeWith { response ->
                    val response = jacksonObjectMapper().readValue(response.responseBody, MultipleAgentResponse::class.java)
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
