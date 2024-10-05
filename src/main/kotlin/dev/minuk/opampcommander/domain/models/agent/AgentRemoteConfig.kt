package dev.minuk.opampcommander.domain.models.agent

data class AgentRemoteConfig(
    val config: AgentConfigMap,
    val configHash: ByteArray,
)
