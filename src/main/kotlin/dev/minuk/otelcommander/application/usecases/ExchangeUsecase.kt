package dev.minuk.otelcommander.application.usecases

import dev.minuk.otelcommander.domain.models.agent.AgentDescription
import dev.minuk.otelcommander.domain.models.agent.ComponentHealth
import dev.minuk.otelcommander.domain.models.agent.CustomCapabilities
import dev.minuk.otelcommander.domain.models.agent.EffectiveConfig
import dev.minuk.otelcommander.domain.models.agent.PackageStatuses
import dev.minuk.otelcommander.domain.models.agent.RemoteConfigStatus
import java.util.UUID

interface ExchangeUsecase {
    // TOOD: make agentToServer as standalone class without protobuf dependency.
    suspend fun exchange(request: AgentExchangeRequest)
}

data class AgentExchangeRequest(
    val instanceUid: UUID,
    val sequenceNum: Long,
    val agentDescription: AgentDescription?,
    val capabilities: Capabilities,
    val componentHealth: ComponentHealth?,
    val effectiveConfig: EffectiveConfig?,
    val remoteConfigStatus: RemoteConfigStatus?,
    val packageStatuses: PackageStatuses?,
    val customCapabilities: CustomCapabilities?,
    val customMessage: CustomMessage?,
)

data class Capabilities(
    val capabilities: Long,
) {
    enum class Capability(
        val value: Long,
    ) {
        Unspecified(0),
        ReportsStatus(1),
        AcceptsRemoteConfig(2),
        ReportsEffectiveConfig(4),
        AcceptsPackages(8),
        ReportsPackageStatuses(16),
        ReportsOwnTraces(32),
        ReportsOwnMetrics(64),
        ReportsOwnLogs(128),
        AcceptsOpAMPConnectionSettings(256),
        AcceptsOtherConnectionSettings(512),
        AcceptsRestartCommand(1024),
        ReportsHealth(2048),
        ReportsRemoteConfig(4096),
        ReportsHeartbeat(8192),
    }

    fun hasCapability(capability: Capability): Boolean = capabilities and capability.value != 0L

    fun toSet(): Set<Capability> = Capability.entries.filter { hasCapability(it) }.toSet()
}

interface DisconnectUsecase {
    suspend fun disconnect(request: AgentDisconnectRequest)
}

data class AgentDisconnectRequest(
    val instanceUid: UUID,
)

data class CustomMessage(
    val capability: String,
    val type: String,
    val data: ByteArray,
)
