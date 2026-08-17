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

    @Test
    fun `AC3 selection count is limited to one through eight`() {
        assertEquals((1..8).toList(), StartPlayerState.selectableStartingPlayerCounts)

        val minimumState = StartPlayerState.initial().setSelectionCount(0)
        assertEquals(1, minimumState.selectedStartingPlayerCount)
        assertEquals(false, minimumState.canDecreaseSelectionCount)
        assertEquals(true, minimumState.canIncreaseSelectionCount)

        val maximumState = StartPlayerState.initial().setSelectionCount(9)
        assertEquals(8, maximumState.selectedStartingPlayerCount)
        assertEquals(true, maximumState.canDecreaseSelectionCount)
        assertEquals(false, maximumState.canIncreaseSelectionCount)
    }

    @Test
    fun `AC5 insufficient players do not start countdown or selection`() {
        val state =
            StartPlayerState.initial()
                .setSelectionCount(3)
                .onPointerAdded(11)
                .onPointerAdded(22)
                .onPointerAdded(33)
                .advanceTime(2_000)

        assertEquals(emptyList<Int>(), state.selectedPointerIds)
        assertEquals(null, state.countdownRemainingMillis)
        assertEquals("Place at least 4 fingers", state.minimumPlayersText)
    }

    @Test
    fun `AC6 required player count starts two second countdown`() {
        val state =
            StartPlayerState.initial()
                .setSelectionCount(3)
                .onPointerAdded(11)
                .onPointerAdded(22)
                .onPointerAdded(33)
                .onPointerAdded(44)

        assertEquals(2_000L, state.countdownRemainingMillis)
        assertEquals("Hold fingers in place: 2", state.countdownText)
    }
}
