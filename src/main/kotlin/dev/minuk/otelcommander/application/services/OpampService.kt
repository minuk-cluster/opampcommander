package dev.minuk.otelcommander.application.services

import dev.minuk.otelcommander.application.usecases.AgentDisconnectRequest
import dev.minuk.otelcommander.application.usecases.AgentExchangeRequest
import dev.minuk.otelcommander.application.usecases.DisconnectUsecase
import dev.minuk.otelcommander.application.usecases.ExchangeUsecase
import dev.minuk.otelcommander.domain.models.agent.Agent
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentInternalUsecase
import dev.minuk.otelcommander.domain.port.primary.agent.UpsertAgentInternalUsecase
import dev.minuk.otelcommander.util.Logger
import org.springframework.stereotype.Service

@Service
class OpampService(
    private val getAgentInternalUsecase: GetAgentInternalUsecase,
    private val upsertAgentInternalUsecase: UpsertAgentInternalUsecase,
) : ExchangeUsecase,
    DisconnectUsecase {
    companion object {
        val log by Logger()
    }

    override suspend fun exchange(request: AgentExchangeRequest) {
        log.info("Received agent exchange request for instanceUid: ${request.instanceUid}")
        log.info("request: $request")
        val agent = getAgentInternalUsecase.getAgent(instanceUid = request.instanceUid) ?: Agent(instanceUid = request.instanceUid)
        val updatedAgent =
            agent.update(
                newAgentDescription = request.agentDescription,
                newComponentHealth = request.componentHealth,
                newEffectiveConfig = request.effectiveConfig,
                newPackageStatuses = request.packageStatuses,
                newCustomCapabilities = request.customCapabilities,
            )
        upsertAgentInternalUsecase.upsertAgent(updatedAgent)
    }

    override suspend fun disconnect(request: AgentDisconnectRequest) {
        TODO("Not yet implemented")
    }
}
