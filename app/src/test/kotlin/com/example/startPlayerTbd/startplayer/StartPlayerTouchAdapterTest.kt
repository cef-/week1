package com.example.startPlayerTbd.startplayer

import org.junit.Assert.assertEquals
import org.junit.Test

class StartPlayerTouchAdapterTest {
    @Test
    fun `AC4 four active pointers produce four positioned indicators`() {
        val pointers =
            listOf(
                ReportedPointer(11, TouchPosition(10f, 20f)),
                ReportedPointer(22, TouchPosition(30f, 40f)),
                ReportedPointer(33, TouchPosition(50f, 60f)),
                ReportedPointer(44, TouchPosition(70f, 80f)),
            )

        val state = StartPlayerTouchAdapter().onPointers(pointers)

        assertEquals("Players detected: 4", state.detectedPlayersText)
        assertEquals(4, state.touchIndicators.size)
        assertEquals(
            pointers.map { pointer -> pointer.position },
            state.touchIndicators.map { indicator -> indicator.center },
        )
    }

    @Test
    fun `AC19 gesture cancellation clears collection and countdown`() {
        val active =
            StartPlayerState.initial()
                .setSelectionCount(2)
                .onPointerAdded(11)
                .onPointerAdded(22)
                .onPointerAdded(33)

        val canceled = StartPlayerTouchAdapter().onCancel(active)

        assertEquals(emptyList<Int>(), canceled.recognizedPointerIds)
        assertEquals(emptyList<Int>(), canceled.selectedPointerIds)
        assertEquals(null, canceled.countdownRemainingMillis)
        assertEquals("Players detected: 0", canceled.detectedPlayersText)
    }

    @Test
    fun `AC20 tenth pointer is excluded from recognized players`() {
        val pointers =
            (1..10).map { pointerId ->
                ReportedPointer(pointerId, TouchPosition(pointerId.toFloat(), pointerId.toFloat()))
            }

        val state = StartPlayerTouchAdapter().onPointers(pointers)

        assertEquals(9, state.touchIndicators.size)
        assertEquals((1..9).toList(), state.touchIndicators.map { it.pointerId })
        assertEquals("Maximum 9 players supported", state.maximumPlayersText)
    }
}
