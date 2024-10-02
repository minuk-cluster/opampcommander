package dev.minuk.otelcommander.domain.port.secondary.agent

import dev.minuk.otelcommander.domain.models.agent.Agent
import kotlinx.coroutines.flow.Flow

interface AgentOperationsPort :
    GetAgentOperation,
    GetAgentsOperation,
    SaveAgentOperation

interface GetAgentOperation {
    suspend fun getAgentByInstanceUid(instanceUid: String): Agent
}

interface GetAgentsOperation {
    suspend fun getAgents(): Flow<Agent>
}

interface SaveAgentOperation {
    suspend fun saveAgent(agent: Agent): Agent
}
