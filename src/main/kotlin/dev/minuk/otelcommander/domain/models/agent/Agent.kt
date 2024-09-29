package dev.minuk.otelcommander.domain.models.agent

import java.util.UUID

data class Agent(
    val instanceUid: UUID,
    val agentDescription: AgentDescription,
    val effectiveConfig: EffectiveConfig,
    val communicationStatus: CommunicationStatus,
    val packageStatuses: PackageStatuses,
    val componentHealth: ComponentHealth,
    val customCapabilities: CustomCapabilities,
) {
    constructor(instanceUid: UUID) : this(
        instanceUid = instanceUid,
        agentDescription = AgentDescription.empty(),
        effectiveConfig = EffectiveConfig.empty(),
        communicationStatus = CommunicationStatus.empty(),
        packageStatuses = PackageStatuses.empty(),
        componentHealth = ComponentHealth.empty(),
        customCapabilities = CustomCapabilities.empty(),
    )

    fun update(
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
