package dev.minuk.otelcommander.adapter.primary.http.v1.opamp

import dev.minuk.otelcommander.adapter.primary.http.v1.opamp.mapper.OpampMapper.toAgentDisconnectRequest
import dev.minuk.otelcommander.adapter.primary.http.v1.opamp.mapper.OpampMapper.toAgentExchangeRequest
import dev.minuk.otelcommander.application.usecases.DisconnectUsecase
import dev.minuk.otelcommander.application.usecases.ExchangeUsecase
import dev.minuk.otelcommander.util.Logger
import opamp.proto.Opamp
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/opamp")
class OpampController(
    val exchangeUsecase: ExchangeUsecase,
    val disconnectUsecase: DisconnectUsecase,
) {
    companion object {
        private val log by Logger()
    }

    @RequestMapping("/test")
    fun test(): Mono<ResponseEntity<String>> = Mono.just(ResponseEntity.ok("Hello, World!"))

    /**
     * @param: [Opamp.AgentToServer]
     * OPAMP says:
     *   - The Client sends AgentToServer Protobuf messages in the request body
     *   - Docs: https://opentelemetry.io/docs/specs/opamp/#plain-http-transport
     *
     * AgentToServer has below actions:
     * - Report:
     *   - AgentDescription
     *   - ComponentHealth
     *   - EffectiveConfig
     *   - RemoteConfigStatus
     *   - PackageStatuses
     *   - CustomCapabilities
     *   - Docs: https://opentelemetry.io/docs/specs/opamp/#agent-status-compression
     * - Disconnect:
     *   - Docs: https://opentelemetry.io/docs/specs/opamp/#agenttoserveragent_disconnect
     * - ConnectionSettingsRequest:
     *   - It's in development.
     *   - Docs: https://opentelemetry.io/docs/specs/opamp/#agent-initiated-csr-flow
     *
     * @return: [Opamp.ServerToAgent]
     * OPAMP says:
     *   - the Server sends ServerToAgent Protobuf messages in the response body.
     *   - Docs: https://opentelemetry.io/docs/specs/opamp/#plain-http-transport
     */
    @PostMapping(
        value = [""],
        consumes = [MediaType.APPLICATION_PROTOBUF_VALUE],
        produces = [MediaType.APPLICATION_PROTOBUF_VALUE],
    )
    suspend fun exchange(
        @RequestBody agentToServer: Opamp.AgentToServer,
    ): ResponseEntity<Opamp.ServerToAgent> {
        try {
            log.info("$agentToServer")
            exchangeUsecase.exchange(
                request = agentToServer.toAgentExchangeRequest(),
            )

            if (agentToServer.hasAgentDisconnect()) {
                disconnectUsecase.disconnect(
                    request = agentToServer.toAgentDisconnectRequest(),
                )
            }

            if (agentToServer.hasConnectionSettingsRequest()) {
                log.warn("ConnectionSettingsRequest is in development. Not implemented yet in this project. It will be skipped.")
            }

            return Opamp.ServerToAgent
                .newBuilder()
                .setInstanceUid(agentToServer.instanceUid)
                .build()
                .let { ResponseEntity.ok(it) }
        } catch (e: Exception) {
            log.error("Error occurred while processing the request: $agentToServer", e)
            return ResponseEntity.badRequest().build()
        }
    }
}
