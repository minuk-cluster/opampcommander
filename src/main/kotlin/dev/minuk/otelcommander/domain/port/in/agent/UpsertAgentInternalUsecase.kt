package dev.minuk.otelcommander.domain.port.`in`.agent

import dev.minuk.otelcommander.domain.models.agent.Agent

interface UpsertAgentInternalUsecase {
    suspend fun upsertAgent(agent: Agent): Boolean
}