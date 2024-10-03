package dev.minuk.opampcommander

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<OpampCommanderApplication>().with(TestcontainersConfiguration::class).run(*args)
}
