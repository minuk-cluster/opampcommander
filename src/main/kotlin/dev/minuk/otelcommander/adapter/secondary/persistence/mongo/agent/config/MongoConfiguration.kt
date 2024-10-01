package dev.minuk.otelcommander.adapter.secondary.persistence.mongo.agent.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Configuration
class MongoConfiguration(
    val converters: List<Converter<*, *>>,
) : AbstractReactiveMongoConfiguration() {
    // TODO: Import databaseName from application.yaml
    //       Decode the spring.data.mongodb.uri from application.yaml
    override fun getDatabaseName(): String = "commander"

    override fun customConversions(): MongoCustomConversions =
        MongoCustomConversions(
            converters.filter {
                it.javaClass.isAnnotationPresent(MongodbReadingConverter::class.java) ||
                    it.javaClass.isAnnotationPresent(MongodbWritingConverter::class.java)
            },
        )

    override fun getMappingBasePackages(): Collection<String> =
        listOf(
            "dev.minuk.otelcommander.adapter.secondary.persistence.mongo",
        )
}

