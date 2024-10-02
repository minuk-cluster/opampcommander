package dev.minuk.otelcommander.application.services

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.otelcommander.application.usecases.AgentAdminUsecase
import dev.minuk.otelcommander.domain.models.agent.Agent
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentInternalUsecase
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentsInternalUsecase
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentsRequest
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AgentAdminService(
    val getAgentInternalUsecase: GetAgentInternalUsecase,
    val getAgentsInternalUsecase: GetAgentsInternalUsecase,
) : AgentAdminUsecase {
    override suspend fun getAgentByInstanceUid(instanceUid: String): Agent {
        // TODO: Make another exception to handle detailed error message
        @Suppress("NAME_SHADOWING")
        val instanceUid = Ulid.from(instanceUid)
        val agent = getAgentInternalUsecase.getAgent(instanceUid) ?: throw IllegalArgumentException("Agent not found")
        return agent
    }

    override suspend fun getAgents(request: GetAgentsRequest): Flow<Agent> = getAgentsInternalUsecase.getAgents(request = request)

    private fun String.toUUID(): UUID = UUID.fromString(this)
}
