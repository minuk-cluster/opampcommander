package dev.minuk.otelcommander.adapter.secondary.persistence.mongo.agent.document

import dev.minuk.otelcommander.domain.models.agent.AgentDescription
import dev.minuk.otelcommander.domain.models.agent.CommunicationStatus
import dev.minuk.otelcommander.domain.models.agent.ComponentHealth
import dev.minuk.otelcommander.domain.models.agent.CustomCapabilities
import dev.minuk.otelcommander.domain.models.agent.EffectiveConfig
import dev.minuk.otelcommander.domain.models.agent.PackageStatuses
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("agents")
data class AgentDocument(
    @Id
    var id: String? = null,
    @Indexed
    val instanceUid: UUID,
    val agentDescription: AgentDescription,
    val effectiveConfig: EffectiveConfig,
    val communicationStatus: CommunicationStatus,
    val packageStatuses: PackageStatuses,
    val componentHealth: ComponentHealth,
    val customCapabilities: CustomCapabilities,
)
