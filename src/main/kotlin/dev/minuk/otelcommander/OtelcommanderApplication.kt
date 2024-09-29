package dev.minuk.otelcommander

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OtelcommanderApplication

fun main(args: Array<String>) {
	runApplication<OtelcommanderApplication>(*args)
}
