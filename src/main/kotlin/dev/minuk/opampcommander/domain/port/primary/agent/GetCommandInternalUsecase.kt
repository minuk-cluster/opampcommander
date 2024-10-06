package dev.minuk.opampcommander.domain.port.primary.agent

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.domain.models.command.RawCommand
import kotlinx.coroutines.flow.Flow

interface GetCommandInternalUsecase {
    suspend fun getCommands(instanceUid: Ulid): Flow<RawCommand>
}
