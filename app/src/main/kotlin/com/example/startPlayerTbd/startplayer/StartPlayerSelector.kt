package com.example.startPlayerTbd.startplayer

import kotlin.random.Random

fun interface SelectionRandomSource {
    fun nextInt(bound: Int): Int
}

class StartPlayerSelector(
    private val randomSource: SelectionRandomSource,
) {
    fun select(pointerIds: List<Int>, count: Int): List<Int> {
        val remaining = pointerIds.toMutableList()
        return buildList {
            repeat(count) {
                add(remaining.removeAt(randomSource.nextInt(remaining.size)))
            }
        }
    }
}

class KotlinSelectionRandomSource(
    private val random: Random = Random.Default,
) : SelectionRandomSource {
    override fun nextInt(bound: Int): Int = random.nextInt(bound)
}
