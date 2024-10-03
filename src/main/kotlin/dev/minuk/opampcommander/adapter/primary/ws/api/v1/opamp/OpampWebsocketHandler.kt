package dev.minuk.opampcommander.adapter.primary.ws.api.v1.opamp

import dev.minuk.opampcommander.adapter.primary.http.v1.opamp.mapper.OpampMapper.toAgentExchangeRequest
import dev.minuk.opampcommander.adapter.primary.ws.config.OpampCommanderWebsocketHandler
import dev.minuk.opampcommander.application.usecases.DisconnectUsecase
import dev.minuk.opampcommander.application.usecases.ExchangeUsecase
import dev.minuk.opampcommander.domain.models.agent.Agent
import dev.minuk.opampcommander.util.Logger
import kotlinx.coroutines.reactor.mono
import opamp.proto.Opamp
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStream

@Component
class OpampWebsocketHandler(
    val exchangeUsecase: ExchangeUsecase,
    val disconnectUsecase: DisconnectUsecase,
) : OpampCommanderWebsocketHandler {
    companion object {
        private val log by Logger()
    }

    override val path: String = "/v1/opamp"

    override fun handle(session: WebSocketSession): Mono<Void> {
        val input: Flux<Agent> =
            session
                .receive()
                .map { message ->
                    message.payload.asInputStream()
                }.map { payloadInputStream ->
                    OpampWebsocketMessageFormat(
                        header = payloadInputStream.readVarInt(),
                        data = payloadInputStream,
                    )
                }.map { opampMessage ->
                    log.info("Received message header: ${opampMessage.header}")
                    val agentToServer = Opamp.AgentToServer.parseFrom(opampMessage.data)
                    agentToServer
                }.flatMap { agentToServer ->
                    val agent =
                        mono {
                            exchangeUsecase.exchange(
                                request = agentToServer.toAgentExchangeRequest(),
                            )
                        }

                    // todo: disconnect
                    // todo: connectionsettingsrequest
                    agent
                }
        // todo: output
        // val output = session.send()

        // return Mono.zip(input, output).then()
        return input.then()
    }

    private fun InputStream.readVarInt(): Long {
        var value = 0L
        var shift = 0
        var b: Int
        do {
            b = read()
            value += (b and 0x7F shl shift).toLong()
            if (b and 0x80 == 0) {
                break
            }
            shift += 7
        } while (true)
        return value
    }
}

data class OpampWebsocketMessageFormat(
    val header: Long,
    val data: InputStream,
) {
    companion object {
        const val HEADER_SIZE = 10
    }
}
