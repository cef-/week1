package com.example.startPlayerTbd

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.example.startPlayerTbd.teams.KotlinTeamRandomSource
import com.example.startPlayerTbd.teams.TeamIndicatorUi
import com.example.startPlayerTbd.teams.TeamRandomizer
import com.example.startPlayerTbd.teams.TeamsScreen
import com.example.startPlayerTbd.teams.TeamsState
import com.example.startPlayerTbd.teams.TeamsTouchAdapter
import com.example.startPlayerTbd.startplayer.KotlinSelectionRandomSource
import com.example.startPlayerTbd.startplayer.StartPlayerIndicatorUi
import com.example.startPlayerTbd.startplayer.StartPlayerScreen
import com.example.startPlayerTbd.startplayer.StartPlayerSelector
import com.example.startPlayerTbd.startplayer.StartPlayerState
import com.example.startPlayerTbd.startplayer.StartPlayerTouchAdapter
import com.example.startPlayerTbd.startplayer.TouchPosition
import com.example.startPlayerTbd.ui.theme.StartPlayerTbdTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StartPlayerTbdTheme {
                AppContent(
                    modifier = Modifier.fillMaxSize().safeDrawingPadding(),
                )
            }
        }
    }
}

@Composable
private fun AppContent(modifier: Modifier = Modifier) {
    var mode by remember { mutableStateOf(AppMode.START_PLAYER) }
    Column(modifier) {
        Row {
            Button(onClick = { mode = AppMode.START_PLAYER }, enabled = mode != AppMode.START_PLAYER) {
                Text("Start player")
            }
            Button(onClick = { mode = AppMode.TEAMS }, enabled = mode != AppMode.TEAMS) {
                Text("Teams")
            }
        }
        when (mode) {
            AppMode.START_PLAYER -> StartPlayerRoute(Modifier.weight(1f))
            AppMode.TEAMS -> TeamsRoute(Modifier.weight(1f))
        }
    }
}

@Composable
private fun StartPlayerRoute(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(StartPlayerState.initial()) }
    val selector = remember { StartPlayerSelector(KotlinSelectionRandomSource()) }
    val touchAdapter = remember { StartPlayerTouchAdapter() }

    LaunchedEffect(state.countdownRemainingMillis, state.recognizedPointerIds) {
        val remaining = state.countdownRemainingMillis ?: return@LaunchedEffect
        delay(remaining)
        if (state.countdownRemainingMillis == remaining) {
            val selectedIds = selector.select(
                pointerIds = state.recognizedPointerIds,
                count = state.selectedStartingPlayerCount,
            )
            state = state.copy(
                selectedPointerIds = selectedIds,
                resultPositions = state.pointerPositions.filterKeys(selectedIds::contains),
                countdownRemainingMillis = null,
            )
        }
    }

    LaunchedEffect(state.retentionRemainingMillis) {
        val remaining = state.retentionRemainingMillis ?: return@LaunchedEffect
        delay(remaining)
        if (state.retentionRemainingMillis == remaining) {
            state = state.advanceTime(remaining)
        }
    }

    val indicatorPositions =
        if (state.selectedPointerIds.isEmpty()) state.pointerPositions else state.resultPositions
    StartPlayerScreen(
        state = state,
        indicators = indicatorPositions.map { (pointerId, position) ->
            StartPlayerIndicatorUi(
                pointerId = pointerId,
                center = position,
                selected = pointerId in state.selectedPointerIds,
            )
        },
        onDecreaseCount = {
            state = state.setSelectionCount(state.selectedStartingPlayerCount - 1)
        },
        onIncreaseCount = {
            state = state.setSelectionCount(state.selectedStartingPlayerCount + 1)
        },
        modifier = modifier.pointerInteropFilter { event ->
            if (event.pointerCount == 1 && event.getY(0) < CONTROL_AREA_HEIGHT_PIXELS) {
                return@pointerInteropFilter false
            }
            state = handleMotionEvent(event, state, touchAdapter)
            true
        },
    )
}

