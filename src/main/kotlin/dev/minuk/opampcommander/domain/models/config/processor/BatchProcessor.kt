package dev.minuk.opampcommander.domain.models.config.processor

import dev.minuk.opampcommander.domain.models.config.ProcessorConfig
import java.time.Duration

// https://github.com/open-telemetry/opentelemetry-collector/tree/main/processor/batchprocessor
class BatchProcessor(
    override val name: String,

    val timeout: Duration? = null,
    val sendBatchSize: UInt? = null,
    val metadataKeys: List<String>? = null,
    val metadataCardinalityLimit: UInt? = null,
) : ProcessorConfig {
    override val type: String
        get() = "batch"

    override val value: Any
        get() = listOf(
            "name" to name,
            "type" to type,
            "timeout" to timeout,
            "send_batch_size" to sendBatchSize,
            "metadata_keys" to metadataKeys,
            "metadata_cardinality_limit" to metadataCardinalityLimit,
        ).filter {
            it.second != null
        }.toMap()
}
