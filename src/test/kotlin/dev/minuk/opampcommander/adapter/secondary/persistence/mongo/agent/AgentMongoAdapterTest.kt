package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.TestcontainersConfiguration
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.document.AgentDocument
import dev.minuk.opampcommander.domain.models.Sort
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.models.agent.AgentCapabilities
import dev.minuk.opampcommander.domain.models.agent.AgentDescription
import dev.minuk.opampcommander.domain.models.agent.CommunicationStatus
import dev.minuk.opampcommander.domain.models.agent.ComponentHealth
import dev.minuk.opampcommander.domain.models.agent.CustomCapabilities
import dev.minuk.opampcommander.domain.models.agent.EffectiveConfig
import dev.minuk.opampcommander.domain.models.agent.PackageStatuses
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import kotlin.test.assertNull

@RunWith(SpringRunner::class)
@Import(TestcontainersConfiguration::class)
@DataMongoTest
class AgentMongoAdapterTest(
    @Autowired
    private val reactiveMongoOperations: ReactiveMongoOperations,
) {
    val agentMongoAdapter = AgentMongoAdapter(reactiveMongoOperations)

    @BeforeEach
    fun setup() {
        // clean up the database
        reactiveMongoOperations.dropCollection("agents").block()
        // create a test collection
        reactiveMongoOperations.createCollection("agents").block()
    }

    @Test
    fun `getAgentByInstanceUid() should return agent when agent exists`() {
        // given
        val instanceUid = Ulid.fast()
        reactiveMongoOperations.insert(emptyAgentDocument().copy(instanceUid = instanceUid.toUuid())).block()
        // when
        val agent = runBlocking { agentMongoAdapter.getAgentByInstanceUid(instanceUid = instanceUid) }
        // then
        assertNotNull(agent)
    }

    @Test
    fun `getAgents() should return multiple agents when agents exist`() {
        // given
        val instanceUid1 = Ulid.fast()
        val instanceUid2 = Ulid.fast()
        reactiveMongoOperations.insert(emptyAgentDocument().copy(instanceUid = instanceUid1.toUuid())).block()
        reactiveMongoOperations.insert(emptyAgentDocument().copy(instanceUid = instanceUid2.toUuid())).block()
        // when
        val agents =
            runBlocking {
                agentMongoAdapter
                    .getAgents(
                        pivot = null,
                        limit = 2,
                        sort = Sort("instanceUid", Sort.Direction.ASC),
                    ).toList()
            }
        // then
        assertNotNull(agents)
        assert(agents.size == 2)
    }

    @Test
    fun `getAgents() should return limited data when limit`() {
        // given
        val instanceUid1 = Ulid.fast() // It's a trick to generate instanceUid1 < instanceUid2
        val instanceUid2 = Ulid.fast()
        reactiveMongoOperations.insert(emptyAgentDocument().copy(instanceUid = instanceUid1.toUuid())).block()
        reactiveMongoOperations.insert(emptyAgentDocument().copy(instanceUid = instanceUid2.toUuid())).block()

        // when
        val agents =
            runBlocking {
                agentMongoAdapter
                    .getAgents(
                        pivot = null,
                        limit = 1,
                        sort = Sort("instanceUid", Sort.Direction.ASC),
                    ).toList()
            }
        // then
        assertNotNull(agents)
        assertEquals(1, agents.size)
    }

    @Test
    fun `getAgents() should return next page of agents when id pivot is provided`() {
        // given
        val instanceUid1 = Ulid.fast()
        val instanceUid2 = Ulid.fast()
        val agent1 = reactiveMongoOperations.insert(emptyAgentDocument().copy(instanceUid = instanceUid1.toUuid())).block()
        val agent2 = reactiveMongoOperations.insert(emptyAgentDocument().copy(instanceUid = instanceUid2.toUuid())).block()

        val agent1Id = agent1!!.id!!
        val agent2Id = agent2!!.id!!
        var pivot = agent1Id
        if (pivot > agent2Id) {
            pivot = agent2Id
        }

        // when
        val agents =
            runBlocking {
                agentMongoAdapter
                    .getAgents(
                        pivot = pivot,
                        limit = 1,
                        sort = Sort("id", Sort.Direction.ASC),
                    ).toList()
            }
        // then
        assertNotNull(agents)
        assert(agents.size == 1)
    }

    @Test
    fun `getAgentByInstanceUid() should return null when agent not exists`() {
        // given
        val instanceUid = Ulid.fast()
        // when
        val agent = runBlocking { agentMongoAdapter.getAgentByInstanceUid(instanceUid = instanceUid) }
        // then
        assertNull(agent)
    }

    @Test
    fun `saveAgent() should save agent to database`() {
        // given
        val instanceUid = Ulid.fast()
        val agent = Agent(instanceUid = instanceUid)

        // when
        runBlocking { agentMongoAdapter.saveAgent(agent) }

        // then - 1
        StepVerifier
            .create(
                reactiveMongoOperations.findOne(
                    Query.query(
                        Criteria.where("instanceUid").`is`(instanceUid.toUuid()),
                    ),
                    AgentDocument::class.java,
                ),
            ).assertNext {
                assertNotNull(it)
            }.verifyComplete()

        // then - 2
        reactiveMongoOperations
            .findOne(
                Query.query(
                    Criteria.where("instanceUid").`is`(instanceUid.toUuid()),
                ),
                AgentDocument::class.java,
            ).block()
            ?.let {
                assertNotNull(it)
            }
    }

    private fun emptyAgentDocument() =
        AgentDocument(
            instanceUid = Ulid.fast().toUuid(),
            capabilities = AgentCapabilities.empty(),
            agentDescription =
                AgentDescription(
                    identifyingAttributes = emptyMap(),
                    nonIdentifyingAttributes = emptyMap(),
                ),
            effectiveConfig = EffectiveConfig.empty(),
            communicationStatus = CommunicationStatus.empty(),
            packageStatuses = PackageStatuses.empty(),
            componentHealth = ComponentHealth.empty(),
            customCapabilities = CustomCapabilities.empty(),
        )
}
