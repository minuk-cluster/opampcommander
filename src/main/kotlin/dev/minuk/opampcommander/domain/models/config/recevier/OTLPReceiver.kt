package dev.minuk.opampcommander.domain.models.config.recevier

import dev.minuk.opampcommander.domain.models.config.ReceiverConfig

class OTLPReceiver(
    override val name: String,
    val protocols: Protocols,
): ReceiverConfig {
    override val type: String
        get() = "otlp"
    override val value: Any
        get() = mapOf(
            "protocols" to protocols
        )
}

class Protocols(
    val http: Http,
    val grpc: Grpc,
    // TODO grpc
)

// https://github.com/open-telemetry/opentelemetry-collector/blob/1703ce6df6979d84720b5826c97d579cf11a59bb/receiver/otlpreceiver/config.go#L24
class Http(
    endpoint: String? = null,
    val traceURLPath: String? = null,
    val metricURLPath: String? = null,
    val logURLPath: String? = null,
) : ServerConfig(endpoint)

// https://github.com/open-telemetry/opentelemetry-collector/blob/1703ce6df6979d84720b5826c97d579cf11a59bb/config/configgrpc/configgrpc.go#L164
class Grpc

// https://github.com/open-telemetry/opentelemetry-collector/blob/1703ce6df6979d84720b5826c97d579cf11a59bb/config/confighttp/confighttp.go#L274
open class ServerConfig(
    val endpoint: String? = null,
    // TODO
    // val tls: TLSConfig? = null,
    // val cors: CORSConfig? = null,
    // val auth: AuthConfig? = null,
    // val maxRequestBodySize: Long? = null,
    // val includeMetadata: Boolean? = null,
    // val responseHeaders: Map<String, String>? = null,
    // val compressionAlgorithms: List<String>? = null,
    // val readTimeout: time.Duration? = null,
    // val readHeaderTimeout: time.Duration? = null,
    // val writeTimeout: time.Duration? = null,
    // val idleTimeout: time.Duration? = null,
)