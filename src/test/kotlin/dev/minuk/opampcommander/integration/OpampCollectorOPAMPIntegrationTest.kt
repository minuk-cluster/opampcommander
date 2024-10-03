package dev.minuk.opampcommander.integration

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.Transferable
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpampCollectorOPAMPIntegrationTest {
    companion object {
        val otelCollectorContainerImage = "otel/opentelemetry-collector-contrib:0.110.0"
    }

    @LocalServerPort
    private var localPort: Int? = null

    @Test
    @Disabled
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
        Thread.sleep(1000 * 60 * 60)
        otelCollectorContainer.stop()
    }

    @Test
    @Disabled
    fun `otel-collector-contrib initialization with opamp extension by websocket`() {
        val otelConfigYaml =
            """
            extensions:
              health_check:
              opamp:
                server:
                  ws:
                    endpoint: "ws://host.docker.internal:$localPort/v1/opamp"
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
        Thread.sleep(1000 * 60 * 60)
        otelCollectorContainer.stop()
    }
}
