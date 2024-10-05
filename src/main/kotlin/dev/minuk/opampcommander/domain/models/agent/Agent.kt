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
        newAgentDescription: AgentDescription? = null,
        newComponentHealth: ComponentHealth? = null,
        newEffectiveConfig: EffectiveConfig? = null,
        newPackageStatuses: PackageStatuses? = null,
        newCustomCapabilities: CustomCapabilities? = null,
    ): Agent =
        Agent(
            instanceUid = this.instanceUid,
            agentDescription = newAgentDescription ?: this.agentDescription,
            componentHealth = newComponentHealth ?: this.componentHealth,
            effectiveConfig = newEffectiveConfig ?: this.effectiveConfig,
            communicationStatus = this.communicationStatus,
            packageStatuses = newPackageStatuses ?: this.packageStatuses,
            customCapabilities = newCustomCapabilities ?: this.customCapabilities,
        )

    val os: Os
        get() = agentDescription.os
    val service: Service
        get() = agentDescription.service
    val host: Host
        get() = agentDescription.host
}
