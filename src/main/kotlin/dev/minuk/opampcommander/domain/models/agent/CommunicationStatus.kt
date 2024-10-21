package dev.minuk.opampcommander.domain.models.agent

data class CommunicationStatus(
    val sequenceNum: Long,
) {
    companion object {
        fun empty(): CommunicationStatus =
            CommunicationStatus(
                sequenceNum = 0,
            )
    }
}
