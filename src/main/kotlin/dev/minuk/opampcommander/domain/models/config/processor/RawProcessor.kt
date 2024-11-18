package dev.minuk.opampcommander.domain.models.config.processor

import dev.minuk.opampcommander.domain.models.config.ProcessorConfig

/**
 * RawProcessor is a processor for raw data.
 * opampcommander cannot support all kind of processors.
 * So, use this processor when you want to use unsupported processor.
 */
class RawProcessor(
    override val type: String,
    override val name: String,
    override val value: Any
): ProcessorConfig