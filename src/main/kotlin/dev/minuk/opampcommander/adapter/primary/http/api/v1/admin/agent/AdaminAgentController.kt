package dev.minuk.opampcommander.adapter.primary.http.api.v1.admin.agent

import dev.minuk.opampcommander.application.usecases.AdminAgentUsecase
import dev.minuk.opampcommander.domain.models.Sort
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.models.agent.AgentCapabilities
import dev.minuk.opampcommander.domain.models.agent.AgentConfigFile
import dev.minuk.opampcommander.domain.models.agent.AgentDescription
import dev.minuk.opampcommander.domain.models.agent.CommunicationStatus
import dev.minuk.opampcommander.domain.models.agent.ComponentHealth
import dev.minuk.opampcommander.domain.models.agent.CustomCapabilities
import dev.minuk.opampcommander.domain.models.agent.EffectiveConfig
import dev.minuk.opampcommander.domain.models.agent.PackageStatuses
import dev.minuk.opampcommander.domain.port.primary.agent.GetAgentsRequest
import kotlinx.coroutines.flow.toList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/agent")
class AdaminAgentController(
    val adminAgentUsecase: AdminAgentUsecase,
) {
    @GetMapping("/{instanceUid}")
    suspend fun getAgentByInstanceUid(
        @PathVariable instanceUid: String,
    ): ResponseEntity<AgentForAdminResponse> {
        val agent = adminAgentUsecase.getAgentByInstanceUid(instanceUid)
        return ResponseEntity.ofNullable(agent?.toAdminResponse())
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
            adminAgentUsecase
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
        val totalCount = adminAgentUsecase.getTotalAgents()

        return MultipleAgentResponse(
            agents = agents.map { it.toAdminResponse() },
            totalCount = totalCount,
        )
    }

    private fun Agent.toAdminResponse(): AgentForAdminResponse {
        return AgentForAdminResponse(
            instanceUid = instanceUid.toString(),
            capabilities = capabilities.toAdminResponse(),
            agentDescription = agentDescription.toAdminResponse(),
            effectiveConfig = effectiveConfig.toAdminResponse(),
            packageStatuses = packageStatuses.toAdminResponse(),
            componentHealth = componentHealth.toAdminResponse(),
            customCapabilities = customCapabilities.toAdminResponse(),
            communicationStatus = communicationStatus.toAdminResponse(),
        )
    }

    private fun AgentDescription.toAdminResponse(): AgentDescriptionForAdminResponse {
        return AgentDescriptionForAdminResponse(
            identifyingAttributes = identifyingAttributes,
            nonIdentifyingAttributes = nonIdentifyingAttributes,
        )
    }

    private fun EffectiveConfig.toAdminResponse(): EffectiveConfigForAdminResponse {
        return EffectiveConfigForAdminResponse(
            configMap = EffectiveConfigForAdminResponse.AgentConfigMapForAdminResponse(
                configMap = configMap.configMap.mapValues {
                    it.value.toAgentResponse()
                }
            )
        )
    }

    private fun PackageStatuses.toAdminResponse(): PackageStatusesForAdminResponse {
        return PackageStatusesForAdminResponse(
            packages = packages.mapValues {
                PackageStatusesForAdminResponse.PackageStatusForAgentResponse(
                    name = it.value.name,
                    agentHasVersion = it.value.agentHasVersion,
                    agentHasHash = it.value.agentHasHash,
                    serverOfferedVersion = it.value.serverOfferedVersion,
                    serverOfferedHash = it.value.serverOfferedHash,
                    status = it.value.status.toString(),
                    errorMessage = it.value.errorMessage,
                )
            }
        )
    }

    private fun ComponentHealth.toAdminResponse(): ComponentHealthForAdminResponse {
        return ComponentHealthForAdminResponse(
            healthy = healthy,
            startedAt = startedAt.toString(),
            lastError = lastError,
            status = status,
            statusObservedAt = statusObservedAt.toString(),
            componentHealthMap = componentHealthMap.mapValues {
                it.value.toAdminResponse()
            }
        )
    }

    private fun CustomCapabilities.toAdminResponse(): CustomCapabilitiesForAdminResponse {
        return CustomCapabilitiesForAdminResponse(
            capabilities = capabilities,
        )
    }

    private fun CommunicationStatus.toAdminResponse(): CommunicationStatusForAdminResponse {
        return CommunicationStatusForAdminResponse(
            sequenceNum = sequenceNum,
        )
    }
    private fun AgentCapabilities.toAdminResponse(): AgentCapabilitiesForAdminResponse {
        return AgentCapabilitiesForAdminResponse(
            capabilities = capabilities.map { it.toString() },
        )
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun AgentConfigFile.toAgentResponse(): EffectiveConfigForAdminResponse.AgentConfigFileForAdminResponse {
    return when (contentType) {
        "", "text/plain", "text/yaml" -> EffectiveConfigForAdminResponse.AgentConfigFileForAdminResponse(
            body = String(body),
            contentType = contentType,
        )
        else -> EffectiveConfigForAdminResponse.AgentConfigFileForAdminResponse(
            body = body.toHexString(),
            contentType = contentType,
        )
    }
}


data class AgentForAdminResponse(
    val instanceUid: String,
    val capabilities: AgentCapabilitiesForAdminResponse,
    val agentDescription: AgentDescriptionForAdminResponse,
    val effectiveConfig: EffectiveConfigForAdminResponse,
    val packageStatuses: PackageStatusesForAdminResponse,
    val componentHealth: ComponentHealthForAdminResponse,
    val customCapabilities: CustomCapabilitiesForAdminResponse,
    val communicationStatus: CommunicationStatusForAdminResponse,
)

data class AgentCapabilitiesForAdminResponse(
    val capabilities: List<String>,
)

data class AgentDescriptionForAdminResponse(
    val identifyingAttributes: Map<String, String>,
    val nonIdentifyingAttributes: Map<String, String>,
)

data class EffectiveConfigForAdminResponse(
    val configMap: AgentConfigMapForAdminResponse,
) {
    data class AgentConfigMapForAdminResponse(
        val configMap: Map<String, AgentConfigFileForAdminResponse>,
    )
    data class AgentConfigFileForAdminResponse(
        val body: String,
        val contentType: String,
    )

}

data class PackageStatusesForAdminResponse(
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

data class ComponentHealthForAdminResponse(
    val healthy: Boolean,
    val startedAt: String,
    val lastError: String,
    val status: String,
    val statusObservedAt: String,
    val componentHealthMap: Map<String, ComponentHealthForAdminResponse>,
)

data class CustomCapabilitiesForAdminResponse(
    val capabilities: List<String>,
)

data class CommunicationStatusForAdminResponse(
    val sequenceNum: Long,
)

data class MultipleAgentResponse(
    val agents: List<AgentForAdminResponse>, // TODO: Change to AgentResponse
    val totalCount: Long?, // totalCount is nullable because it's performance heavy to calculate
)
