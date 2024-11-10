package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.helperutil.querybuilder

import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria

/**
 * Thrown when an invalid pivot value is provided.
 */
class InvalidPivotException(
    message: String,
) : IllegalArgumentException(message)

@MongoDsl
class SortCriteriaBuilder {
    data class SortDirectionKey(
        val direction: SortDirection,
        val key: String,
    )

    private val sortCriteria =
        SortCriteria(
            sort = Sort.unsorted(),
            criteria = null,
        )

    infix fun SortDirection.by(key: String): SortDirectionKey {
        sortCriteria.apply {
            sort = Sort.by(direction, key)
        }
        return SortDirectionKey(
            direction = this,
            key = key,
        )
    }

    /**
     * Sets the pivot value for the sort criteria.
     * The pivot value is used to determine the sort direction.
     * If the pivot value is null, the sort criteria will not be set.
     *
     * @throws InvalidPivotException if the pivot value is invalid.
     */
    infix fun SortDirectionKey.pivot(value: Any) {
        var pivotValue = value

        if (key == "_id" && value is String) {
            try {
                pivotValue = ObjectId(value) // If the key is "_id", convert the value to an ObjectId.
            } catch (e: IllegalArgumentException) {
                throw InvalidPivotException("Invalid pivot value for key $key: $value")
            }
        }

        sortCriteria.criteria =
            when (direction) {
                SortDirection.Asc -> Criteria(key).gt(pivotValue)
                SortDirection.Desc -> Criteria(key).lt(pivotValue)
            }
    }

    fun build(): SortCriteria = sortCriteria
}

data class SortCriteria(
    var sort: Sort,
    var criteria: Criteria?,
)

enum class SortDirection(
    val direction: Sort.Direction,
) {
    Asc(Sort.Direction.ASC),
    Desc(Sort.Direction.DESC),
}
