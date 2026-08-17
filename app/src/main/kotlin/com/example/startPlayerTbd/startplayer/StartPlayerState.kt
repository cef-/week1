package com.example.startPlayerTbd.startplayer

data class StartPlayerState(
    val selectedStartingPlayerCount: Int,
    val startingPlayerCountText: String,
    val minimumPlayersText: String,
    val recognizedPointerIds: List<Int>,
    val selectedPointerIds: List<Int>,
    val countdownRemainingMillis: Long?,
) {
    val canDecreaseSelectionCount: Boolean
        get() = selectedStartingPlayerCount > MINIMUM_SELECTION_COUNT

    val canIncreaseSelectionCount: Boolean
        get() = selectedStartingPlayerCount < MAXIMUM_SELECTION_COUNT

    val countdownText: String?
        get() = countdownRemainingMillis?.let { "Hold fingers in place: ${(it + 999) / 1_000}" }

    fun setSelectionCount(count: Int): StartPlayerState =
        count.coerceIn(MINIMUM_SELECTION_COUNT, MAXIMUM_SELECTION_COUNT).let { boundedCount ->
            copy(
                selectedStartingPlayerCount = boundedCount,
                startingPlayerCountText = "Starting players: $boundedCount",
                minimumPlayersText = "Place at least ${boundedCount + 1} fingers",
            )
        }

    fun onPointerAdded(pointerId: Int): StartPlayerState {
        val updatedPointers = recognizedPointerIds + pointerId
        val requiredPlayers = selectedStartingPlayerCount + 1
        return copy(
            recognizedPointerIds = updatedPointers,
            countdownRemainingMillis =
                if (countdownRemainingMillis != null ||
                    (recognizedPointerIds.size < requiredPlayers && updatedPointers.size == requiredPlayers)
                ) {
                    SETTLING_DELAY_MILLIS
                } else {
                    countdownRemainingMillis
                },
        )
    }

    fun onPointerRemoved(pointerId: Int): StartPlayerState {
        val updatedPointers = recognizedPointerIds - pointerId
        return copy(
            recognizedPointerIds = updatedPointers,
            countdownRemainingMillis =
                if (countdownRemainingMillis != null &&
                    updatedPointers.size >= selectedStartingPlayerCount + 1
                ) {
                    SETTLING_DELAY_MILLIS
                } else {
                    countdownRemainingMillis
                },
        )
    }

    fun advanceTime(milliseconds: Long): StartPlayerState =
        copy(
            countdownRemainingMillis =
                countdownRemainingMillis?.let { remaining ->
                    (remaining - milliseconds).coerceAtLeast(0)
                },
        )

    companion object {
        private const val MINIMUM_SELECTION_COUNT = 1
        private const val MAXIMUM_SELECTION_COUNT = 8
        private const val SETTLING_DELAY_MILLIS = 2_000L

        val selectableStartingPlayerCounts: List<Int> =
            (MINIMUM_SELECTION_COUNT..MAXIMUM_SELECTION_COUNT).toList()

        fun initial(): StartPlayerState =
            StartPlayerState(
                selectedStartingPlayerCount = 1,
                startingPlayerCountText = "Starting players: 1",
                minimumPlayersText = "Place at least 2 fingers",
                recognizedPointerIds = emptyList(),
                selectedPointerIds = emptyList(),
                countdownRemainingMillis = null,
            )
    }
}
