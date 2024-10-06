package dev.minuk.opampcommander.domain.port.primary.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.domain.models.agent.Agent

interface GetAgentInternalUsecase {
    suspend fun getAgent(instanceUid: Ulid): Agent?

    // TODO: Optimize this function to return a boolean instead of an Agent?
    suspend fun existsAgent(instanceUid: Ulid): Boolean = getAgent(instanceUid) != null
}
