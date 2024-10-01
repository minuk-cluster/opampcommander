package dev.minuk.otelcommander.application.usecases

import dev.minuk.otelcommander.domain.models.agent.Agent
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentsRequest
import kotlinx.coroutines.flow.Flow

interface AgentAdminUsecase {
    suspend fun getAgentByInstanceUid(instanceUid: String): Agent

    suspend fun getAgents(request: GetAgentsRequest): Flow<Agent>
}
