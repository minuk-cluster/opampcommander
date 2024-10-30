package dev.minuk.opampcommander.domain.port.primary.agent

interface CountAgentsInternalUsecase {
    /**
     * Get the total number of agents.
     */
    suspend fun getTotalAgents(): Long
}
