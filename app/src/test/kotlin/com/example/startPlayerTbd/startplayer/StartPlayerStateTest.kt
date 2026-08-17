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
}
