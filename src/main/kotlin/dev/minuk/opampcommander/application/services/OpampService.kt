package dev.minuk.opampcommander.application.services

import com.github.f4b6a3.ulid.Ulid
import com.google.protobuf.ByteString
import dev.minuk.opampcommander.application.services.mapper.toDomain
import dev.minuk.opampcommander.application.services.mapper.toProto
import dev.minuk.opampcommander.application.usecases.FetchServerToAgentUsecase
import dev.minuk.opampcommander.application.usecases.HandleAgentToServerUsecase
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.models.agent.AgentCapabilities
import dev.minuk.opampcommander.domain.models.agent.CommunicationStatus
import dev.minuk.opampcommander.domain.models.command.CommandKind
import dev.minuk.opampcommander.domain.models.command.RawCommand
import dev.minuk.opampcommander.domain.models.command.ServerToAgent
import dev.minuk.opampcommander.domain.models.command.ServerToAgentFlags
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.GetCommandInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.UpsertAgentInternalUsecase
import dev.minuk.opampcommander.util.Logger
import kotlinx.coroutines.flow.fold
import opamp.proto.Opamp
import org.springframework.stereotype.Service

@Service
class OpampService(
    private val getAgentInternalUsecase: GetAgentInternalUsecase,
    private val upsertAgentInternalUsecase: UpsertAgentInternalUsecase,
    private val getCommandInternalUsecase: GetCommandInternalUsecase,
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
                reportedCapabilities = agentToServer.capabilities.toDomain(),
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
                reportedCommunicationStatus = CommunicationStatus(
                    sequenceNum = agentToServer.sequenceNum,
                ),
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
        val exist = getAgentInternalUsecase.existsAgent(instanceUid)
        if (!exist) {
            return Opamp.ServerToAgent
                .newBuilder()
                .setInstanceUid(ByteString.copyFrom(instanceUid.toBytes()))
                .setErrorResponse(
                    Opamp.ServerErrorResponse
                        .newBuilder()
                        .setErrorMessage("Agent not found")
                        .build(),
                ).build()
        }

        val commands = getCommandInternalUsecase.getCommands(instanceUid)
        val serverToAgent =
            commands.fold(ServerToAgent(instanceUid), { serverToAgent: ServerToAgent, command: RawCommand ->
                if (command.kind == CommandKind.ReportFullState) {
                    serverToAgent.copy(
                        flags = ServerToAgentFlags(ServerToAgentFlags.Flag.ReportFullState) + serverToAgent.flags,
                    )
                } else {
                    serverToAgent
                }
            })

        return serverToAgent.toProto()
    }
}

