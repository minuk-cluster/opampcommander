package dev.minuk.otelcommander.domain.models.agent

data class CommunicationStatus(
    val sequenceNum: Int,
) {
    companion object {
        fun empty(): CommunicationStatus {
            return CommunicationStatus(
                sequenceNum = 0,
            )
        }
    }
}