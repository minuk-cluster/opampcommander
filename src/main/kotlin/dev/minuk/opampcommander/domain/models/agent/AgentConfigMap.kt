package dev.minuk.opampcommander.domain.models.agent

data class AgentConfigMap(
    val configMap: Map<String, AgentConfigFile>,
) {
    companion object {
        fun empty(): AgentConfigMap = AgentConfigMap(mapOf())
    }
}

data class AgentConfigFile(
    val body: ByteArray,
    val contentType: String,
)
