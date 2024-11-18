package dev.minuk.opampcommander.domain.models.config.recevier

import dev.minuk.opampcommander.domain.models.config.ReceiverConfig

/**
 * RawReceiver is a receiver for raw data.
 * opampcommander cannot support all kind of receivers.
 * So, use this receiver when you want to use unsupported receiver.
 */
class RawReceiver(
    override val type: String,
    override val name: String,
    override val value: Any
): ReceiverConfig