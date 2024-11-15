package dev.minuk.opampcommander.domain.models.config.exporter

import dev.minuk.opampcommander.domain.models.config.ExporterConfig
import java.time.Duration

class OTLPExporter(
    val retryOnFailure: BackoffConfig? = null,
    val sendingQueue: QueueConfig? = null,
    override val name: String,
    override val timeout: Duration? = null,
) : ExporterConfig, TimeoutConfig, GrpcClientConfig {
    override val type: String
        get() = "otlp"
    override val value: Any
        get() {
        return mapOf(
            "name" to name,
            "retryOnFailure" to retryOnFailure.value,
        )
    }
}

interface TimeoutConfig {
    val timeout: Duration?
}

class QueueConfig(
    val enabled: Boolean?,
    val numConsumers: Int?,
    val queueSize: Int?,
)

interface GrpcClientConfig {
    val endpoint: String?
    // TODO: Add more fields
}

interface BackoffConfig {
    val enabled: Boolean?
    val initialInterval: Duration?
    val randomizationFactor: Double?
    val multiplier: Double?
    val maxInterval: Duration?
    val maxElapsedTime: Duration?
}