@Composable
private fun TeamsRoute(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(TeamsState.initial()) }
    val randomizer = remember { TeamRandomizer(KotlinTeamRandomSource()) }
    val touchAdapter = remember { TeamsTouchAdapter() }

    LaunchedEffect(state.countdownRemainingMillis, state.recognizedPointerIds) {
        val remaining = state.countdownRemainingMillis ?: return@LaunchedEffect
        delay(remaining)
        if (state.countdownRemainingMillis == remaining) {
            state = state.withAssignments(
                randomizer.assign(state.recognizedPointerIds, state.selectedTeamCount),
            )
        }
    }

    LaunchedEffect(state.retentionRemainingMillis) {
        val remaining = state.retentionRemainingMillis ?: return@LaunchedEffect
        delay(remaining)
        if (state.retentionRemainingMillis == remaining) state = state.advanceTime(remaining)
    }

    val positions = if (state.assignments.isEmpty()) state.pointerPositions else state.resultPositions
    val teamByPointer = state.assignments.flatMap { (team, ids) -> ids.map { it to team } }.toMap()
    TeamsScreen(
        state = state,
        indicators = positions.map { (pointerId, position) ->
            TeamIndicatorUi(pointerId, position, teamByPointer[pointerId])
        },
        onDecreaseCount = { state = state.setTeamCount(state.selectedTeamCount - 1) },
        onIncreaseCount = { state = state.setTeamCount(state.selectedTeamCount + 1) },
        modifier = modifier.pointerInteropFilter { event ->
            if (event.pointerCount == 1 && event.getY(0) < CONTROL_AREA_HEIGHT_PIXELS) {
                return@pointerInteropFilter false
            }
            state = handleTeamsMotionEvent(event, state, touchAdapter)
            true
        },
    )
}

private fun handleTeamsMotionEvent(
    event: MotionEvent,
    currentState: TeamsState,
    touchAdapter: TeamsTouchAdapter,
): TeamsState {
    var state = currentState
    when (event.actionMasked) {
        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
            state = state.onPointerAdded(event.getPointerId(event.actionIndex))
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
            state = state.onPointerRemoved(event.getPointerId(event.actionIndex))
        }
        MotionEvent.ACTION_CANCEL -> return touchAdapter.onCancel(state)
    }
    for (index in 0 until event.pointerCount) {
        val pointerId = event.getPointerId(index)
        if (pointerId in state.recognizedPointerIds) {
            state = state.onPointerMoved(pointerId, TouchPosition(event.getX(index), event.getY(index)))
        }
    }
    return state
}

private fun handleMotionEvent(
    event: MotionEvent,
    currentState: StartPlayerState,
    touchAdapter: StartPlayerTouchAdapter,
): StartPlayerState {
    var state = currentState
    when (event.actionMasked) {
        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
            val pointerId = event.getPointerId(event.actionIndex)
            if (pointerId !in state.recognizedPointerIds && state.recognizedPointerIds.size < MAXIMUM_PLAYERS) {
                state = state.onPointerAdded(pointerId)
            }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
            val pointerId = event.getPointerId(event.actionIndex)
            if (pointerId in state.recognizedPointerIds) {
                state = state.onPointerRemoved(pointerId)
            }
        }
        MotionEvent.ACTION_CANCEL -> return touchAdapter.onCancel(state)
    }

    for (index in 0 until event.pointerCount) {
        val pointerId = event.getPointerId(index)
        if (pointerId in state.recognizedPointerIds) {
            state = state.onPointerMoved(
                pointerId,
                TouchPosition(event.getX(index), event.getY(index)),
            )
        }
    }
    return state
}

private const val MAXIMUM_PLAYERS = 9
private const val CONTROL_AREA_HEIGHT_PIXELS = 180f

private enum class AppMode { START_PLAYER, TEAMS }
