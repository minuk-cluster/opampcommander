package dev.minuk.opampcommander.domain.models.command

enum class CommandKind {
    DirectServerToAgent,
    SetRemoteConfig,
    SetConnectionSettings,
    UpdateAvailablePackages,
    UpdateAgentInstanceUid,
    SendRestartCommand,
    SendCustomMessage,
    ReportFullState,
}

interface RawCommand {
    val kind: CommandKind
}

class ReportFullStateCommand : RawCommand {
    override val kind: CommandKind
        get() = CommandKind.ReportFullState
}
