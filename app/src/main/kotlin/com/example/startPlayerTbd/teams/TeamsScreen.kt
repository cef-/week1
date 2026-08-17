package com.example.startPlayerTbd.teams

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.example.startPlayerTbd.startplayer.TouchPosition

val TeamColorTokenKey = SemanticsPropertyKey<String>("TeamColorToken")
val TeamPatternTokenKey = SemanticsPropertyKey<String>("TeamPatternToken")
val TeamNumberKey = SemanticsPropertyKey<Int>("TeamNumber")

data class TeamIndicatorUi(
    val pointerId: Int,
    val center: TouchPosition,
    val teamNumber: Int?,
)

@Composable
fun TeamsScreen(
    state: TeamsState,
    indicators: List<TeamIndicatorUi>,
    onDecreaseCount: () -> Unit = {},
    onIncreaseCount: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Teams")
            Row {
                Button(onClick = onDecreaseCount, enabled = state.canDecreaseTeamCount) { Text("−") }
                Spacer(Modifier.width(12.dp))
                Text(state.teamCountText, Modifier.padding(top = 12.dp))
                Spacer(Modifier.width(12.dp))
                Button(onClick = onIncreaseCount, enabled = state.canIncreaseTeamCount) { Text("+") }
            }
            Text(state.maximumPlayersText ?: state.detectedPlayersText)
            Text(state.resultText ?: state.countdownText ?: state.minimumPlayersText)
        }

        indicators.forEach { indicator ->
            val team = indicator.teamNumber
            val colorToken = team?.let { TEAM_TOKENS[it - 1].first } ?: "none"
            val patternToken = team?.let { TEAM_TOKENS[it - 1].second } ?: "none"
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            indicator.center.x.toInt() - INDICATOR_RADIUS_PIXELS,
                            indicator.center.y.toInt() - INDICATOR_RADIUS_PIXELS,
                        )
                    }
                    .size(INDICATOR_SIZE)
                    .clip(CircleShape)
                    .background(team?.let { TEAM_COLORS[it - 1] } ?: Color.Gray)
                    .testTag("team-pointer-${indicator.pointerId}")
                    .semantics {
                        this[TeamColorTokenKey] = colorToken
                        this[TeamPatternTokenKey] = patternToken
                        if (team != null) this[TeamNumberKey] = team
                    },
            ) {
                if (team != null) {
                    Canvas(Modifier.fillMaxSize()) {
                        val offset = (team % 3) * 8f
                        drawLine(
                            color = Color.White,
                            start = Offset(0f, size.height - offset),
                            end = Offset(size.width - offset, 0f),
                            strokeWidth = 5f,
                        )
                    }
                    Text(
                        text = team.toString(),
                        color = Color.White,
                        modifier = Modifier.padding(start = 19.dp, top = 12.dp),
                    )
                }
            }
        }
    }
}

private val TEAM_TOKENS = listOf(
    "blue" to "diagonal",
    "red" to "reverse-diagonal",
    "green" to "horizontal",
    "purple" to "vertical",
    "orange" to "cross",
    "teal" to "dots",
    "pink" to "rings",
    "brown" to "grid",
    "indigo" to "chevron",
)
private val TEAM_COLORS = listOf(
    Color(0xFF1565C0), Color(0xFFC62828), Color(0xFF2E7D32),
    Color(0xFF6A1B9A), Color(0xFFEF6C00), Color(0xFF00796B),
    Color(0xFFAD1457), Color(0xFF5D4037), Color(0xFF283593),
)
private val INDICATOR_SIZE = 48.dp
private const val INDICATOR_RADIUS_PIXELS = 24
