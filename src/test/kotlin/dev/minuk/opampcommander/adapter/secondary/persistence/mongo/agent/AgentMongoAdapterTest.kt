package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.document.AgentDocument
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
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertNull

@RunWith(SpringRunner::class)
@DataMongoTest
class AgentMongoAdapterTest(
    @Autowired
    private val reactiveMongoOperations: ReactiveMongoOperations,
){
    val agentMongoAdapter = AgentMongoAdapter(reactiveMongoOperations)

    @BeforeEach
    fun setup() {
        // clean up the database
        reactiveMongoOperations.dropCollection("agents").block()
        // create a test collection
        reactiveMongoOperations.createCollection("agents").block()
    }

    @Test
    fun `getAgentByInstanceUid should return agent when agent exists`() {
        // given
        val instanceUid = Ulid.fast()
        reactiveMongoOperations.insert(emptyAgentDocument().copy(instanceUid = instanceUid.toUuid())).block()
        // when
        val agent = runBlocking { agentMongoAdapter.getAgentByInstanceUid(instanceUid = instanceUid) }
        // then
        assertNotNull(agent)
    }

    @Test
    fun `getAgentByInstanceUid should return null when agent not exists`() {
        // given
        val instanceUid = Ulid.fast()
        // when
        val agent = runBlocking { agentMongoAdapter.getAgentByInstanceUid(instanceUid = instanceUid) }
        // then
        assertNull(agent)
    }

    private fun emptyAgentDocument() = AgentDocument(
        instanceUid = Ulid.fast().toUuid(),
        agentDescription = AgentDescription(
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