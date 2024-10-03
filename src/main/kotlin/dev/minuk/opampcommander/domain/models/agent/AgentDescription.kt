package dev.minuk.opampcommander.domain.models.agent

data class AgentDescription(
    val identifyingAttributes: IdentifyingAttributes,
    val nonIdentifyingAttributes: NonIdentifyingAttributes,
) {
    val os: Os
        get() = nonIdentifyingAttributes.getOs()
    val service: Service
        get() = identifyingAttributes.getService()
    val host: Host
        get() = nonIdentifyingAttributes.getHost()

    companion object {
        fun empty(): AgentDescription =
            AgentDescription(
                identifyingAttributes = emptyMap(),
                nonIdentifyingAttributes = emptyMap(),
            )
    }
}

typealias IdentifyingAttributes = Map<String, String>

fun IdentifyingAttributes.getService(): Service {
    val serviceName: String? = this["service.name"]
    val serviceNamespace: String? = this["service.namespace"]
    val serviceVersion: String? = this["service.version"]
    val serviceInstanceId: String? = this["service.instance.id"]
    return Service(
        instanceId = serviceInstanceId,
        name = serviceName,
        version = serviceVersion,
        namespace = serviceNamespace,
    )
}

typealias NonIdentifyingAttributes = Map<String, String>

fun NonIdentifyingAttributes.getOs(): Os {
    val osType: String? = this["os.type"]
    val osVersion: String? = this["os.version"]
    return Os(
        type = osType,
        version = osVersion,
    )
}

fun NonIdentifyingAttributes.getHost(): Host {
    val hostName: String? = this["host.name"]
    return Host(
        name = hostName,
    )
}
