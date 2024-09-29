package dev.minuk.otelcommander.domain.models.agent

data class Service(
    val instanceId: String?,
    val name: String?,
    val namespace: String?,
    val version: String?,
)
