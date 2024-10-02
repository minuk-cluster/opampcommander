package dev.minuk.otelcommander.adapter.primary.http.v1.agent

import dev.minuk.otelcommander.application.usecases.AgentAdminUsecase
import dev.minuk.otelcommander.domain.models.Sort
import dev.minuk.otelcommander.domain.models.agent.Agent
import dev.minuk.otelcommander.domain.port.primary.agent.GetAgentsRequest
import kotlinx.coroutines.flow.toList
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/agent")
class AgentController(
    val agentUsecase: AgentAdminUsecase,
) {
    @GetMapping("/{instanceUid}")
    suspend fun getAgentByInstanceUid(
        @PathVariable instanceUid: String,
    ): AgentResponse {
        TODO("Not yet implemented")
    }

    @GetMapping("")
    suspend fun getAgents(
        @RequestParam(value = "pivot", required = false, defaultValue = "") pivot: String,
        @RequestParam(value = "limit", required = false, defaultValue = "20") limit: Int,
        @RequestParam(value = "sort", required = false, defaultValue = "instanceUid:Asc") sort: String,
    ): MultipleAgentResponse {
        val basis = sort.split(":")[0]
        val direction = sort.split(":")[1]

        val agents =
            agentUsecase
                .getAgents(
                    request =
                        GetAgentsRequest(
                            pivot = pivot,
                            limit = limit,
                            sort =
                                Sort(
                                    basis = basis,
                                    direction = Sort.Direction.of(direction),
                                ),
                        ),
                ).toList()

        return MultipleAgentResponse(
            agents = agents, // TODO: Change to AgentResponse
            totalCount = null,
        )
    }
}

data class AgentResponse(
    val instanceUid: String,
    val agentDescription: AgentDescriptionForAgentResponse,
    val effectiveConfig: EffectiveConfigForAgentResponse,
    val packageStatuses: PackageStatusesForAgentResponse,
    val componentHealth: ComponentHealthForAgentResponse,
    val customCapabilities: CustomCapabilitiesForAgentResponse,
    val communicationStatus: CommunicationStatusForAgentResponse,
)

data class AgentDescriptionForAgentResponse(
    val identifyingAttributes: Map<String, String>,
    val nonIdentifyingAttributes: Map<String, String>,
)

data class EffectiveConfigForAgentResponse(
    val configMap: Map<String, AgentConfigMapForAgentResponse>,
) {
    data class AgentConfigMapForAgentResponse(
        val body: String,
        val contentType: String,
    )
}

data class PackageStatusesForAgentResponse(
    val packages: Map<String, PackageStatusForAgentResponse>,
) {
    data class PackageStatusForAgentResponse(
        val name: String,
        val agentHasVersion: String,
        val agentHasHash: String,
        val serverOfferedVersion: String,
        val serverOfferedHash: String,
        val status: String,
        val errorMessage: String,
    )
}

data class ComponentHealthForAgentResponse(
    val healthy: Boolean,
    val startedAt: String,
    val lastError: String,
    val status: String,
    val statusObservedAt: String,
    val componentHealthMap: Map<String, ComponentHealthForAgentResponse>,
)

data class CustomCapabilitiesForAgentResponse(
    val capabilities: Map<String, String>,
)

data class CommunicationStatusForAgentResponse(
    val sequenceNum: Int,
)

data class MultipleAgentResponse(
    val agents: List<Agent>, // TODO: Change to AgentResponse
    val totalCount: Int?, // totalCount is nullable because it's performance heavy to calculate
)
