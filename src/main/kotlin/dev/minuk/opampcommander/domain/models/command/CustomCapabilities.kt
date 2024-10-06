package dev.minuk.opampcommander.domain.models.command

data class CustomCapabilities(
    val capabilities: List<String>,
) {
    companion object {
        fun empty(): CustomCapabilities = CustomCapabilities(emptyList())
    }
}
