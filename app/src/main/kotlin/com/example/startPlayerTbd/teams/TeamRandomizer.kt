package com.example.startPlayerTbd.teams

fun interface TeamRandomSource {
    fun nextInt(bound: Int): Int
}

class TeamRandomizer(
    private val randomSource: TeamRandomSource,
) {
    fun shuffle(pointerIds: List<Int>): List<Int> {
        val shuffled = pointerIds.toMutableList()
        for (index in shuffled.lastIndex downTo 1) {
            val replacementIndex = randomSource.nextInt(index + 1)
            val value = shuffled[index]
            shuffled[index] = shuffled[replacementIndex]
            shuffled[replacementIndex] = value
        }
        return shuffled
    }

    fun assign(pointerIds: List<Int>, teamCount: Int): Map<Int, List<Int>> {
        require(teamCount in 2..pointerIds.size)
        val teams = (1..teamCount).associateWith { mutableListOf<Int>() }
        shuffle(pointerIds).forEachIndexed { index, pointerId ->
            teams.getValue(index % teamCount + 1).add(pointerId)
        }
        return teams.mapValues { it.value.toList() }
    }
}

class KotlinTeamRandomSource : TeamRandomSource {
    override fun nextInt(bound: Int): Int = kotlin.random.Random.Default.nextInt(bound)
}
