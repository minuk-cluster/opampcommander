package dev.minuk.opampcommander.domain.models.agent

import com.github.f4b6a3.ulid.Ulid
import opamp.proto.Opamp.DownloadableFile
import java.time.Duration

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

    fun applyRemoteConfig(newRemoteConfig: AgentRemoteConfig) {
        TODO("Not yet implemented")
    }

    fun toServerToAgent(): ServerToAgent =
        ServerToAgent(
            instanceUid = instanceUid,
            errorResponse = null,
            remoteConfig = null,
            connectionSettings = null,
            packagesAvailable = null,
            flags = null,
            capabilities = null,
            agentIdentification = null,
            command = null,
            customCapabilities = null,
            customMessage = null,
        )
}

data class AgentRemoteConfig(
    val config: AgentConfigMap,
    val configHash: ByteArray,
)

data class ServerToAgent(
    val instanceUid: Ulid,
    val errorResponse: ServerErrorResponse?,
    val remoteConfig: AgentRemoteConfig?,
    val connectionSettings: ConnectionSettingsOffers?,
    val packagesAvailable: PackagesAvailable?,
    val flags: ServerToAgentFlags?,
    val capabilities: ServerCapabilities?,
    val agentIdentification: AgentIdentification?,
    val command: ServerToAgentCommand?,
    val customCapabilities: CustomCapabilities?,
    val customMessage: CustomMessage?,
)

data class PackagesAvailable(
    val packages: Map<String, PackageAvailable>,
    val allPackagesHash: ByteArray,
)

data class PackageAvailable(
    val type: PackageType,
    val version: String,
    val file: DownloadableFile,
    val hash: ByteArray,
) {
    enum class PackageType(
        val value: Int,
    ) {
        TopLevelPackage(0),
        AddonPackage(1),
        ;

        companion object {
            fun of(value: Int): PackageType = entries.find { it.value == value }!!
        }
    }
}

data class ServerErrorResponse(
    val type: Type,
    val errorMessage: String,
    val retryInfo: RetryInfo?,
) {
    enum class Type(
        val value: Int,
    ) {
        UNKNOWN(0),
        BAD_REQUEST(1),
        UNAUTHORIZED(2),
        ;

        companion object {
            fun of(value: Int): Type = entries.find { it.value == value }!!
        }
    }
}

data class RetryInfo(
    val retryAfter: Duration,
)

data class ConnectionSettingsOffers(
    val hash: ByteArray,
    val opamp: OpAMPConnectionSettings,
    val ownMetrics: TelemetryConnectionSettings,
    val ownTraces: TelemetryConnectionSettings,
    val ownLogs: TelemetryConnectionSettings,
    val otherConnections: Map<String, OtherConnectionSettings>,
)

data class OpAMPConnectionSettings(
    val destinationEndPoint: String,
    val headers: Headers,
    val certificate: TLSCertificate,
)

data class Headers(
    val headers: List<Header>,
)

data class Header(
    val key: String,
    val value: String,
)

data class TLSCertificate(
    val publicKey: ByteArray,
    val privateKey: ByteArray,
    val caPublicKey: ByteArray,
)

data class TelemetryConnectionSettings(
    val destinationEndPoint: String,
    val headers: Headers,
    val certificate: TLSCertificate,
)

data class OtherConnectionSettings(
    val destinationEndPoint: String,
    val headers: Headers,
    val certificate: TLSCertificate,
    val otherSettings: Map<String, String>,
)

data class ServerToAgentFlags(
    val flags: Set<Flag>,
) {
    enum class Flag(
        val value: Int,
    ) {
        FlagUnspecified(0),
        ReportFullState(1),
        ;

        companion object {
            fun of(value: Int): Flag = entries.find { it.value == value }!!
        }
    }
}

data class ServerCapabilities(
    val capabilities: Set<Capability>,
) {
    enum class Capability(
        val value: Int,
    ) {
        UnspecifiedServerCapability(0),
        AcceptsStatus(0x00000001),
        OffersRemoteConfig(0x00000002),
        AcceptsEffectiveConfig(0x00000004),
        OffersPackages(0x00000008),
        AcceptsPackagesStatus(0x00000010),
        OffersConnectionSettings(0x00000020),
        AcceptsConnectionSettingsRequest(0x00000040),
        ;

        companion object {
            fun of(value: Int): Capability = entries.find { it.value == value }!!
        }
    }
}

data class AgentIdentification(
    val newInstanceUid: Ulid,
)

data class ServerToAgentCommand(
    val type: CommandType,
) {
    enum class CommandType(
        val value: Int,
    ) {
        Restart(0),
        ;

        companion object {
            fun of(value: Int): CommandType = entries.find { it.value == value }!!
        }
    }
}
