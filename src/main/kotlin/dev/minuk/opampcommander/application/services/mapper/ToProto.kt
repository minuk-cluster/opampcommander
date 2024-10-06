package dev.minuk.opampcommander.application.services.mapper

import com.google.protobuf.ByteString
import dev.minuk.opampcommander.domain.models.command.AgentConfigFile
import dev.minuk.opampcommander.domain.models.command.AgentConfigMap
import dev.minuk.opampcommander.domain.models.command.AgentIdentification
import dev.minuk.opampcommander.domain.models.command.AgentRemoteConfig
import dev.minuk.opampcommander.domain.models.command.ConnectionSettingsOffers
import dev.minuk.opampcommander.domain.models.command.CustomCapabilities
import dev.minuk.opampcommander.domain.models.command.CustomMessage
import dev.minuk.opampcommander.domain.models.command.Headers
import dev.minuk.opampcommander.domain.models.command.OpAMPConnectionSettings
import dev.minuk.opampcommander.domain.models.command.OtherConnectionSettings
import dev.minuk.opampcommander.domain.models.command.PackageAvailable
import dev.minuk.opampcommander.domain.models.command.PackagesAvailable
import dev.minuk.opampcommander.domain.models.command.RetryInfo
import dev.minuk.opampcommander.domain.models.command.ServerCapabilities
import dev.minuk.opampcommander.domain.models.command.ServerErrorResponse
import dev.minuk.opampcommander.domain.models.command.ServerToAgent
import dev.minuk.opampcommander.domain.models.command.ServerToAgentCommand
import dev.minuk.opampcommander.domain.models.command.ServerToAgentFlags
import dev.minuk.opampcommander.domain.models.command.TLSCertificate
import dev.minuk.opampcommander.domain.models.command.TelemetryConnectionSettings
import opamp.proto.Opamp

fun ServerToAgent.toProto(): Opamp.ServerToAgent {
    val builder =
        Opamp.ServerToAgent
            .newBuilder()
            .setInstanceUid(ByteString.copyFrom(instanceUid.toBytes()))
    if (errorResponse != null) {
        builder.setErrorResponse(errorResponse.toProto())
    }
    if (remoteConfig != null) {
        builder.setRemoteConfig(remoteConfig.toProto())
    }
    if (connectionSettings != null) {
        builder.setConnectionSettings(connectionSettings.toProto())
    }
    if (packagesAvailable != null) {
        builder.setPackagesAvailable(packagesAvailable.toProto())
    }
    if (flags != null) {
        builder.setFlags(flags.toProto())
    }
    if (capabilities != null) {
        builder.setCapabilities(capabilities.toProto())
    }
    if (agentIdentification != null) {
        builder.setAgentIdentification(agentIdentification.toProto())
    }
    if (command != null) {
        builder.setCommand(command.toProto())
    }
    if (customCapabilities != null) {
        builder.setCustomCapabilities(customCapabilities.toProto())
    }
    if (customMessage != null) {
        builder.setCustomMessage(customMessage.toProto())
    }
    return builder.build()
}

fun ServerErrorResponse.toProto(): Opamp.ServerErrorResponse.Builder {
    val builder =
        Opamp.ServerErrorResponse
            .newBuilder()
            .setType(Opamp.ServerErrorResponseType.forNumber(type.value))
            .setErrorMessage(errorMessage)
    if (retryInfo != null) {
        builder.setRetryInfo(retryInfo.toProto())
    }
    return builder
}

fun RetryInfo.toProto(): Opamp.RetryInfo.Builder {
    val builder =
        Opamp.RetryInfo
            .newBuilder()
            .setRetryAfterNanoseconds(retryAfter.toNanos())
    return builder
}

fun AgentRemoteConfig.toProto(): Opamp.AgentRemoteConfig.Builder {
    val builder =
        Opamp.AgentRemoteConfig
            .newBuilder()
            .setConfig(config.toProto())
    return builder
}

fun AgentConfigMap.toProto(): Opamp.AgentConfigMap.Builder {
    val builder =
        Opamp.AgentConfigMap
            .newBuilder()
            .putAllConfigMap(
                configMap.mapValues {
                    it.value.toProto()
                },
            )
    return builder
}

fun AgentConfigFile.toProto(): Opamp.AgentConfigFile {
    val builder =
        Opamp.AgentConfigFile
            .newBuilder()
            .setBody(ByteString.copyFrom(body))
            .setContentType(contentType)
    return builder.build()
}

