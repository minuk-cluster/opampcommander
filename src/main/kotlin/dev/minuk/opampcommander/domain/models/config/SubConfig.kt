package dev.minuk.opampcommander.domain.models.config

interface SubConfig {
    val type: String
    val name: String
    val value: Any
}