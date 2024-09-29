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
