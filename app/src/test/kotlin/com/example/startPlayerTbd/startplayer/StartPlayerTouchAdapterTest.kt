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
}
