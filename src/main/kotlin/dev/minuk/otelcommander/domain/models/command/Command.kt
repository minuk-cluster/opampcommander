package dev.minuk.otelcommander.domain.models.command

import java.util.UUID

interface Command {
    val targetInstanceUids: List<UUID>
}

class ReportFullStateCommand(
    override val targetInstanceUids: List<UUID>
): Command