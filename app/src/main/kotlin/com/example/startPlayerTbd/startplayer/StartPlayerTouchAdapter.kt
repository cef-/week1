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
)

class StartPlayerTouchAdapter {
    fun onPointers(pointers: List<ReportedPointer>): StartPlayerTouchState =
        StartPlayerTouchState(
            detectedPlayersText = "Players detected: ${pointers.size}",
            touchIndicators =
                pointers.map { pointer ->
                    TouchIndicator(
                        pointerId = pointer.id,
                        center = pointer.position,
                    )
                },
        )

    fun onCancel(state: StartPlayerState): StartPlayerState =
        state.copy(
            recognizedPointerIds = emptyList(),
            pointerPositions = emptyMap(),
            selectedPointerIds = emptyList(),
            resultPositions = emptyMap(),
            countdownRemainingMillis = null,
            retentionRemainingMillis = null,
        )
}
