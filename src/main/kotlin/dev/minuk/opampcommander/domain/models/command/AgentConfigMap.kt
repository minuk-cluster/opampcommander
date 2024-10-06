package dev.minuk.opampcommander.domain.models.command

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
