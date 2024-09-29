package dev.minuk.otelcommander.domain.models.agent

data class CustomCapabilities(
    val capabilities: List<String>
) {
    companion object {
        fun empty(): CustomCapabilities {
            return CustomCapabilities(emptyList())
        }
    }
}