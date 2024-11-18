package dev.minuk.opampcommander.domain.models.config

import org.yaml.snakeyaml.Yaml

data class OTELCollectorConfig (
    val receivers: List<ReceiverConfig>,
    val processors: List<ProcessorConfig>,
    val exporters: List<ExporterConfig>,

    val pipelines: List<PipelineConfig>,
) {

    data class OTELCollectorDTO(
        val receivers: Map<String, Any>,
        val processors: Map<String, Any>,
        val exporters: Map<String, Any>,
        val service: Map<String, Any>,
    ) {
        fun toYAMLString(): String {
            val yaml = Yaml()
            return yaml.dump(this)
        }

        override fun toString(): String {
            return toYAMLString()
        }
    }

    fun toDto(): OTELCollectorDTO {
        return OTELCollectorDTO(
            receivers = receivers.associate { "${it.type}/${it.name}" to it.value },
            processors = processors.associate { "${it.type}/${it.name}" to it.value },
            exporters = exporters.associate { "${it.type}/${it.name}" to it.value },
            service = mapOf("pipelines" to pipelines.associate { "${it.type}/${it.name}" to it.value }),
        )
    }

    val value: String
        get() = toDto().toYAMLString()
}

