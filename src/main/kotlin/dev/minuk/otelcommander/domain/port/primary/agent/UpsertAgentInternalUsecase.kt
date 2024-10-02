package dev.minuk.otelcommander.domain.port.primary.agent

import dev.minuk.otelcommander.domain.models.agent.Agent

interface UpsertAgentInternalUsecase {
    suspend fun upsertAgent(agent: Agent): Agent
}
