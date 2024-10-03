package dev.minuk.opampcommander.application.usecases

import opamp.proto.Opamp.AgentToServer

interface HandleAgentToServerUsecase {
    suspend fun handleAgentToServer(agentToServer: AgentToServer)
}
