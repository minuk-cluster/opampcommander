package dev.minuk.opampcommander.domain.models.agent

data class EffectiveConfig(
    val configMap: AgentConfigMap,
) {
    companion object {
        fun empty(): EffectiveConfig =
            EffectiveConfig(
                configMap = AgentConfigMap.empty(),
            )
    }
}
