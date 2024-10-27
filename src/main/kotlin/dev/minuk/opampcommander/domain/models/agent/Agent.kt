package dev.minuk.opampcommander.domain.models.agent

import com.github.f4b6a3.ulid.Ulid

data class AgentCapabilities(
    val capabilities: Set<Capability>,
) {
    companion object {
        fun empty(): AgentCapabilities =
            AgentCapabilities(
                capabilities = setOf(),
            )

        fun of(vararg capabilities: Capability): AgentCapabilities =
            AgentCapabilities(
                capabilities = capabilities.toSet(),
            )

        fun of(capabilities: Long): AgentCapabilities =
            AgentCapabilities(
                capabilities = Capability.entries.filter { it.value and capabilities != 0L }.toSet(),
            )
    }

    fun toLong(): Long = capabilities.fold(0L) { acc, capability -> acc or capability.value }

    val hasReportsStatus: Boolean
        get() = capabilities.contains(Capability.ReportsStatus)

    enum class Capability(
        val value: Long,
    ) {
        UnspecifiedAgentCapability(0),
        ReportsStatus(0x00000001),
        AcceptsRemoteConfig(0x00000002),
        ReportsEffectiveConfig(0x00000004),
        AcceptsPackages(0x00000008),
        ReportsPackageStatuses(0x00000010),
        ReportsOwnTraces(0x00000020),
        ReportsOwnMetrics(0x00000040),
        ReportsOwnLogs(0x00000080),
        AcceptsOpAMPConnectionSettings(0x00000100),
        AcceptsOtherConnectionSettings(0x00000200),
        AcceptsRestartCommand(0x00000400),
        ReportsHealth(0x00000800),
        ReportsRemoteConfig(0x00001000),
        ;

        companion object {
            fun of(value: Long): Capability = entries.find { it.value == value }!!
        }
    }
}

data class Agent(
    val instanceUid: Ulid,
    val capabilities: AgentCapabilities?,
    val agentDescription: AgentDescription?,
    val effectiveConfig: EffectiveConfig?,
    val packageStatuses: PackageStatuses?,
    val componentHealth: ComponentHealth?,
    val customCapabilities: CustomCapabilities?,
    val communicationStatus: CommunicationStatus?,
) {
    constructor(instanceUid: Ulid) : this(
        instanceUid = instanceUid,
        capabilities = null,
        agentDescription = null,
        effectiveConfig = null,
        communicationStatus = null,
        packageStatuses = null,
        componentHealth = null,
        customCapabilities = null,
    )

    fun report(
        reportedCapabilities: AgentCapabilities? = null,
        reportedAgentDescription: AgentDescription? = null,
        reportedComponentHealth: ComponentHealth? = null,
        reportedEffectiveConfig: EffectiveConfig? = null,
        reportedPackageStatuses: PackageStatuses? = null,
        reportedCustomCapabilities: CustomCapabilities? = null,
        reportedCommunicationStatus: CommunicationStatus? = null,
    ): Agent =
        Agent(
            instanceUid = this.instanceUid,
            capabilities = reportedCapabilities ?: this.capabilities,
            agentDescription = reportedAgentDescription ?: this.agentDescription,
            componentHealth = reportedComponentHealth ?: this.componentHealth,
            effectiveConfig = reportedEffectiveConfig ?: this.effectiveConfig,
            communicationStatus = reportedCommunicationStatus ?: this.communicationStatus,
            packageStatuses = reportedPackageStatuses ?: this.packageStatuses,
            customCapabilities = reportedCustomCapabilities ?: this.customCapabilities,
        )

    val os: Os?
        get() = agentDescription?.os
    val service: Service?
        get() = agentDescription?.service
    val host: Host?
        get() = agentDescription?.host

    fun applyRemoteConfig(newRemoteConfig: AgentRemoteConfig) {
        TODO("Not yet implemented")
    }
}
