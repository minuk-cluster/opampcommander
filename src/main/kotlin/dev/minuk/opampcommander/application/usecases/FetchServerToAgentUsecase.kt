package dev.minuk.opampcommander.application.usecases

import com.github.f4b6a3.ulid.Ulid
import com.google.protobuf.ByteString
import opamp.proto.Opamp

interface FetchServerToAgentUsecase {
    suspend fun fetchServerToAgent(instanceUid: ByteString): Opamp.ServerToAgent? = fetchServerToAgent(Ulid.from(instanceUid.toByteArray()))

    suspend fun fetchServerToAgent(instanceUid: Ulid): Opamp.ServerToAgent?
}
