package dev.minuk.opampcommander.application.usecases

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentsRequest
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * The AdminAgentUsecase interface.
 */
interface AdminAgentUsecase {
    /**
     * Get an agent by instanceUid.
     *
     * @param instanceUid the instanceUid of the agent.
     *        instanceUid is a ULID string.
     * @return the agent if found, null otherwise.
     */
    suspend fun getAgentByInstanceUid(instanceUid: Ulid): Agent?

    /**
     * Get an agent by instanceUid.
     *
     * @param instanceUid the instanceUid of the agent.
     *       instanceUid is a UUID.
     * @return the agent if found, null otherwise.
     */
    suspend fun getAgentByInstanceUid(instanceUid: UUID): Agent?

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
