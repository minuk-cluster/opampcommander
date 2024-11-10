package dev.minuk.opampcommander.domain.port.secondary.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.domain.models.Sort
import dev.minuk.opampcommander.domain.models.agent.Agent
import kotlinx.coroutines.flow.Flow

interface AgentOperationsPort :
    GetAgentOperation,
    GetAgentsOperation,
    SaveAgentOperation,
    CountAgentsOperation

interface GetAgentOperation {
    suspend fun getAgentByInstanceUid(instanceUid: Ulid): Agent?
}

/**
 * Get agents.
 *
 * @param pivot the pivot. If null, the first page is returned.
 * @param limit the limit.
 * @param sort the sort.
 */
interface GetAgentsOperation {
    suspend fun getAgents(
        pivot: String?,
        limit: Int,
        sort: Sort,
    ): Flow<Agent>
}

interface SaveAgentOperation {
    suspend fun saveAgent(agent: Agent): Agent
}

interface CountAgentsOperation {
    suspend fun countAgents(): Long
}
