package dev.minuk.otelcommander.domain.models.agent

data class PackageStatuses(
    val packages: Map<String, PackageStatus>,
    val serverProvidedAllPackagesHash: String,
    val errorMessage: String,
) {
    companion object {
        fun empty(): PackageStatuses =
            PackageStatuses(
                packages = mapOf(),
                serverProvidedAllPackagesHash = "",
                errorMessage = "",
            )
    }
}

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
