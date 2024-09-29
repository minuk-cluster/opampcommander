package dev.minuk.otelcommander.adapter.primary.http

import dev.minuk.otelcommander.adapter.primary.http.CompressionUtils.isGzipRequest
import org.apache.commons.io.IOUtils
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders.ACCEPT_ENCODING
import org.springframework.http.HttpHeaders.CONTENT_ENCODING
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.InputStream
import java.io.SequenceInputStream
import java.lang.RuntimeException
import java.util.zip.GZIPInputStream
import kotlin.text.Charsets.UTF_8

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class GzipDecompressionFilter : WebFilter {
    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        if (!exchange.request.isGzipRequest()) {
            return chain.filter(exchange)
        }

        val mutatedWebExchange = getMutatedWebExchange(exchange)
        return chain
            .filter(mutatedWebExchange)
            .onErrorResume {
                Mono.empty()
            }
    }

    private fun getMutatedWebExchange(serverWebExchange: ServerWebExchange): ServerWebExchange {
        val mutatedHttpRequest: ServerHttpRequest = GzipServerHttpRequest(serverWebExchange.request)
        return serverWebExchange
            .mutate()
            .request(mutatedHttpRequest)
            .build()
    }

    class GzipServerHttpRequest(
        private val serverHttpRequest: ServerHttpRequest,
    ) : ServerHttpRequest by serverHttpRequest {
        override fun getBody(): Flux<DataBuffer> =
            serverHttpRequest.body
                .map { it.asInputStream() }
                .reduce { acc, elem -> SequenceInputStream(acc, elem) }
                .flux()
                .flatMap {
                    DataBufferUtils.readInputStream(
                        { GZIPInputStream(it) },
                        DefaultDataBufferFactory(),
                        it.available(),
                    )
                }
    }
}

class IllegalGzipRequestException(
    message: String,
) : RuntimeException(message)

object CompressionUtils {
    const val GZIP: String = "gzip"
    const val UNKNOWN: String = "unknown"

    fun getDeflatedBytes(inputStream: InputStream): ByteArray {
        val string: String = IOUtils.toString(inputStream, UTF_8)
        return string.toByteArray()
    }

    fun ServerHttpRequest.isGzipRequest(): Boolean = containsGzip(this, CONTENT_ENCODING)

    fun isGzipResponseRequired(serverHttpRequest: ServerHttpRequest): Boolean = containsGzip(serverHttpRequest, ACCEPT_ENCODING)

    private fun containsGzip(
        serverHttpRequest: ServerHttpRequest,
        headerName: String,
    ): Boolean {
        if (!serverHttpRequest.headers.isEmpty()) {
            return serverHttpRequest.headers[headerName]?.contains(GZIP) ?: false
        }
        return false
    }
}
