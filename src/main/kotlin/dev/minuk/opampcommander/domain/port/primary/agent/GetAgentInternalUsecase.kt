package dev.minuk.opampcommander.domain.port.primary.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.domain.models.agent.Agent

interface GetAgentInternalUsecase {
    suspend fun getAgent(instanceUid: Ulid): Agent?
}
