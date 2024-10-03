package dev.minuk.opampcommander.adapter.primary.ws.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebsocketConfig {
    @Bean
    fun webSocketHandlerMapping(handlers: List<OpampCommanderWebsocketHandler>): HandlerMapping {
        val handlerMapping = SimpleUrlHandlerMapping()
        handlerMapping.order = -1
        handlerMapping.urlMap = handlers.associateBy { it.path }
        return handlerMapping
    }

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter = WebSocketHandlerAdapter()
}

interface OpampCommanderWebsocketHandler : WebSocketHandler {
    val path: String
}
