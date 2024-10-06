package dev.minuk.opampcommander.domain.services

import com.github.f4b6a3.ulid.Ulid
import dev.minuk.opampcommander.domain.models.command.RawCommand
import dev.minuk.opampcommander.domain.port.primary.agent.GetCommandInternalUsecase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.springframework.stereotype.Service

@Service
class CommandService : GetCommandInternalUsecase {
    override suspend fun getCommands(instanceUid: Ulid): Flow<RawCommand> = emptyFlow()
}
