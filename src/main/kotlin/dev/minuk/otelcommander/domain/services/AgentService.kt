package dev.minuk.otelcommander.domain.services

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.otelcommander.domain.models.agent.Agent
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentInternalUsecase
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentsInternalUsecase
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentsRequest
import dev.minuk.otelcommander.domain.port.primary.agent.UpsertAgentInternalUsecase
import dev.minuk.otelcommander.domain.port.secondary.agent.AgentOperationsPort
import dev.minuk.otelcommander.util.Logger
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class AgentService(
    val agentOperationsPort: AgentOperationsPort,
) : GetAgentInternalUsecase,
    GetAgentsInternalUsecase,
    UpsertAgentInternalUsecase {
    companion object {
        val log by Logger()
    }

    override suspend fun getAgent(instanceUid: Ulid): Agent? = null

    override suspend fun upsertAgent(agent: Agent): Agent = agentOperationsPort.saveAgent(agent)

    override suspend fun getAgents(request: GetAgentsRequest): Flow<Agent> {
        // TODO: Implement request by sort.
        return agentOperationsPort.getAgents()
    }
}
