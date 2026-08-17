package com.example.startPlayerTbd.startplayer

data class TouchPosition(
    val x: Float,
    val y: Float,
)

data class ReportedPointer(
    val id: Int,
    val position: TouchPosition,
)

data class TouchIndicator(
    val pointerId: Int,
    val center: TouchPosition,
)

data class StartPlayerTouchState(
    val detectedPlayersText: String,
    val touchIndicators: List<TouchIndicator>,
    val maximumPlayersText: String?,
)

class StartPlayerTouchAdapter {
    fun onPointers(pointers: List<ReportedPointer>): StartPlayerTouchState {
        val recognizedPointers = pointers.take(MAXIMUM_PLAYERS)
        return StartPlayerTouchState(
            detectedPlayersText = "Players detected: ${recognizedPointers.size}",
            touchIndicators =
                recognizedPointers.map { pointer ->
                    TouchIndicator(
                        pointerId = pointer.id,
                        center = pointer.position,
                    )
                },
            maximumPlayersText =
                if (pointers.size > MAXIMUM_PLAYERS) "Maximum 9 players supported" else null,
        )
    }

    fun onCancel(state: StartPlayerState): StartPlayerState =
        state.copy(
            recognizedPointerIds = emptyList(),
            pointerPositions = emptyMap(),
            selectedPointerIds = emptyList(),
            resultPositions = emptyMap(),
            countdownRemainingMillis = null,
            retentionRemainingMillis = null,
        )

    private companion object {
        const val MAXIMUM_PLAYERS = 9
    }
}
