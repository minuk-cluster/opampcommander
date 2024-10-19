package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.document.AgentDocument
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.domain.models.agent.AgentDescription
import dev.minuk.opampcommander.domain.models.agent.CommunicationStatus
import dev.minuk.opampcommander.domain.models.agent.ComponentHealth
import dev.minuk.opampcommander.domain.models.agent.CustomCapabilities
import dev.minuk.opampcommander.domain.models.agent.EffectiveConfig
import dev.minuk.opampcommander.domain.models.agent.PackageStatuses
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import kotlin.test.assertNull

@RunWith(SpringRunner::class)
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
