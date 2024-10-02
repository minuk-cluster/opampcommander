package dev.minuk.otelcommander.adapter.secondary.persistence.mongo.agent.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.bson.UuidRepresentation
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

@Configuration
class MongoConfiguration(
    val mongoProperties: MongoProperties,
    val converters: List<Converter<*, *>>,
) : AbstractReactiveMongoConfiguration() {
    override fun mongoClientSettings(): MongoClientSettings =
        MongoClientSettings
            .builder()
            .applyConnectionString(ConnectionString(mongoProperties.uri))
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .build()

    // TODO: Import databaseName from application.yaml
    //       Decode the spring.data.mongodb.uri from application.yaml
    override fun getDatabaseName(): String = mongoProperties.database

    override fun mappingMongoConverter(
        databaseFactory: ReactiveMongoDatabaseFactory,
        customConversions: MongoCustomConversions,
        mappingContext: MongoMappingContext,
    ): MappingMongoConverter {
        val mappingMongoConverter = super.mappingMongoConverter(databaseFactory, customConversions, mappingContext)
        mappingMongoConverter.setTypeMapper(DefaultMongoTypeMapper(null))
        return mappingMongoConverter
    }

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
