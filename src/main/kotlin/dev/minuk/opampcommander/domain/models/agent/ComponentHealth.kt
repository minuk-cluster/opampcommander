package dev.minuk.opampcommander.domain.models.agent

import java.time.Instant

data class ComponentHealth(
    val healthy: Boolean,
    val startedAt: Instant,
    val lastError: String,
    val status: String,
    val statusObservedAt: Instant,
    val componentHealthMap: Map<String, ComponentHealth>,
) {
    companion object {
        fun empty(): ComponentHealth =
            ComponentHealth(
                healthy = false,
                startedAt = Instant.now(),
                lastError = "",
                status = "",
                statusObservedAt = Instant.now(),
                componentHealthMap = mapOf(),
            )
    }
}
