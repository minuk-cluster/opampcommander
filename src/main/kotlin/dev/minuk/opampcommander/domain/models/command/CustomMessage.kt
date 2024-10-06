package dev.minuk.opampcommander.domain.models.command

data class CustomMessage(
    val capability: String,
    val type: String,
    val data: ByteArray,
)
