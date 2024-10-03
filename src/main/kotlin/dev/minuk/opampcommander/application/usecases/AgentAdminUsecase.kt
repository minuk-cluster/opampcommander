package dev.minuk.opampcommander.application.usecases

import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentsRequest
import kotlinx.coroutines.flow.Flow

interface AgentAdminUsecase {
    suspend fun getAgentByInstanceUid(instanceUid: String): Agent

    suspend fun getAgents(request: GetAgentsRequest): Flow<Agent>
}
