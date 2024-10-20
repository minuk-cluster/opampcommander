package dev.minuk.opampcommander.application.usecases

import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentsRequest
import kotlinx.coroutines.flow.Flow

/**
 * The AdminAgentUsecase interface.
 */
interface AdminAgentUsecase {
    /**
     * Get an agent by instanceUid.
     *
     * @param instanceUid the instanceUid of the agent.
     * @return the agent if found, null otherwise.
     */
    suspend fun getAgentByInstanceUid(instanceUid: String): Agent?

    /**
     * Get agents.
     *
     * @param request the request.
     * @return the agents.
     */
    suspend fun getAgents(request: GetAgentsRequest): Flow<Agent>

    /**
     * Get total number of agents
     */
    suspend fun getTotalAgents(): Long
}
