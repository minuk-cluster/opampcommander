package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.document.AgentDocument
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.helperutil.querybuilder.SortDirection
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.helperutil.querybuilder.mongoQuery
import dev.minuk.opampcommander.domain.models.Sort
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.port.secondary.agent.AgentOperationsPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.FindAndReplaceOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.count
import org.springframework.stereotype.Component

@Component
class AgentMongoAdapter(
    val reactiveMongoOperations: ReactiveMongoOperations,
) : AgentOperationsPort {
    override suspend fun getAgentByInstanceUid(instanceUid: Ulid): Agent? {
        val query =
            mongoQuery {
                where {
                    "instanceUid" eq instanceUid.toUuid()
                }
            }
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

    override suspend fun getAgents(
        pivot: String?,
        limit: Int,
        sort: Sort,
    ): Flow<Agent> {
        val query =
            mongoQuery {
                sort {
                    val sortCriteria =
                        when (sort.direction) {
                            Sort.Direction.ASC -> SortDirection.Asc
                            Sort.Direction.DESC -> SortDirection.Desc
                        } by sort.basis

                    pivot?.let {
                        sortCriteria pivot it
                    } ?: sortCriteria
                }
                limit(limit)
            }

        return reactiveMongoOperations
            .find(query, AgentDocument::class.java)
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
    }

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
        val query =
            mongoQuery {
                where {
                    "instanceUid" eq agent.instanceUid.toUuid()
                }
            }
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
            .count<AgentDocument>()
            .awaitSingle()
}
