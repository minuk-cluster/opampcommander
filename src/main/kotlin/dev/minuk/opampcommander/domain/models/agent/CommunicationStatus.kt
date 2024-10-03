package dev.minuk.opampcommander.domain.models.agent

data class CommunicationStatus(
    val sequenceNum: Int,
) {
    companion object {
        fun empty(): CommunicationStatus =
            CommunicationStatus(
                sequenceNum = 0,
            )
    }
}