fun ConnectionSettingsOffers.toProto(): Opamp.ConnectionSettingsOffers.Builder {
    val builder =
        Opamp.ConnectionSettingsOffers
            .newBuilder()
            .setHash(ByteString.copyFrom(hash))
            .setOpamp(opamp.toProto())
            .setOwnMetrics(ownMetrics.toProto())
            .setOwnTraces(ownTraces.toProto())
            .setOwnLogs(ownLogs.toProto())
            .putAllOtherConnections(
                otherConnections.mapValues {
                    it.value.toProto()
                },
            )
    return builder
}

fun OpAMPConnectionSettings.toProto(): Opamp.OpAMPConnectionSettings.Builder {
    val builder =
        Opamp.OpAMPConnectionSettings
            .newBuilder()
            .setDestinationEndpoint(destinationEndPoint)
            .setHeaders(headers.toProto())
            .setCertificate(certificate.toProto())
    return builder
}

fun TelemetryConnectionSettings.toProto(): Opamp.TelemetryConnectionSettings.Builder {
    val builder =
        Opamp.TelemetryConnectionSettings
            .newBuilder()
            .setDestinationEndpoint(destinationEndPoint)
            .setHeaders(headers.toProto())
            .setCertificate(certificate.toProto())
    return builder
}

fun OtherConnectionSettings.toProto(): Opamp.OtherConnectionSettings {
    val builder =
        Opamp.OtherConnectionSettings
            .newBuilder()
            .setDestinationEndpoint(destinationEndPoint)
            .setHeaders(headers.toProto())
            .setCertificate(certificate.toProto())
            .putAllOtherSettings(otherSettings)
    return builder.build()
}

fun TLSCertificate.toProto(): Opamp.TLSCertificate.Builder {
    val builder =
        Opamp.TLSCertificate
            .newBuilder()
            .setCert(ByteString.copyFrom(publicKey))
            .setPrivateKey(ByteString.copyFrom(privateKey))
            .setCaCert(ByteString.copyFrom(caPublicKey))
    return builder
}

fun Headers.toProto(): Opamp.Headers.Builder {
    val builder =
        Opamp.Headers
            .newBuilder()
            .addAllHeaders(
                headers.map {
                    Opamp.Header
                        .newBuilder()
                        .setKey(it.key)
                        .setValue(it.value)
                        .build()
                },
            )
    return builder
}

fun PackagesAvailable.toProto(): Opamp.PackagesAvailable.Builder {
    val builder =
        Opamp.PackagesAvailable
            .newBuilder()
            .putAllPackages(
                packages.mapValues {
                    it.value.toProto()
                },
            ).setAllPackagesHash(ByteString.copyFrom(allPackagesHash))
    return builder
}

fun PackageAvailable.toProto(): Opamp.PackageAvailable {
    val builder =
        Opamp.PackageAvailable
            .newBuilder()
            .setVersion(version)
            .setHash(ByteString.copyFrom(hash))
            .setFile(
                Opamp.DownloadableFile
                    .newBuilder()
                    .setHeaders(file.headers.toProto())
                    .setDownloadUrl(file.downloadUrl)
                    .setContentHash(ByteString.copyFrom(file.contentHash))
                    .setSignature(ByteString.copyFrom(file.signature))
                    .build(),
            )
    return builder.build()
}

fun ServerToAgentFlags.toProto(): Long =
    flags.fold(0L) { acc, flag ->
        acc or flag.value.toLong()
    }

fun ServerCapabilities.toProto(): Long =
    capabilities.fold(0L) { acc, capability ->
        acc or capability.value.toLong()
    }

fun AgentIdentification.toProto(): Opamp.AgentIdentification.Builder {
    val builder =
        Opamp.AgentIdentification
            .newBuilder()
            .setNewInstanceUid(ByteString.copyFrom(newInstanceUid.toBytes()))
    return builder
}

fun ServerToAgentCommand.toProto(): Opamp.ServerToAgentCommand.Builder {
    val builder =
        Opamp.ServerToAgentCommand
            .newBuilder()
            .setTypeValue(type.value)
    return builder
}

fun CustomCapabilities.toProto(): Opamp.CustomCapabilities.Builder {
    val builder =
        Opamp.CustomCapabilities
            .newBuilder()
            .addAllCapabilities(capabilities)
    return builder
}

fun CustomMessage.toProto(): Opamp.CustomMessage =
    Opamp.CustomMessage
        .newBuilder()
        .setCapability(capability)
        .setType(type)
        .setData(ByteString.copyFrom(data))
        .build()
