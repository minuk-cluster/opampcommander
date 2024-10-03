package dev.minuk.opampcommander.domain.models.agent

data class RemoteConfigStatus(
    val lastRemoteConfigHash: String,
    val status: Status,
    val errorMessage: String,
) {
    enum class Status(
        val value: Int,
    ) {
        UNSET(0),
        APPLIED(1),
        APPLYING(2),
        FAILED(3),
        ;

        companion object {
            fun of(value: Int): Status = Status.entries.find { it.value == value }!!
        }
    }
}
