package dev.minuk.otelcommander

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<OtelcommanderApplication>().with(TestcontainersConfiguration::class).run(*args)
}
