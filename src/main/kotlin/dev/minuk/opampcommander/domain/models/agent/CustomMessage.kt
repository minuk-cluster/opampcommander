package dev.minuk.opampcommander.domain.models.agent

data class CustomMessage(
    val capability: String,
    val type: String,
    val data: ByteArray,
)
