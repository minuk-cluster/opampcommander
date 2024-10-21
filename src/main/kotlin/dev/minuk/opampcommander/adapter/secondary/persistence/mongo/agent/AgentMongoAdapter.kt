package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.document.AgentDocument
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.port.secondary.agent.AgentOperationsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.FindAndReplaceOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class AgentMongoAdapter(
    val reactiveMongoOperations: ReactiveMongoOperations,
) : AgentOperationsPort {
    override suspend fun getAgentByInstanceUid(instanceUid: Ulid): Agent? {
        val query = Query.query(Criteria.where("instanceUid").`is`(instanceUid.toUuid()))
        return reactiveMongoOperations
            .findOne(query, AgentDocument::class.java)
            .map {
                Agent(
                    instanceUid = Ulid.from(it.instanceUid),
                    capabilities = it.capabilities,
                    agentDescription = it.agentDescription,
                    effectiveConfig = it.effectiveConfig,
                    communicationStatus = it.communicationStatus,
                    packageStatuses = it.packageStatuses,
                    componentHealth = it.componentHealth,
                    customCapabilities = it.customCapabilities,
                )
            }.awaitSingleOrNull()
    }

    override suspend fun getAgents(): Flow<Agent> =
        reactiveMongoOperations
            .findAll(AgentDocument::class.java)
            .map {
                Agent(
                    instanceUid = Ulid.from(it.instanceUid),
                    capabilities = it.capabilities,
                    agentDescription = it.agentDescription,
                    effectiveConfig = it.effectiveConfig,
                    communicationStatus = it.communicationStatus,
                    packageStatuses = it.packageStatuses,
                    componentHealth = it.componentHealth,
                    customCapabilities = it.customCapabilities,
                )
            }.asFlow()

    override suspend fun saveAgent(agent: Agent): Agent {
        val agentDocument =
            AgentDocument(
                instanceUid = agent.instanceUid.toUuid(),
                capabilities = agent.capabilities,
                agentDescription = agent.agentDescription,
                effectiveConfig = agent.effectiveConfig,
                communicationStatus = agent.communicationStatus,
                packageStatuses = agent.packageStatuses,
                componentHealth = agent.componentHealth,
                customCapabilities = agent.customCapabilities,
            )
        val query = Query.query(Criteria.where("instanceUid").`is`(agent.instanceUid.toUuid()))
        val options = FindAndReplaceOptions.options().upsert().returnNew()
        return reactiveMongoOperations
            .findAndReplace(query, agentDocument, options)
            .map {
                Agent(
                    instanceUid = Ulid.from(it.instanceUid),
                    capabilities = it.capabilities,
                    agentDescription = it.agentDescription,
                    effectiveConfig = it.effectiveConfig,
                    communicationStatus = it.communicationStatus,
                    packageStatuses = it.packageStatuses,
                    componentHealth = it.componentHealth,
                    customCapabilities = it.customCapabilities,
                )
            }.awaitSingle()
    }

    override suspend fun countAgents(): Long =
        reactiveMongoOperations
            .count(Query(), AgentDocument::class.java)
            .awaitSingle()
}
