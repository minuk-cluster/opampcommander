package dev.minuk.opampcommander.domain.models.config

interface PipelineConfig: SubConfig {
    val receivers: List<String>
    val processors: List<String>
    val exporters: List<String>

    override val value: Any
        get() = mapOf(
            "receivers" to receivers,
            "processors" to processors,
            "exporters" to exporters
        )
}

class MetricPipeline(
    override val name: String,
    override val receivers: List<String>,
    override val processors: List<String>,
    override val exporters: List<String>,
): PipelineConfig{
    override val type: String
        get() = "metrics"
}

class LogPipeLine(
    override val name: String,
    override val receivers: List<String>,
    override val processors: List<String>,
    override val exporters: List<String>,
): PipelineConfig{
    override val type: String
        get() = "logs"
}

class TracePipeLine(
    override val name: String,
    override val receivers: List<String>,
    override val processors: List<String>,
    override val exporters: List<String>,
): PipelineConfig{
    override val type: String
        get() = "traces"
}