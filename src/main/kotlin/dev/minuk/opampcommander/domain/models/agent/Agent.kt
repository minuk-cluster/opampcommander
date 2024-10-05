package dev.minuk.opampcommander.domain.models.agent

import com.github.f4b6a3.ulid.Ulid

data class Agent(
    val instanceUid: Ulid,
    val agentDescription: AgentDescription,
    val effectiveConfig: EffectiveConfig,
    val packageStatuses: PackageStatuses,
    val componentHealth: ComponentHealth,
    val customCapabilities: CustomCapabilities,
    val communicationStatus: CommunicationStatus,
) {
    constructor(instanceUid: Ulid) : this(
        instanceUid = instanceUid,
        agentDescription = AgentDescription.empty(),
        effectiveConfig = EffectiveConfig.empty(),
        communicationStatus = CommunicationStatus.empty(),
        packageStatuses = PackageStatuses.empty(),
        componentHealth = ComponentHealth.empty(),
        customCapabilities = CustomCapabilities.empty(),
    )

    fun report(
        reportedAgentDescription: AgentDescription? = null,
        reportedComponentHealth: ComponentHealth? = null,
        reportedEffectiveConfig: EffectiveConfig? = null,
        reportedPackageStatuses: PackageStatuses? = null,
        reportedCustomCapabilities: CustomCapabilities? = null,
    ): Agent =
        Agent(
            instanceUid = this.instanceUid,
            agentDescription = reportedAgentDescription ?: this.agentDescription,
            componentHealth = reportedComponentHealth ?: this.componentHealth,
            effectiveConfig = reportedEffectiveConfig ?: this.effectiveConfig,
            communicationStatus = this.communicationStatus,
            packageStatuses = reportedPackageStatuses ?: this.packageStatuses,
            customCapabilities = reportedCustomCapabilities ?: this.customCapabilities,
        )

    val os: Os
        get() = agentDescription.os
    val service: Service
        get() = agentDescription.service
    val host: Host
        get() = agentDescription.host
}
