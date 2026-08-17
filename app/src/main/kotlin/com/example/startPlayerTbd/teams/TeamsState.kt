package com.example.startPlayerTbd.teams

import com.example.startPlayerTbd.startplayer.TouchPosition

data class TeamsState(
    val selectedTeamCount: Int,
    val recognizedPointerIds: List<Int>,
    val pointerPositions: Map<Int, TouchPosition>,
    val assignments: Map<Int, List<Int>>,
    val resultPositions: Map<Int, TouchPosition>,
    val countdownRemainingMillis: Long?,
    val retentionRemainingMillis: Long?,
) {
    val teamCountText get() = "Teams: $selectedTeamCount"
    val minimumPlayersText get() = "Place at least $selectedTeamCount fingers"
    val detectedPlayersText get() = "Players detected: ${recognizedPointerIds.size}"
    val maximumPlayersText get() = if (recognizedPointerIds.size == MAXIMUM_PLAYERS) "Maximum 9 players supported" else null
    val countdownText get() = countdownRemainingMillis?.let { "Hold fingers in place: ${(it + 999) / 1_000}" }
    val resultText get() = assignments.takeIf { it.isNotEmpty() }?.let { "$selectedTeamCount teams created" }
    val canDecreaseTeamCount get() = selectedTeamCount > MINIMUM_TEAMS
    val canIncreaseTeamCount get() = selectedTeamCount < MAXIMUM_TEAMS

    fun setTeamCount(count: Int) = copy(selectedTeamCount = count.coerceIn(MINIMUM_TEAMS, MAXIMUM_TEAMS))

    fun onPointerAdded(pointerId: Int): TeamsState {
        if (pointerId in recognizedPointerIds || recognizedPointerIds.size >= MAXIMUM_PLAYERS) return this
        if (retentionRemainingMillis != null) {
            return copy(
                recognizedPointerIds = listOf(pointerId),
                pointerPositions = emptyMap(),
                assignments = emptyMap(),
                resultPositions = emptyMap(),
                countdownRemainingMillis = null,
                retentionRemainingMillis = null,
            )
        }
        if (assignments.isNotEmpty()) return this
        val pointers = recognizedPointerIds + pointerId
        return copy(
            recognizedPointerIds = pointers,
            countdownRemainingMillis = if (pointers.size >= selectedTeamCount) SETTLING_DELAY else null,
        )
    }

    fun onPointerRemoved(pointerId: Int): TeamsState {
        val pointers = recognizedPointerIds - pointerId
        return copy(
            recognizedPointerIds = pointers,
            countdownRemainingMillis = when {
                assignments.isNotEmpty() -> null
                countdownRemainingMillis != null && pointers.size >= selectedTeamCount -> SETTLING_DELAY
                else -> null
            },
            retentionRemainingMillis =
                if (assignments.isNotEmpty() && pointers.isEmpty()) RESULT_RETENTION else retentionRemainingMillis,
        )
    }

    fun onPointerMoved(pointerId: Int, position: TouchPosition) =
        if (assignments.isEmpty()) copy(pointerPositions = pointerPositions + (pointerId to position)) else this

    fun withAssignments(value: Map<Int, List<Int>>) = copy(
        assignments = value,
        resultPositions = pointerPositions,
        countdownRemainingMillis = null,
    )

    fun advanceTime(milliseconds: Long): TeamsState {
        if (retentionRemainingMillis != null && milliseconds >= retentionRemainingMillis) {
            return copy(
                pointerPositions = emptyMap(),
                assignments = emptyMap(),
                resultPositions = emptyMap(),
                retentionRemainingMillis = null,
            )
        }
        return copy(
            countdownRemainingMillis = countdownRemainingMillis?.let { (it - milliseconds).coerceAtLeast(0) },
            retentionRemainingMillis = retentionRemainingMillis?.let { (it - milliseconds).coerceAtLeast(0) },
        )
    }

    companion object {
        const val MINIMUM_TEAMS = 2
        const val MAXIMUM_TEAMS = 9
        private const val MAXIMUM_PLAYERS = 9
        private const val SETTLING_DELAY = 2_000L
        private const val RESULT_RETENTION = 3_000L
        val selectableTeamCounts = (MINIMUM_TEAMS..MAXIMUM_TEAMS).toList()

        fun initial() = TeamsState(2, emptyList(), emptyMap(), emptyMap(), emptyMap(), null, null)
    }
}
