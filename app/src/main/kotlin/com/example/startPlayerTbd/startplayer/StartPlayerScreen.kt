package com.example.startPlayerTbd.startplayer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

val StartPlayerColorTokenKey = SemanticsPropertyKey<String>("StartPlayerColorToken")
val StartPlayerPatternTokenKey = SemanticsPropertyKey<String>("StartPlayerPatternToken")

data class StartPlayerIndicatorUi(
    val pointerId: Int,
    val center: TouchPosition,
    val selected: Boolean,
)

@Composable
fun StartPlayerScreen(
    indicators: List<StartPlayerIndicatorUi>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        indicators.forEach { indicator ->
            val colorToken = if (indicator.selected) "start-player" else "none"
            val patternToken = if (indicator.selected) "diagonal" else "none"
            Box(
                modifier =
                    Modifier
                        .offset {
                            IntOffset(
                                indicator.center.x.toInt() - INDICATOR_RADIUS_PIXELS,
                                indicator.center.y.toInt() - INDICATOR_RADIUS_PIXELS,
                            )
                        }
                        .size(INDICATOR_SIZE)
                        .clip(CircleShape)
                        .background(if (indicator.selected) START_PLAYER_COLOR else Color.Gray)
                        .testTag("pointer-${indicator.pointerId}")
                        .semantics {
                            this[StartPlayerColorTokenKey] = colorToken
                            this[StartPlayerPatternTokenKey] = patternToken
                        },
            ) {
                if (indicator.selected) {
                    Canvas(Modifier.fillMaxSize()) {
                        drawLine(
                            color = Color.White,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, 0f),
                            strokeWidth = PATTERN_STROKE_WIDTH,
                        )
                    }
                }
            }
        }
    }
}

private val INDICATOR_SIZE = 48.dp
private const val INDICATOR_RADIUS_PIXELS = 24
private const val PATTERN_STROKE_WIDTH = 6f
private val START_PLAYER_COLOR = Color(0xFF1565C0)
