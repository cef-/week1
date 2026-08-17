package com.example.startPlayerTbd.teams

import com.example.startPlayerTbd.startplayer.ReportedPointer
import com.example.startPlayerTbd.startplayer.TouchPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TeamsTouchAdapterTest {
    @Test fun AC4_displays_each_recognized_pointer_at_its_position() {
        val pointers = (1..4).map { ReportedPointer(it, TouchPosition(it.toFloat(), (it * 2).toFloat())) }
        val result = TeamsTouchAdapter().onPointers(pointers)
        assertEquals("Players detected: 4", result.detectedPlayersText)
        assertEquals(pointers.map { it.position }, result.touchIndicators.map { it.center })
    }

    @Test fun AC20_cancellation_clears_collection_and_countdown() {
        val state = TeamsState.initial().onPointerAdded(1).onPointerAdded(2)
        val canceled = TeamsTouchAdapter().onCancel(state)
        assertTrue(canceled.recognizedPointerIds.isEmpty()); assertEquals(null, canceled.countdownRemainingMillis)
    }

    @Test fun AC21_tenth_pointer_is_excluded() {
        val result = TeamsTouchAdapter().onPointers((1..10).map { ReportedPointer(it, TouchPosition(0f, 0f)) })
        assertEquals(9, result.touchIndicators.size)
        assertEquals("Maximum 9 players supported", result.maximumPlayersText)
    }
}
