package dev.minuk.opampcommander.domain.port.secondary.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.domain.models.agent.Agent
import kotlinx.coroutines.flow.Flow

interface AgentOperationsPort :
    GetAgentOperation,
    GetAgentsOperation,
    SaveAgentOperation

interface GetAgentOperation {
    suspend fun getAgentByInstanceUid(instanceUid: Ulid): Agent?
}

interface GetAgentsOperation {
    suspend fun getAgents(): Flow<Agent>
}

interface SaveAgentOperation {
    suspend fun saveAgent(agent: Agent): Agent
}
