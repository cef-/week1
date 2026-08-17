package com.example.startPlayerTbd.startplayer

data class StartPlayerState(
    val selectedStartingPlayerCount: Int,
    val startingPlayerCountText: String,
) {
    companion object {
        fun initial(): StartPlayerState =
            StartPlayerState(
                selectedStartingPlayerCount = 1,
                startingPlayerCountText = "Starting players: 1",
            )
    }
}
