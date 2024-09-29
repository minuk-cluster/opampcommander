package dev.minuk.otelcommander.domain.models.agent

data class PackageStatus(
    val name: String,
    val agentHasVersion: String,
    val agentHasHash: String,
    val serverOfferedVersion: String,
    val serverOfferedHash: String,
    val status: Status,
    val errorMessage: String,
) {
    enum class Status(
        val value: Int,
    ) {
        INSTALLED(0),
        INSTALLING(1),
        INSTALL_FAILED(2),
        ;

        companion object {
            fun of(value: Int): Status = Status.entries.find { it.value == value }!!
        }
    }
}
