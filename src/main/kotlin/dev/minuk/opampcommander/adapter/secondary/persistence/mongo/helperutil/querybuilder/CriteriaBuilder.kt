package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.helperutil.querybuilder

import org.springframework.data.mongodb.core.query.Criteria

@MongoDsl
class CriteriaBuilder {
    private val criteria: MutableList<Criteria> = mutableListOf()

    fun and(subQuery: CriteriaBuilder.() -> Unit) {
        val subCriteria = CriteriaBuilder().apply(subQuery).build()
        if (criteria.isEmpty()) {
            criteria.add(Criteria())
        }
        criteria.last().andOperator(subCriteria)
    }

    fun or(subQuery: CriteriaBuilder.() -> Unit) {
        val subCriteria = CriteriaBuilder().apply(subQuery).build()
        if (criteria.isEmpty()) {
            criteria.add(Criteria())
        }
        criteria.last().orOperator(subCriteria)
    }

    fun range(subQuery: RangeCriteriaBuilder.() -> Unit) {
        val rangeCriteria = RangeCriteriaBuilder().apply(subQuery).build()
        if (rangeCriteria.isNotEmpty()) {
            criteria.addAll(rangeCriteria)
        }
    }

    /**
     * eq - equals
     * `is` is a reserved keyword in Kotlin so use `eq` instead
     */
    infix fun String.eq(value: Any?) {
        criteria.add(Criteria(this).`is`(value))
    }

    infix fun String.ne(value: Any?) {
        criteria.add(Criteria(this).ne(value))
    }

    infix fun String.gt(value: Any) {
        criteria.add(Criteria(this).gt(value))
    }

    infix fun String.gte(value: Any) {
        criteria.add(Criteria(this).gte(value))
    }

    infix fun String.lt(value: Any) {
        criteria.add(Criteria(this).lt(value))
    }

    infix fun String.lte(value: Any) {
        criteria.add(Criteria(this).lte(value))
    }

    /**
     * `in` is a reserved keyword in Kotlin so use `contains` instead
     */
    infix fun String.contains(values: Iterable<Any?>) {
        values.toList().let {
            criteria.add(Criteria(this).`in`(it))
        }
    }

    infix fun String.notIn(values: Iterable<Any?>) {
        values.toList().let {
            criteria.add(Criteria(this).nin(it))
        }
    }

    infix fun String.exists(value: Boolean) {
        criteria.add(Criteria(this).exists(value))
    }

    fun build(): Collection<Criteria> = criteria
}
