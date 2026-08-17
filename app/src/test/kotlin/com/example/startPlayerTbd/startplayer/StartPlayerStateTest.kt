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

    @Test
    fun `AC7 adding a pointer restarts active countdown`() {
        val countingDown =
            StartPlayerState.initial()
                .setSelectionCount(3)
                .onPointerAdded(11)
                .onPointerAdded(22)
                .onPointerAdded(33)
                .onPointerAdded(44)
                .advanceTime(800)
        assertEquals(1_200L, countingDown.countdownRemainingMillis)

        val restarted = countingDown.onPointerAdded(55)
        assertEquals(2_000L, restarted.countdownRemainingMillis)
        assertEquals("Hold fingers in place: 2", restarted.countdownText)
    }

    @Test
    fun `AC8 removing a pointer restarts countdown when enough remain`() {
        val countingDown =
            StartPlayerState.initial()
                .setSelectionCount(2)
                .onPointerAdded(11)
                .onPointerAdded(22)
                .onPointerAdded(33)
                .onPointerAdded(44)
                .advanceTime(800)

        val restarted = countingDown.onPointerRemoved(44)

        assertEquals(2_000L, restarted.countdownRemainingMillis)
        assertEquals("Hold fingers in place: 2", restarted.countdownText)
    }

    @Test
    fun `AC9 removing below required count cancels countdown`() {
        val state =
            StartPlayerState.initial()
                .setSelectionCount(2)
                .onPointerAdded(11)
                .onPointerAdded(22)
                .onPointerAdded(33)
                .onPointerRemoved(33)

        assertEquals(null, state.countdownRemainingMillis)
        assertEquals(emptyList<Int>(), state.selectedPointerIds)
        assertEquals("Place at least 3 fingers", state.minimumPlayersText)
    }

    @Test
    fun `AC10 pointer movement updates position without restarting countdown`() {
        val countingDown =
            StartPlayerState.initial()
                .setSelectionCount(3)
                .onPointerAdded(11)
                .onPointerAdded(22)
                .onPointerAdded(33)
                .onPointerAdded(44)
                .advanceTime(800)

        val moved = countingDown.onPointerMoved(11, TouchPosition(30f, 40f))

        assertEquals(1_200L, moved.countdownRemainingMillis)
        assertEquals(TouchPosition(30f, 40f), moved.pointerPositions[11])
    }

    @Test
    fun `AC14 selected result position remains frozen after pointer movement`() {
        val result =
            StartPlayerState.result(
                selectedPointerIds = listOf(33),
                resultPositions = mapOf(33 to TouchPosition(120f, 400f)),
            )

        val moved = result.onPointerMoved(33, TouchPosition(220f, 500f))

        assertEquals(listOf(33), moved.selectedPointerIds)
        assertEquals(TouchPosition(120f, 400f), moved.resultPositions[33])
        assertEquals(null, moved.countdownRemainingMillis)
    }

    @Test
    fun `AC15 result remains while at least one finger is active`() {
        val result =
            StartPlayerState.result(
                selectedPointerIds = listOf(33, 11),
                resultPositions =
                    mapOf(
                        33 to TouchPosition(120f, 400f),
                        11 to TouchPosition(220f, 500f),
                    ),
            )

        val afterLift = result.onPointerRemoved(11)

        assertEquals(listOf(33, 11), afterLift.selectedPointerIds)
        assertEquals("2 starting players selected", afterLift.resultText)
    }

    @Test
    fun `AC16 final lift retains result through 2999 milliseconds`() {
        val result =
            StartPlayerState.result(
                selectedPointerIds = listOf(33, 11),
                resultPositions =
                    mapOf(
                        33 to TouchPosition(120f, 400f),
                        11 to TouchPosition(220f, 500f),
                    ),
            ).onPointerRemoved(33).onPointerRemoved(11)

        assertEquals(3_000L, result.retentionRemainingMillis)
        val retained = result.advanceTime(2_999)
        assertEquals(listOf(33, 11), retained.selectedPointerIds)
        assertEquals("2 starting players selected", retained.resultText)
        assertEquals(1L, retained.retentionRemainingMillis)
    }

    @Test
    fun `AC17 final retention millisecond resets result and preserves configuration`() {
        val retaining =
            StartPlayerState.result(
                selectedPointerIds = listOf(33, 11),
                resultPositions =
                    mapOf(
                        33 to TouchPosition(120f, 400f),
                        11 to TouchPosition(220f, 500f),
                    ),
            ).setSelectionCount(3)
                .onPointerRemoved(33)
                .onPointerRemoved(11)
                .advanceTime(2_999)

        val reset = retaining.advanceTime(1)

        assertEquals(emptyList<Int>(), reset.selectedPointerIds)
        assertEquals(emptyMap<Int, TouchPosition>(), reset.resultPositions)
        assertEquals("Players detected: 0", reset.detectedPlayersText)
        assertEquals(3, reset.selectedStartingPlayerCount)
    }

    @Test
    fun `AC18 new pointer during retention starts a fresh collection`() {
        val retaining =
            StartPlayerState.result(
                selectedPointerIds = listOf(33, 11),
                resultPositions =
                    mapOf(
                        33 to TouchPosition(120f, 400f),
                        11 to TouchPosition(220f, 500f),
                    ),
            ).setSelectionCount(2)
                .onPointerRemoved(33)
                .onPointerRemoved(11)

        val fresh = retaining.onPointerAdded(55)

        assertEquals(emptyList<Int>(), fresh.selectedPointerIds)
        assertEquals(null, fresh.retentionRemainingMillis)
        assertEquals(listOf(55), fresh.recognizedPointerIds)
        assertEquals("Players detected: 1", fresh.detectedPlayersText)
        assertEquals("Place at least 3 fingers", fresh.minimumPlayersText)
    }

    @Test
    fun `AC21 in process reset preserves configured count and guidance`() {
        val reset =
            StartPlayerState.result(
                selectedPointerIds = listOf(33),
                resultPositions = mapOf(33 to TouchPosition(120f, 400f)),
            ).setSelectionCount(3)
                .onPointerRemoved(33)
                .advanceTime(3_000)

        assertEquals(3, reset.selectedStartingPlayerCount)
        assertEquals("Starting players: 3", reset.startingPlayerCountText)
        assertEquals("Place at least 4 fingers", reset.minimumPlayersText)
    }

    @Test
    fun `AC22 new process state does not retain feature state`() {
        StartPlayerState.result(
            selectedPointerIds = listOf(33),
            resultPositions = mapOf(33 to TouchPosition(120f, 400f)),
        ).setSelectionCount(3)

        val newProcessState = StartPlayerState.initial()

        assertEquals(1, newProcessState.selectedStartingPlayerCount)
        assertEquals(emptyList<Int>(), newProcessState.recognizedPointerIds)
        assertEquals(emptyList<Int>(), newProcessState.selectedPointerIds)
        assertEquals(emptyMap<Int, TouchPosition>(), newProcessState.resultPositions)
        assertEquals("Players detected: 0", newProcessState.detectedPlayersText)
    }
}
