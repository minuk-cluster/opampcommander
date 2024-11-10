package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.helperutil.querybuilder

import org.springframework.data.mongodb.core.query.Query

@MongoDsl
class MongoQueryBuilder {
    private val query = Query()

    fun where(subQuery: CriteriaBuilder.() -> Unit) {
        val criteria = CriteriaBuilder().apply(subQuery).build()
        criteria.forEach { subCriteria ->
            query.addCriteria(subCriteria)
        }
    }

    fun sort(subQuery: SortCriteriaBuilder.() -> Unit) {
        val sortCriteria = SortCriteriaBuilder().apply(subQuery).build()
        sortCriteria.criteria?.let { query.addCriteria(it) }
        query.with(sortCriteria.sort)
    }

    fun limit(limit: Int): Query = query.limit(limit)

    fun build(): Query = query
}
