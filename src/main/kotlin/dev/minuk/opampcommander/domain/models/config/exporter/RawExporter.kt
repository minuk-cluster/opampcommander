package dev.minuk.opampcommander.domain.models.config.exporter

import dev.minuk.opampcommander.domain.models.config.ExporterConfig

/**
 * RawExporter is a exporter for raw data.
 * opampcommander cannot support all kind of exporters.
 * So, use this exporter when you want to use unsupported exporter.
 */
class RawExporter(
    override val type: String,
    override val name: String,
    override val value: Any
): ExporterConfig