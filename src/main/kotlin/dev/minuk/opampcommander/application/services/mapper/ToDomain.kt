package dev.minuk.opampcommander.application.services.mapper

import dev.minuk.opampcommander.domain.models.agent.AgentConfigFile
import dev.minuk.opampcommander.domain.models.agent.AgentConfigMap
import dev.minuk.opampcommander.domain.models.agent.AgentDescription
import dev.minuk.opampcommander.domain.models.agent.ComponentHealth
import dev.minuk.opampcommander.domain.models.agent.CustomCapabilities
import dev.minuk.opampcommander.domain.models.agent.CustomMessage
import dev.minuk.opampcommander.domain.models.agent.EffectiveConfig
import dev.minuk.opampcommander.domain.models.agent.PackageStatus
import dev.minuk.opampcommander.domain.models.agent.PackageStatuses
import dev.minuk.opampcommander.domain.models.agent.RemoteConfigStatus
import opamp.proto.Opamp
import java.time.Instant

fun Opamp.ComponentHealth.toDomain(): ComponentHealth =
    ComponentHealth(
        healthy = healthy,
        startedAt = Instant.ofEpochSecond(0L, startTimeUnixNano),
        lastError = lastError,
        status = status,
        statusObservedAt = Instant.ofEpochSecond(0, statusTimeUnixNano),
        componentHealthMap = componentHealthMapMap.mapValues { it.value.toDomain() },
    )

fun Opamp.AgentDescription.toDomain(): AgentDescription =
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

fun Opamp.EffectiveConfig.toDomain(): EffectiveConfig =
    EffectiveConfig(
        configMap = configMap.toDomain(),
    )

fun Opamp.AgentConfigMap.toDomain(): AgentConfigMap =
    AgentConfigMap(
        configMap =
            configMapMap.mapValues {
                AgentConfigFile(
                    body = it.value.body.toByteArray(),
                    contentType = it.value.contentType,
                )
            },
    )

fun Opamp.RemoteConfigStatus.toDomain(): RemoteConfigStatus =
    RemoteConfigStatus(
        lastRemoteConfigHash = lastRemoteConfigHash.toString(),
        status =
            RemoteConfigStatus.Status.of(
                statusValue,
            ),
        errorMessage = errorMessage,
    )

fun Opamp.PackageStatuses.toDomain(): PackageStatuses =
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

fun Opamp.CustomCapabilities.toDomain(): CustomCapabilities =
    CustomCapabilities(
        capabilities = capabilitiesList,
    )

fun Opamp.CustomMessage.toDomain(): CustomMessage =
    CustomMessage(
        capability = capability,
        type = type,
        data = data.toByteArray(),
    )
