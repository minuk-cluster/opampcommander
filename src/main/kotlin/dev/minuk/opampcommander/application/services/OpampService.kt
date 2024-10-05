package dev.minuk.opampcommander.application.services

import com.github.f4b6a3.ulid.Ulid
import com.google.protobuf.ByteString
import dev.minuk.opampcommander.application.usecases.FetchServerToAgentUsecase
import dev.minuk.opampcommander.application.usecases.HandleAgentToServerUsecase
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.models.agent.AgentConfigFile
import dev.minuk.opampcommander.domain.models.agent.AgentConfigMap
import dev.minuk.opampcommander.domain.models.agent.AgentDescription
import dev.minuk.opampcommander.domain.models.agent.AgentIdentification
import dev.minuk.opampcommander.domain.models.agent.AgentRemoteConfig
import dev.minuk.opampcommander.domain.models.agent.ComponentHealth
import dev.minuk.opampcommander.domain.models.agent.ConnectionSettingsOffers
import dev.minuk.opampcommander.domain.models.agent.CustomCapabilities
import dev.minuk.opampcommander.domain.models.agent.CustomMessage
import dev.minuk.opampcommander.domain.models.agent.EffectiveConfig
import dev.minuk.opampcommander.domain.models.agent.Headers
import dev.minuk.opampcommander.domain.models.agent.OpAMPConnectionSettings
import dev.minuk.opampcommander.domain.models.agent.OtherConnectionSettings
import dev.minuk.opampcommander.domain.models.agent.PackageAvailable
import dev.minuk.opampcommander.domain.models.agent.PackageStatus
import dev.minuk.opampcommander.domain.models.agent.PackageStatuses
import dev.minuk.opampcommander.domain.models.agent.PackagesAvailable
import dev.minuk.opampcommander.domain.models.agent.RemoteConfigStatus
import dev.minuk.opampcommander.domain.models.agent.RetryInfo
import dev.minuk.opampcommander.domain.models.agent.ServerCapabilities
import dev.minuk.opampcommander.domain.models.agent.ServerErrorResponse
import dev.minuk.opampcommander.domain.models.agent.ServerToAgentCommand
import dev.minuk.opampcommander.domain.models.agent.ServerToAgentFlags
import dev.minuk.opampcommander.domain.models.agent.TLSCertificate
import dev.minuk.opampcommander.domain.models.agent.TelemetryConnectionSettings
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.UpsertAgentInternalUsecase
import dev.minuk.opampcommander.util.Logger
import opamp.proto.Opamp
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class OpampService(
    private val getAgentInternalUsecase: GetAgentInternalUsecase,
    private val upsertAgentInternalUsecase: UpsertAgentInternalUsecase,
) : HandleAgentToServerUsecase,
    FetchServerToAgentUsecase {
    companion object {
        val log by Logger()
    }

    override suspend fun handleAgentToServer(agentToServer: Opamp.AgentToServer) {
        val instanceUid = Ulid.from(agentToServer.instanceUid.toByteArray())
        log.info("Received agentToServer message for instanceUid: $instanceUid")

        val agent = getAgentInternalUsecase.getAgent(instanceUid = instanceUid) ?: Agent(instanceUid = instanceUid)
        val updatedAgent =
            agent.report(
                reportedAgentDescription =
                    if (agentToServer.hasAgentDescription()) {
                        agentToServer.agentDescription.toDomain()
                    } else {
                        null
                    },
                reportedComponentHealth =
                    if (agentToServer.hasHealth()) {
                        agentToServer.health.toDomain()
                    } else {
                        null
                    },
                reportedEffectiveConfig =
                    if (agentToServer.hasEffectiveConfig()) {
                        agentToServer.effectiveConfig.toDomain()
                    } else {
                        null
                    },
                reportedPackageStatuses =
                    if (agentToServer.hasPackageStatuses()) {
                        agentToServer.packageStatuses.toDomain()
                    } else {
                        null
                    },
                reportedCustomCapabilities =
                    if (agentToServer.hasCustomCapabilities()) {
                        agentToServer.customCapabilities.toDomain()
                    } else {
                        null
                    },
            )
        upsertAgentInternalUsecase.upsertAgent(updatedAgent)
    }

    override suspend fun fetchServerToAgent(instanceUid: Ulid): Opamp.ServerToAgent {
        val agent =
            getAgentInternalUsecase.getAgent(instanceUid)
                ?: return Opamp.ServerToAgent
                    .newBuilder()
                    .setInstanceUid(ByteString.copyFrom(instanceUid.toBytes()))
                    .setErrorResponse(
                        Opamp.ServerErrorResponse
                            .newBuilder()
                            .setErrorMessage("Agent not found")
                            .build(),
                    ).build()
        val serverToAgent = agent.toServerToAgent()

        return serverToAgent.let {
            val builder =
                Opamp.ServerToAgent
                    .newBuilder()
                    .setInstanceUid(ByteString.copyFrom(it.instanceUid.toBytes()))
            if (it.errorResponse != null) {
                builder.setErrorResponse(it.errorResponse.toProto())
            }
            if (it.remoteConfig != null) {
                builder.setRemoteConfig(it.remoteConfig.toProto())
            }
            if (it.connectionSettings != null) {
                builder.setConnectionSettings(it.connectionSettings.toProto())
            }
            if (it.packagesAvailable != null) {
                builder.setPackagesAvailable(it.packagesAvailable.toProto())
            }
            if (it.flags != null) {
                builder.setFlags(it.flags.toProto())
            }
            if (it.capabilities != null) {
                builder.setCapabilities(it.capabilities.toProto())
            }
            if (it.agentIdentification != null) {
                builder.setAgentIdentification(it.agentIdentification.toProto())
            }
            if (it.command != null) {
                builder.setCommand(it.command.toProto())
            }
            if (it.customCapabilities != null) {
                builder.setCustomCapabilities(it.customCapabilities.toProto())
            }
            if (it.customMessage != null) {
                builder.setCustomMessage(it.customMessage.toProto())
            }
            builder.build()
        }
    }

    private fun Opamp.ComponentHealth.toDomain(): ComponentHealth =
        ComponentHealth(
            healthy = healthy,
            startedAt = Instant.ofEpochSecond(0L, startTimeUnixNano),
            lastError = lastError,
            status = status,
            statusObservedAt = Instant.ofEpochSecond(0, statusTimeUnixNano),
            componentHealthMap = componentHealthMapMap.mapValues { it.value.toDomain() },
        )

    private fun Opamp.AgentDescription.toDomain(): AgentDescription =
        AgentDescription(
            identifyingAttributes =
                identifyingAttributesList.associate {
                    it.key to it.value.stringValue
                },
            nonIdentifyingAttributes =
                nonIdentifyingAttributesList.associate {
                    it.key to it.value.stringValue
                },
        )

    private fun Opamp.EffectiveConfig.toDomain(): EffectiveConfig =
        EffectiveConfig(
            configMap = configMap.toDomain(),
        )

    private fun Opamp.AgentConfigMap.toDomain(): AgentConfigMap =
        AgentConfigMap(
            configMap =
                configMapMap.mapValues {
                    AgentConfigFile(
                        body = it.value.body.toByteArray(),
                        contentType = it.value.contentType,
                    )
                },
        )

    private fun Opamp.RemoteConfigStatus.toDomain(): RemoteConfigStatus =
        RemoteConfigStatus(
            lastRemoteConfigHash = lastRemoteConfigHash.toString(),
            status =
                RemoteConfigStatus.Status.of(
                    statusValue,
                ),
            errorMessage = errorMessage,
        )

    private fun Opamp.PackageStatuses.toDomain(): PackageStatuses =
        PackageStatuses(
            packages =
                packagesMap.mapValues {
                    PackageStatus(
                        name = it.value.name,
                        agentHasVersion = it.value.agentHasVersion,
                        agentHasHash = it.value.agentHasHash.toString(),
                        serverOfferedVersion = it.value.serverOfferedVersion,
                        serverOfferedHash = it.value.serverOfferedHash.toString(),
                        status = PackageStatus.Status.of(it.value.statusValue),
                        errorMessage = it.value.errorMessage,
                    )
                },
            serverProvidedAllPackagesHash = serverProvidedAllPackagesHash.toString(),
            errorMessage = errorMessage,
        )

    private fun Opamp.CustomCapabilities.toDomain(): CustomCapabilities =
        CustomCapabilities(
            capabilities = capabilitiesList,
        )

    private fun Opamp.CustomMessage.toDomain(): CustomMessage =
        CustomMessage(
            capability = capability,
            type = type,
            data = data.toByteArray(),
        )

    private fun ServerErrorResponse.toProto(): Opamp.ServerErrorResponse.Builder {
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

    private fun RetryInfo.toProto(): Opamp.RetryInfo.Builder {
        val builder =
            Opamp.RetryInfo
                .newBuilder()
                .setRetryAfterNanoseconds(retryAfter.toNanos())
        return builder
    }

    private fun AgentRemoteConfig.toProto(): Opamp.AgentRemoteConfig.Builder {
        val builder =
            Opamp.AgentRemoteConfig
                .newBuilder()
                .setConfig(config.toProto())
        return builder
    }

    private fun AgentConfigMap.toProto(): Opamp.AgentConfigMap.Builder {
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

    private fun AgentConfigFile.toProto(): Opamp.AgentConfigFile {
        val builder =
            Opamp.AgentConfigFile
                .newBuilder()
                .setBody(ByteString.copyFrom(body))
                .setContentType(contentType)
        return builder.build()
    }

    private fun ConnectionSettingsOffers.toProto(): Opamp.ConnectionSettingsOffers.Builder {
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

    private fun OpAMPConnectionSettings.toProto(): Opamp.OpAMPConnectionSettings.Builder {
        val builder =
            Opamp.OpAMPConnectionSettings
                .newBuilder()
                .setDestinationEndpoint(destinationEndPoint)
                .setHeaders(headers.toProto())
                .setCertificate(certificate.toProto())
        return builder
    }

    private fun TelemetryConnectionSettings.toProto(): Opamp.TelemetryConnectionSettings.Builder {
        val builder =
            Opamp.TelemetryConnectionSettings
                .newBuilder()
                .setDestinationEndpoint(destinationEndPoint)
                .setHeaders(headers.toProto())
                .setCertificate(certificate.toProto())
        return builder
    }

    private fun OtherConnectionSettings.toProto(): Opamp.OtherConnectionSettings {
        val builder =
            Opamp.OtherConnectionSettings
                .newBuilder()
                .setDestinationEndpoint(destinationEndPoint)
                .setHeaders(headers.toProto())
                .setCertificate(certificate.toProto())
                .putAllOtherSettings(otherSettings)
        return builder.build()
    }

    private fun TLSCertificate.toProto(): Opamp.TLSCertificate.Builder {
        val builder =
            Opamp.TLSCertificate
                .newBuilder()
                .setCert(ByteString.copyFrom(publicKey))
                .setPrivateKey(ByteString.copyFrom(privateKey))
                .setCaCert(ByteString.copyFrom(caPublicKey))
        return builder
    }

    private fun Headers.toProto(): Opamp.Headers.Builder {
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

    private fun PackagesAvailable.toProto(): Opamp.PackagesAvailable.Builder {
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

    private fun PackageAvailable.toProto(): Opamp.PackageAvailable {
        val builder =
            Opamp.PackageAvailable
                .newBuilder()
                .setVersion(version)
                .setHash(ByteString.copyFrom(hash))
        return builder.build()
    }

    private fun ServerToAgentFlags.toProto(): Long =
        flags.fold(0L) { acc, flag ->
            acc or flag.value.toLong()
        }

    private fun ServerCapabilities.toProto(): Long =
        capabilities.fold(0L) { acc, capability ->
            acc or capability.value.toLong()
        }

    private fun AgentIdentification.toProto(): Opamp.AgentIdentification.Builder {
        val builder =
            Opamp.AgentIdentification
                .newBuilder()
                .setNewInstanceUid(ByteString.copyFrom(newInstanceUid.toBytes()))
        return builder
    }

    private fun ServerToAgentCommand.toProto(): Opamp.ServerToAgentCommand.Builder {
        val builder =
            Opamp.ServerToAgentCommand
                .newBuilder()
                .setTypeValue(type.value)
        return builder
    }

    private fun CustomCapabilities.toProto(): Opamp.CustomCapabilities.Builder {
        val builder =
            Opamp.CustomCapabilities
                .newBuilder()
                .addAllCapabilities(capabilities)
        return builder
    }

    private fun CustomMessage.toProto(): Opamp.CustomMessage =
        Opamp.CustomMessage
            .newBuilder()
            .setCapability(capability)
            .setType(type)
            .setData(ByteString.copyFrom(data))
            .build()
}
