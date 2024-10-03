package dev.minuk.opampcommander.domain.port.primary.agent

import dev.minuk.opampcommander.domain.models.agent.Agent

interface UpsertAgentInternalUsecase {
    suspend fun upsertAgent(agent: Agent): Agent
}
