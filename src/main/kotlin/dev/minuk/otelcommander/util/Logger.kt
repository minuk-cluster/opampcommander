package dev.minuk.otelcommander.util

import org.slf4j.LoggerFactory

class Logger {
    private val logger: org.slf4j.Logger? = null

    operator fun getValue(
        thisRef: Any?,
        property: Any?,
    ) = logger ?: LoggerFactory.getLogger(thisRef?.javaClass)!!
}
