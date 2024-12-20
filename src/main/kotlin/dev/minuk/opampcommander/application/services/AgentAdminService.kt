package dev.minuk.opampcommander.application.services

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.application.usecases.AdminAgentUsecase
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.port.primary.agent.CountAgentsInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentsInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentsRequest
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AgentAdminService(
    val getAgentInternalUsecase: GetAgentInternalUsecase,
    val getAgentsInternalUsecase: GetAgentsInternalUsecase,
    val countAgentsInternalUsecase: CountAgentsInternalUsecase,
) : AdminAgentUsecase {
    /**
     * Get an agent by instanceUid.
     */
    override suspend fun getAgentByInstanceUid(instanceUid: Ulid): Agent? = getAgentInternalUsecase.getAgent(instanceUid)

    /**
     * Get an agent by instanceUid.
     */
    override suspend fun getAgentByInstanceUid(instanceUid: UUID): Agent? = getAgentInternalUsecase.getAgent(Ulid.from(instanceUid))

    /**
     * Get agents.
     */
    override suspend fun getAgents(request: GetAgentsRequest): Flow<Agent> = getAgentsInternalUsecase.getAgents(request = request)

    /**
     * Get total number of agents
     */
    override suspend fun getTotalAgents(): Long = countAgentsInternalUsecase.getTotalAgents()
}
