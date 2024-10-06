package dev.minuk.opampcommander.domain.models.command

data class AgentRemoteConfig(
    val config: AgentConfigMap,
    val configHash: ByteArray,
)
