package dev.minuk.opampcommander.domain.services

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.port.primary.agent.CountAgentsInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentsInternalUsecase
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentsRequest
import dev.minuk.opampcommander.domain.port.primary.agent.UpsertAgentInternalUsecase
import dev.minuk.opampcommander.domain.port.secondary.agent.AgentOperationsPort
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class AgentService(
    val agentOperationsPort: AgentOperationsPort,
) : GetAgentInternalUsecase,
    GetAgentsInternalUsecase,
    CountAgentsInternalUsecase,
    UpsertAgentInternalUsecase {
    override suspend fun getAgent(instanceUid: Ulid): Agent? = agentOperationsPort.getAgentByInstanceUid(instanceUid)

    override suspend fun upsertAgent(agent: Agent): Agent = agentOperationsPort.saveAgent(agent)

    override suspend fun getTotalAgents(): Long = agentOperationsPort.countAgents()

    override suspend fun getAgents(request: GetAgentsRequest): Flow<Agent> {
        val pivot = request.pivot.ifEmpty { null }
        val limit = request.limit
        val sort = request.sort

        return agentOperationsPort.getAgents(
            pivot = pivot,
            limit = limit,
            sort = sort,
        )
    }
}
