package dev.minuk.otelcommander.domain.port.`in`.agent

import dev.minuk.otelcommander.domain.models.agent.Agent
import java.util.UUID

interface GetAgentInternalUsecase {
    suspend fun getAgent(instanceUid: UUID): Agent?
}