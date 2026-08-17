package com.example.startPlayerTbd.startplayer

data class StartPlayerState(
    val selectedStartingPlayerCount: Int,
    val startingPlayerCountText: String,
    val minimumPlayersText: String,
) {
    val canDecreaseSelectionCount: Boolean
        get() = selectedStartingPlayerCount > MINIMUM_SELECTION_COUNT

    val canIncreaseSelectionCount: Boolean
        get() = selectedStartingPlayerCount < MAXIMUM_SELECTION_COUNT

    fun setSelectionCount(count: Int): StartPlayerState =
        count.coerceIn(MINIMUM_SELECTION_COUNT, MAXIMUM_SELECTION_COUNT).let { boundedCount ->
            copy(
                selectedStartingPlayerCount = boundedCount,
                startingPlayerCountText = "Starting players: $boundedCount",
                minimumPlayersText = "Place at least ${boundedCount + 1} fingers",
            )
        }

    companion object {
        private const val MINIMUM_SELECTION_COUNT = 1
        private const val MAXIMUM_SELECTION_COUNT = 8

        val selectableStartingPlayerCounts: List<Int> =
            (MINIMUM_SELECTION_COUNT..MAXIMUM_SELECTION_COUNT).toList()

        fun initial(): StartPlayerState =
            StartPlayerState(
                selectedStartingPlayerCount = 1,
                startingPlayerCountText = "Starting players: 1",
                minimumPlayersText = "Place at least 2 fingers",
            )
    }
}
