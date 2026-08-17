package com.example.startPlayerTbd.startplayer

data class StartPlayerState(
    val selectedStartingPlayerCount: Int,
    val startingPlayerCountText: String,
    val minimumPlayersText: String,
) {
    fun setSelectionCount(count: Int): StartPlayerState =
        copy(
            selectedStartingPlayerCount = count,
            startingPlayerCountText = "Starting players: $count",
            minimumPlayersText = "Place at least ${count + 1} fingers",
        )

    companion object {
        fun initial(): StartPlayerState =
            StartPlayerState(
                selectedStartingPlayerCount = 1,
                startingPlayerCountText = "Starting players: 1",
                minimumPlayersText = "Place at least 2 fingers",
            )
    }
}
