package dev.minuk.otelcommander.domain.models

// TODO: Sort is a very general class. Split common application model.
data class Sort(
    val basis: String,
    val direction: Direction,
) {
    enum class Direction {
        ASC,
        DESC,
        ;

        companion object {
            fun of(value: String): Direction =
                when (value.lowercase()) {
                    "asc" -> ASC
                    "desc" -> DESC
                    else -> throw IllegalArgumentException("Invalid direction: $value")
                }
        }
    }
}
