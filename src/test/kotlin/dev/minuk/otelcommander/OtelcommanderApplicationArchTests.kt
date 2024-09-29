package dev.minuk.otelcommander

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.junit.jupiter.api.Test

class OtelcommanderApplicationArchTests {
    @Test
    fun `otelcommander follows onion architecture`() {
        val classes = ClassFileImporter().importPackages("dev.minuk.otelcommander")
        val rule =
            onionArchitecture()
                .domainModels("dev.minuk.otelcommander.domain.models..")
                .domainServices(
                    "dev.minuk.otelcommander.domain.services..", // implementations
                    "dev.minuk.otelcommander.domain.port.primary..", // interfaces
                ).applicationServices(
                    "dev.minuk.otelcommander.application.services..",
                    "dev.minuk.otelcommander.application.usecases..",
                ).adapter("http", "dev.minuk.otelcommander.adapter.primary.http..")
                .adapter("persistence", "dev.minuk.otelcommander.adapter.secondary.persistence..")

        rule.check(classes)
    }
}
