package dev.minuk.opampcommander.domain.models.command

import com.github.f4b6a3.ulid.Ulid
import java.time.Duration

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
) {
    constructor(instanceUid: Ulid) : this(
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

data class DownloadableFile(
    val downloadUrl: String,
    val contentHash: ByteArray,
    val signature: ByteArray,
    val headers: Headers,
)

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
    constructor() : this(emptySet())

    constructor(vararg initialFlags: Flag) : this(initialFlags.toSet())

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

    fun turnOn(flag: Flag): ServerToAgentFlags = copy(flags = flags + flag)

    operator fun plus(other: ServerToAgentFlags?): ServerToAgentFlags = ServerToAgentFlags(flags + (other?.flags ?: emptySet()))
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
