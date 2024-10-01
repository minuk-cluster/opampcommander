package dev.minuk.otelcommander.domain.port.primary.agent

import dev.minuk.otelcommander.domain.models.Sort
import dev.minuk.otelcommander.domain.models.agent.Agent
import kotlinx.coroutines.flow.Flow

interface GetAgentsInternalUsecase {
    suspend fun getAgents(request: GetAgentsRequest): Flow<Agent>
}

data class GetAgentsRequest(
    val pivot: String,
    val limit: Int,
    val sort: Sort,
)
