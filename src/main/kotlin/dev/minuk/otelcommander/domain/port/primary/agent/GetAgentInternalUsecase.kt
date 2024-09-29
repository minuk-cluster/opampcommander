package dev.minuk.otelcommander.domain.port.primary.agent

import dev.minuk.otelcommander.domain.models.agent.Agent
import java.util.UUID

interface GetAgentInternalUsecase {
    suspend fun getAgent(instanceUid: UUID): Agent?
}
