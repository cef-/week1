package com.example.startPlayerTbd.teams

import com.example.startPlayerTbd.startplayer.TouchPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TeamsStateTest {
    @Test fun AC1_default_is_two_teams() = with(TeamsState.initial()) {
        assertEquals(2, selectedTeamCount); assertEquals("Teams: 2", teamCountText)
    }

    @Test fun AC2_configures_four_teams() = with(TeamsState.initial().setTeamCount(4)) {
        assertEquals(4, selectedTeamCount); assertEquals("Teams: 4", teamCountText)
        assertEquals("Place at least 4 fingers", minimumPlayersText)
    }

    @Test fun AC3_enforces_two_through_nine() {
        assertEquals((2..9).toList(), TeamsState.selectableTeamCounts)
        with(TeamsState.initial().setTeamCount(1)) { assertEquals(2, selectedTeamCount); assertFalse(canDecreaseTeamCount) }
        with(TeamsState.initial().setTeamCount(10)) { assertEquals(9, selectedTeamCount); assertFalse(canIncreaseTeamCount) }
    }

    @Test fun AC5_to_AC10_countdown_membership_and_movement_rules() {
        var state = TeamsState.initial().setTeamCount(3).onPointerAdded(1).onPointerAdded(2)
        assertNull(state.countdownRemainingMillis)
        state = state.onPointerAdded(3)
        assertEquals(2_000L, state.countdownRemainingMillis)
        state = state.advanceTime(800).onPointerAdded(4)
        assertEquals(2_000L, state.countdownRemainingMillis)
        state = state.advanceTime(800).onPointerRemoved(4)
        assertEquals(2_000L, state.countdownRemainingMillis)
        state = state.onPointerMoved(1, TouchPosition(5f, 6f))
        assertEquals(2_000L, state.countdownRemainingMillis)
        assertEquals(TouchPosition(5f, 6f), state.pointerPositions[1])
        state = state.onPointerRemoved(3)
        assertNull(state.countdownRemainingMillis)
    }

    @Test fun AC15_to_AC19_result_freeze_retention_and_reset() {
        var state = TeamsState.initial().setTeamCount(3)
            .onPointerAdded(1).onPointerMoved(1, TouchPosition(10f, 20f))
            .onPointerAdded(2).onPointerAdded(3)
            .withAssignments(mapOf(1 to listOf(1), 2 to listOf(2), 3 to listOf(3)))
        state = state.onPointerMoved(1, TouchPosition(30f, 40f))
        assertEquals(TouchPosition(10f, 20f), state.resultPositions[1])
        state = state.onPointerRemoved(1).onPointerRemoved(2).onPointerRemoved(3)
        assertEquals(3_000L, state.retentionRemainingMillis)
        assertTrue(state.advanceTime(2_999).assignments.isNotEmpty())
        val reset = state.advanceTime(3_000)
        assertTrue(reset.assignments.isEmpty()); assertEquals(3, reset.selectedTeamCount)
        val fresh = state.onPointerAdded(88)
        assertTrue(fresh.assignments.isEmpty()); assertNull(fresh.retentionRemainingMillis)
        assertEquals(listOf(88), fresh.recognizedPointerIds)
    }

    @Test fun AC23_new_process_restores_defaults() {
        val state = TeamsState.initial()
        assertEquals(2, state.selectedTeamCount); assertTrue(state.assignments.isEmpty())
        assertEquals("Players detected: 0", state.detectedPlayersText)
    }
}
