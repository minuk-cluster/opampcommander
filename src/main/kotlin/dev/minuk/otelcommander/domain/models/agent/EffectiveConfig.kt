package dev.minuk.otelcommander.domain.models.agent

data class EffectiveConfig(
    val configMap: Map<String, AgentConfigMap>,
) {
    companion object {
        fun empty(): EffectiveConfig = EffectiveConfig(mapOf())
    }

    data class AgentConfigMap(
        val body: String,
        val contentType: String,
    )
}
