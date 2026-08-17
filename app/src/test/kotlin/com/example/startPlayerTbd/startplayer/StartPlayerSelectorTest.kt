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
}

private class SequenceRandomSource(vararg values: Int) : SelectionRandomSource {
    private val iterator = values.iterator()

    override fun nextInt(bound: Int): Int = iterator.nextInt()
}
