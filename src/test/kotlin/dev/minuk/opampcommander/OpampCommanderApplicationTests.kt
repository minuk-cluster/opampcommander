package dev.minuk.opampcommander

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class OpampCommanderApplicationTests {
    companion object {
        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: org.springframework.test.context.DynamicPropertyRegistry) {
            val mongoDbContainer = MongoDBContainer("mongo:latest")
            mongoDbContainer.start()
            registry.add("spring.data.mongodb.uri") { mongoDbContainer.replicaSetUrl }
        }
    }

    @Test
    fun contextLoads() {
    }
}
