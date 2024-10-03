package dev.minuk.opampcommander.domain.models.command

import java.util.UUID

interface Command {
    val targetInstanceUids: List<UUID>
}

class ReportFullStateCommand(
    override val targetInstanceUids: List<UUID>,
) : Command
