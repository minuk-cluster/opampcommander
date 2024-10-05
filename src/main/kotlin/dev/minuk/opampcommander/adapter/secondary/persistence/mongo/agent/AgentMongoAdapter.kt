package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.document.AgentDocument
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.port.secondary.agent.AgentOperationsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class AgentMongoAdapter(
    val reactiveMongoTemplate: ReactiveMongoTemplate,
) : AgentOperationsPort {
    override suspend fun getAgentByInstanceUid(instanceUid: Ulid): Agent? {
        val query = Query.query(Criteria.where("instanceUid").`is`(instanceUid))
        return reactiveMongoTemplate
            .findOne(query, AgentDocument::class.java)
            .map {
                Agent(
                    instanceUid = it.instanceUid,
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
        reactiveMongoTemplate
            .findAll(AgentDocument::class.java)
            .map {
                Agent(
                    instanceUid = it.instanceUid,
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
                instanceUid = agent.instanceUid,
                agentDescription = agent.agentDescription,
                effectiveConfig = agent.effectiveConfig,
                communicationStatus = agent.communicationStatus,
                packageStatuses = agent.packageStatuses,
                componentHealth = agent.componentHealth,
                customCapabilities = agent.customCapabilities,
            )
        return reactiveMongoTemplate
            .save(agentDocument)
            .map {
                Agent(
                    instanceUid = it.instanceUid,
                    agentDescription = it.agentDescription,
                    effectiveConfig = it.effectiveConfig,
                    communicationStatus = it.communicationStatus,
                    packageStatuses = it.packageStatuses,
                    componentHealth = it.componentHealth,
                    customCapabilities = it.customCapabilities,
                )
            }.awaitSingle()
    }
}
