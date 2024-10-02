package dev.minuk.otelcommander.domain.port.primary.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.otelcommander.domain.models.agent.Agent

interface GetAgentInternalUsecase {
    suspend fun getAgent(instanceUid: Ulid): Agent?
}
