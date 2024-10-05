package dev.minuk.opampcommander.application.services

import com.github.f4b6a3.ulid.Ulid
import com.google.protobuf.ByteString
import dev.minuk.opampcommander.application.usecases.FetchServerToAgentUsecase
import dev.minuk.opampcommander.application.usecases.HandleAgentToServerUsecase
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.models.agent.AgentDescription
import dev.minuk.opampcommander.domain.models.agent.ComponentHealth
import dev.minuk.opampcommander.domain.models.agent.CustomCapabilities
import dev.minuk.opampcommander.domain.models.agent.CustomMessage
import dev.minuk.opampcommander.domain.models.agent.EffectiveConfig
import dev.minuk.opampcommander.domain.models.agent.PackageStatus
import dev.minuk.opampcommander.domain.models.agent.PackageStatuses
import dev.minuk.opampcommander.domain.models.agent.RemoteConfigStatus
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
                newAgentDescription =
                    if (agentToServer.hasAgentDescription()) {
                        agentToServer.agentDescription.toDomain()
                    } else {
                        null
                    },
                newComponentHealth =
                    if (agentToServer.hasHealth()) {
                        agentToServer.health.toDomain()
                    } else {
                        null
                    },
                newEffectiveConfig =
                    if (agentToServer.hasEffectiveConfig()) {
                        agentToServer.effectiveConfig.toDomain()
                    } else {
                        null
                    },
                newPackageStatuses =
                    if (agentToServer.hasPackageStatuses()) {
                        agentToServer.packageStatuses.toDomain()
                    } else {
                        null
                    },
                newCustomCapabilities =
                    if (agentToServer.hasCustomCapabilities()) {
                        agentToServer.customCapabilities.toDomain()
                    } else {
                        null
                    },
            )
        upsertAgentInternalUsecase.upsertAgent(updatedAgent)
    }

    override suspend fun fetchServerToAgent(instanceUid: Ulid): Opamp.ServerToAgent? =
        Opamp.ServerToAgent
            .newBuilder()
            .setInstanceUid(ByteString.copyFrom(instanceUid.toBytes()))
            .build()

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
            configMap =
                configMap.configMapMap.mapValues {
                    EffectiveConfig.AgentConfigMap(
                        body = it.value.body.toString(),
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
}
