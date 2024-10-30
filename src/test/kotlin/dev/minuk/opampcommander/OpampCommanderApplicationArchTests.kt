package dev.minuk.opampcommander

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.junit.jupiter.api.Test

class OpampCommanderApplicationArchTests {
    @Test
    fun `opampcommander follows onion architecture`() {
        val classes =
            ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages("dev.minuk.opampcommander")
        val rule =
            onionArchitecture()
                .domainModels("dev.minuk.opampcommander.domain.models..")
                .domainServices(
                    "dev.minuk.opampcommander.domain.services..", // implementations
                    "dev.minuk.opampcommander.domain.port.primary..", // interfaces
                    "dev.minuk.opampcommander.domain.port.secondary..", // interfaces
                ).applicationServices(
                    "dev.minuk.opampcommander.application.services..",
                    "dev.minuk.opampcommander.application.usecases..",
                ).adapter("http", "dev.minuk.opampcommander.adapter.primary.http..")
                .adapter("ws", "dev.minuk.opampcommander.adapter.primary.ws..")
                .adapter("persistence", "dev.minuk.opampcommander.adapter.secondary.persistence..")

        rule.check(classes)
    }
}
