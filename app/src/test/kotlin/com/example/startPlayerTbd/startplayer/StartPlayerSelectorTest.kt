package com.example.startPlayerTbd.startplayer

import org.junit.Assert.assertEquals
import org.junit.Test

class StartPlayerSelectorTest {
    @Test
    fun `AC11 selects configured distinct pointer IDs`() {
        val randomSource = SequenceRandomSource(2, 0)
        val selector = StartPlayerSelector(randomSource)

        val selected = selector.select(listOf(11, 22, 33, 44), 2)

        assertEquals(listOf(33, 11), selected)
        assertEquals(2, selected.distinct().size)
        assertEquals(setOf(22, 44), listOf(11, 22, 33, 44).toSet() - selected.toSet())
    }

    @Test
    fun `AC12 every valid subset has equal probability`() {
        for (playerCount in 2..9) {
            for (selectionCount in 1 until playerCount) {
                val pointerIds = (1..playerCount).toList()
                val bounds = List(selectionCount) { draw -> playerCount - draw }
                val subsetFrequencies =
                    randomStreams(bounds)
                        .map { stream ->
                            StartPlayerSelector(SequenceRandomSource(*stream.toIntArray()))
                                .select(pointerIds, selectionCount)
                        }
                        .onEach { selected ->
                            assertEquals(selectionCount, selected.size)
                            assertEquals(selectionCount, selected.distinct().size)
                        }
                        .groupingBy { selected -> selected.toSet() }
                        .eachCount()

                assertEquals(setOf(factorial(selectionCount)), subsetFrequencies.values.toSet())
                assertEquals(combinations(playerCount, selectionCount), subsetFrequencies.size)
            }
        }
    }
}

private fun randomStreams(bounds: List<Int>): List<List<Int>> =
    bounds.fold(listOf(emptyList())) { streams, bound ->
        streams.flatMap { stream -> (0 until bound).map { value -> stream + value } }
    }

private fun factorial(value: Int): Int = (1..value).fold(1, Int::times)

private fun combinations(total: Int, selected: Int): Int =
    factorial(total) / (factorial(selected) * factorial(total - selected))

private class SequenceRandomSource(vararg values: Int) : SelectionRandomSource {
    private val iterator = values.iterator()

    override fun nextInt(bound: Int): Int = iterator.nextInt()
}
