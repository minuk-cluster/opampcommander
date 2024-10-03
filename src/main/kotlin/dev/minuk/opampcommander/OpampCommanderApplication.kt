package dev.minuk.opampcommander

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OpampCommanderApplication

fun main(args: Array<String>) {
    runApplication<OpampCommanderApplication>(*args)
}
