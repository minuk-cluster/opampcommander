package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.helperutil.querybuilder

import org.springframework.data.mongodb.core.query.Criteria

@MongoDsl
class RangeCriteriaBuilder {
    private val criteria: MutableList<Criteria> = mutableListOf()

    infix fun <T> String.gt(value: T?): PartialRange<T> =
        PartialRange(
            key = this,
            start = Exclusive(value),
        )

    infix fun <T> PartialRange<T>.lt(value: T?) {
        addCriteria(
            CompleteRange(
                partialRange = this,
                end = Exclusive(value),
            ),
        )
    }

    infix fun <T> PartialRange<T>.lte(value: T?) {
        addCriteria(
            CompleteRange(
                partialRange = this,
                end = Inclusive(value),
            ),
        )
    }

    private fun <T> addCriteria(range: CompleteRange<T>) {
        val start = range.partialRange.start
        val end = range.end

        if (start.value != null || end.value != null) {
            val rangeCriteria = Criteria(range.partialRange.key)
            when (start.isInclusive) {
                true ->
                    start.value?.let {
                        rangeCriteria.gt(it as Any)
                    }
                false ->
                    start.value?.let {
                        rangeCriteria.gte(it as Any)
                    }
            }

            when (end.isInclusive) {
                true ->
                    end.value?.let {
                        rangeCriteria.lt(it as Any)
                    }
                false ->
                    end.value?.let {
                        rangeCriteria.lte(it as Any)
                    }
            }
            criteria.add(rangeCriteria)
        }
    }

    fun build(): Collection<Criteria> = criteria
}

data class PartialRange<T>(
    val key: String,
    val start: InclusiveOrExclusive<T>,
)

data class CompleteRange<T>(
    val partialRange: PartialRange<T>,
    val end: InclusiveOrExclusive<T>,
)

interface InclusiveOrExclusive<T> {
    val value: T?

    val isInclusive: Boolean

    val isExclusive: Boolean
        get() = !isInclusive
}

class Inclusive<T>(
    override val value: T?,
) : InclusiveOrExclusive<T> {
    override val isInclusive: Boolean
        get() = true
}

class Exclusive<T>(
    override val value: T?,
) : InclusiveOrExclusive<T> {
    override val isInclusive: Boolean
        get() = false
}
