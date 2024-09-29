package dev.minuk.otelcommander.adapter.`in`.http.v1.opamp.mapper

import com.google.protobuf.ByteString
import dev.minuk.otelcommander.application.usecases.AgentDisconnectRequest
import dev.minuk.otelcommander.domain.models.agent.AgentDescription
import dev.minuk.otelcommander.application.usecases.AgentExchangeRequest
import dev.minuk.otelcommander.application.usecases.Capabilities
import dev.minuk.otelcommander.application.usecases.CustomMessage
import dev.minuk.otelcommander.domain.models.agent.AgentConfigMap
import dev.minuk.otelcommander.domain.models.agent.ComponentHealth
import dev.minuk.otelcommander.domain.models.agent.CustomCapabilities
import dev.minuk.otelcommander.domain.models.agent.EffectiveConfig
import dev.minuk.otelcommander.domain.models.agent.PackageStatus
import dev.minuk.otelcommander.domain.models.agent.PackageStatuses
import dev.minuk.otelcommander.domain.models.agent.RemoteConfigStatus
import opamp.proto.Opamp
import java.nio.ByteBuffer
import java.time.Instant
import java.util.UUID

/**
 * OpampMapper is a class
 */
object OpampMapper {
    /**
     * AgentToServer contains:
     *   - AgentDescription
     *   - ComponentHealth
     *   - EffectiveConfig
     *   - RemoteConfigStatus
     *   - PackageStatuses
     *   - CustomCapabilities
     * Please see https://opentelemetry.io/docs/specs/opamp/#agent-status-compression
     *
     */
    fun Opamp.AgentToServer.toAgentExchangeRequest(): AgentExchangeRequest {
        val agentDescription = if (hasAgentDescription()) { agentDescription.toDomain() } else { null }
        val componentHealth = if (hasHealth()) { health.toDomain() } else { null }
        val effectiveConfig = if (hasEffectiveConfig()) { effectiveConfig.toDomain() } else { null }
        val remoteConfigStatus = if (hasRemoteConfigStatus()) { remoteConfigStatus.toDomain() } else { null }
        val packageStatuses = if (hasPackageStatuses()) { packageStatuses.toDomain() } else { null }
        val customCapabilities = if (hasCustomCapabilities()) { customCapabilities.toDomain() } else { null }
        val customMessage = if (hasCustomMessage()) { customMessage.toDomain() } else { null }

        return AgentExchangeRequest(
            instanceUid = instanceUid.toUUID(),
            sequenceNum = sequenceNum,
            capabilities = Capabilities(capabilities),
            agentDescription = agentDescription,
            componentHealth = componentHealth,
            effectiveConfig = effectiveConfig,
            remoteConfigStatus = remoteConfigStatus,
            packageStatuses = packageStatuses,
            customCapabilities = customCapabilities,
            customMessage = customMessage,
        )
    }

    fun Opamp.AgentToServer.toAgentDisconnectRequest(): AgentDisconnectRequest {
        return AgentDisconnectRequest(
            instanceUid = instanceUid.toUUID()
        )
    }

    private fun Opamp.ComponentHealth.toDomain(): ComponentHealth {
        return ComponentHealth(
            healthy = healthy,
            startedAt = Instant.ofEpochSecond(0L, startTimeUnixNano),
            lastError = lastError,
            status = status,
            statusObservedAt = Instant.ofEpochSecond(0, statusTimeUnixNano),
            componentHealthMap = componentHealthMapMap.mapValues { it.value.toDomain() },
        )
    }

    private fun Opamp.AgentDescription.toDomain(): AgentDescription {
        return AgentDescription(
            identifyingAttributes = identifyingAttributesList.associate {
                it.key to it.value.stringValue
            },
            nonIdentifyingAttributes = nonIdentifyingAttributesList.associate {
                it.key to it.value.stringValue
            }
        )
    }

    private fun Opamp.EffectiveConfig.toDomain(): EffectiveConfig {
        return EffectiveConfig(
            configMap = configMap.configMapMap.mapValues {
                AgentConfigMap(
                    body = it.value.body.toString(),
                    contentType = it.value.contentType,
                )
            }
        )
    }

    private fun Opamp.RemoteConfigStatus.toDomain(): RemoteConfigStatus {
        return RemoteConfigStatus(
            lastRemoteConfigHash = lastRemoteConfigHash.toString(),
            status = RemoteConfigStatus.Status.of(statusValue),
            errorMessage = errorMessage,
        )
    }

    private fun Opamp.PackageStatuses.toDomain(): PackageStatuses {
        return PackageStatuses(
            packages = packagesMap.mapValues {
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
    }

    private fun Opamp.CustomCapabilities.toDomain(): CustomCapabilities {
        return CustomCapabilities(
            capabilities = capabilitiesList,
        )
    }

    private fun ByteString.toUUID(): UUID {
        val bb = ByteBuffer.wrap(this.toByteArray())
        return UUID(bb.getLong(), bb.getLong())
    }

    private fun Opamp.CustomMessage.toDomain(): CustomMessage {
        return CustomMessage(
            capability = capability,
            type = type,
            data = data.toByteArray(),
        )
    }

}

