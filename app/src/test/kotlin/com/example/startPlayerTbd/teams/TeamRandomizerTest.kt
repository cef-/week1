package com.example.startPlayerTbd.teams

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TeamRandomizerTest {
    @Test
    fun AC11_assigns_controlled_shuffle_round_robin() {
        val randomizer = TeamRandomizer(SequenceTeamRandomSource(4, 2, 2, 1, 2, 0))

        val teams = randomizer.assign(listOf(11, 22, 33, 44, 55, 66, 77), 3)

        assertEquals(mapOf(1 to listOf(44, 22, 55), 2 to listOf(11, 66), 3 to listOf(77, 33)), teams)
    }

    @Test
    fun AC12_every_supported_assignment_is_balanced_and_nonempty() {
        for (players in 2..9) for (teams in 2..players) {
            val assignment = TeamRandomizer { 0 }.assign((1..players).toList(), teams)
            val sizes = assignment.values.map(List<Int>::size)
            assertEquals(teams, assignment.size)
            assertTrue(sizes.all { it >= 1 })
            assertTrue(sizes.max() - sizes.min() <= 1)
            assertEquals((1..players).toSet(), assignment.values.flatten().toSet())
        }
    }

    @Test
    fun AC13_shuffle_contains_each_input_once() {
        for (players in 2..9) {
            val input = (1..players).toList()
            val outcomes = mutableMapOf<List<Int>, Int>()
            shuffleDecisionStreams(players).forEach { decisions ->
                val shuffled = TeamRandomizer(SequenceTeamRandomSource(*decisions.toIntArray())).shuffle(input)
                assertEquals(input.toSet(), shuffled.toSet())
                assertEquals(players, shuffled.distinct().size)
                outcomes[shuffled] = outcomes.getOrDefault(shuffled, 0) + 1
            }
            assertEquals(factorial(players), outcomes.size)
            assertTrue(outcomes.values.all { it == 1 })
        }
    }

    private fun shuffleDecisionStreams(players: Int): List<List<Int>> {
        var streams = listOf(emptyList<Int>())
        for (bound in players downTo 2) {
            streams = streams.flatMap { stream -> (0 until bound).map { stream + it } }
        }
        return streams
    }

    private fun factorial(value: Int) = (2..value).fold(1, Int::times)
}

private class SequenceTeamRandomSource(vararg values: Int) : TeamRandomSource {
    private val iterator = values.iterator()
    override fun nextInt(bound: Int) = iterator.next().coerceIn(0, bound - 1)
}
