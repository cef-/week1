package com.example.startPlayerTbd.teams

import com.example.startPlayerTbd.startplayer.ReportedPointer
import com.example.startPlayerTbd.startplayer.TouchIndicator

data class TeamsTouchState(
    val detectedPlayersText: String,
    val touchIndicators: List<TouchIndicator>,
    val maximumPlayersText: String?,
)

class TeamsTouchAdapter {
    fun onPointers(pointers: List<ReportedPointer>): TeamsTouchState {
        val recognized = pointers.take(9)
        return TeamsTouchState(
            detectedPlayersText = "Players detected: ${recognized.size}",
            touchIndicators = recognized.map { TouchIndicator(it.id, it.position) },
            maximumPlayersText = if (pointers.size > 9) "Maximum 9 players supported" else null,
        )
    }

    fun onCancel(state: TeamsState) = state.copy(
        recognizedPointerIds = emptyList(),
        pointerPositions = emptyMap(),
        assignments = emptyMap(),
        resultPositions = emptyMap(),
        countdownRemainingMillis = null,
        retentionRemainingMillis = null,
    )
}
