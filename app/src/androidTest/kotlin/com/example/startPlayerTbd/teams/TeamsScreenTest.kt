package com.example.startPlayerTbd.teams

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.startPlayerTbd.startplayer.TouchPosition
import org.junit.Rule
import org.junit.Test

class TeamsScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun AC14_each_team_uses_distinct_color_pattern_tokens_and_number() {
        val assignments = mapOf(1 to listOf(44, 22, 55), 2 to listOf(11, 66), 3 to listOf(77, 33))
        composeRule.setContent {
            TeamsScreen(
                state = TeamsState.initial().setTeamCount(3).withAssignments(assignments),
                indicators = assignments.flatMap { (team, ids) ->
                    ids.map { id -> TeamIndicatorUi(id, TouchPosition(id.toFloat(), id.toFloat()), team) }
                },
            )
        }

        val expectedTokens = mapOf(
            1 to ("blue" to "diagonal"),
            2 to ("red" to "reverse-diagonal"),
            3 to ("green" to "horizontal"),
        )
        assignments.forEach { (team, ids) ->
            ids.forEach { id ->
                composeRule.onNodeWithTag("team-pointer-$id")
                    .assert(SemanticsMatcher.expectValue(TeamColorTokenKey, expectedTokens.getValue(team).first))
                    .assert(SemanticsMatcher.expectValue(TeamPatternTokenKey, expectedTokens.getValue(team).second))
                    .assert(SemanticsMatcher.expectValue(TeamNumberKey, team))
            }
        }
    }
}
