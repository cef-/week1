package com.example.startPlayerTbd.startplayer

import org.junit.Assert.assertEquals
import org.junit.Test

class StartPlayerStateTest {
    @Test
    fun `AC1 default number of starting players is one`() {
        val state = StartPlayerState.initial()

        assertEquals(1, state.selectedStartingPlayerCount)
        assertEquals("Starting players: 1", state.startingPlayerCountText)
    }

    @Test
    fun `AC2 increasing count to three updates count and guidance`() {
        val state = StartPlayerState.initial().setSelectionCount(3)

        assertEquals(3, state.selectedStartingPlayerCount)
        assertEquals("Starting players: 3", state.startingPlayerCountText)
        assertEquals("Place at least 4 fingers", state.minimumPlayersText)
    }
}
