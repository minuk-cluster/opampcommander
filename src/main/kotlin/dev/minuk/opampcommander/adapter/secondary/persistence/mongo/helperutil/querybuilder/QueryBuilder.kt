package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.helperutil.querybuilder

import org.springframework.data.mongodb.core.query.Query

/**
 * A DSL for building MongoDB queries.
 *
 * mongoQuery is the entry point for building a query.
 *
 * Example usage:
 *
 * ```kotlin
 * val query = mongoQuery {
 *    where {
 *      "field1" eq "value1"
 *    }
 *    sort {
 *      Asc by "field1" pivot "value1"
 *    }
 *    limit(10)
 * }
 *
 * val result = reactiveMongoOperations.find(query, Document::class.java)
 * ```
 */
fun mongoQuery(predicate: MongoQueryBuilder.() -> Unit): Query = MongoQueryBuilder().apply(predicate).build()
