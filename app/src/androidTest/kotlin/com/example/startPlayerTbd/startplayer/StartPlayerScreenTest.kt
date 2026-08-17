package com.example.startPlayerTbd.startplayer

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class StartPlayerScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun AC13_selected_indicators_use_color_and_pattern_tokens() {
        composeRule.setContent {
            StartPlayerScreen(
                indicators =
                    listOf(
                        StartPlayerIndicatorUi(33, TouchPosition(120f, 400f), selected = true),
                        StartPlayerIndicatorUi(11, TouchPosition(220f, 500f), selected = true),
                        StartPlayerIndicatorUi(22, TouchPosition(320f, 600f), selected = false),
                    ),
            )
        }

        listOf(33, 11).forEach { pointerId ->
            composeRule.onNodeWithTag("pointer-$pointerId")
                .assert(SemanticsMatcher.expectValue(StartPlayerColorTokenKey, "start-player"))
                .assert(SemanticsMatcher.expectValue(StartPlayerPatternTokenKey, "diagonal"))
        }
        composeRule.onNodeWithTag("pointer-22")
            .assert(SemanticsMatcher.expectValue(StartPlayerColorTokenKey, "none"))
            .assert(SemanticsMatcher.expectValue(StartPlayerPatternTokenKey, "none"))
    }
}
