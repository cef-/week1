# Feature: Start Player Selection

## Status

Done

## Problem Statement

Board-game players need a quick and impartial way to choose one or more starting players without entering names or using additional physical components. The app must recognize players through simultaneous finger touches, wait until the group is stable, select the configured number of starting players uniformly at random, and make the result unambiguous on a shared phone or tablet screen.

## Proposed Change

Add a dedicated start-player mode with these behaviors:

- Represent players only as active, session-local touch-pointer IDs; do not request or store names.
- Default to selecting `1` starting player and allow values from `1` through `8`.
- Require at least `k + 1` recognized fingers to select `k` starting players, ensuring at least one player remains unselected.
- Recognize at most `9` players and visibly report every recognized touch.
- Start a `2,000` millisecond settling countdown once enough fingers are present.
- Restart the countdown when a finger is added or removed, cancel it when too few fingers remain, and leave it unchanged by finger movement.
- Select exactly `k` distinct touches, with every valid `k`-touch subset having equal probability.
- Identify selected players using both a color token and a pattern token at their positions at selection time.
- Freeze the result until all fingers are removed, retain it for another `3,000` milliseconds, and then reset.
- Reset immediately if a new finger appears during result retention.
- Keep the configured starting-player count during in-process resets, but restore the default after the app process closes.
- Keep random-selection logic independent of Android APIs so it can later move to Kotlin Multiplatform common code.
- Target Android phones and tablets, optimize the initial layout for phones, and keep interactive controls outside system-gesture insets.

## Acceptance Criteria

### AC99: Select one starting player from two players
**Given** the selected starting-player count is `1`
**And** two players each have exactly one finger recognized on the physical device screen
**When** both fingers remain on the screen and the recognized finger count remains `2` for `2,000` milliseconds
**Then** exactly `1` of the `2` visible touch indicators uses the start-player color token and start-player pattern token
**And** exactly `1` of the `2` visible touch indicators remains unselected
**And** the screen displays `1 starting player selected`

### AC1: Default number of starting players
**Given** start-player mode has just been opened
**When** the start-player screen is displayed
**Then** the selected starting-player count is `1`
**And** the screen displays `Starting players: 1`

### AC2: Configure multiple starting players
**Given** the selected starting-player count is `1`
**When** the user increases the count to `3`
**Then** the selected starting-player count is `3`
**And** the screen displays `Starting players: 3`
**And** the screen displays `Place at least 4 fingers`

### AC3: Enforce the starting-player count range
**Given** the app supports at most `9` recognized players and at least one player must remain unselected
**When** the user changes the selected starting-player count
**Then** the selectable values are the integers from `1` through `8` inclusive
**And** the decrease control is disabled when the value is `1`
**And** the increase control is disabled when the value is `8`

### AC4: Recognize and display active fingers
**Given** start-player mode is collecting players
**When** `4` distinct active pointers are reported by the device
**Then** the screen displays `Players detected: 4`
**And** exactly `4` touch indicators are visible
**And** each indicator is centered on the current position of its corresponding active pointer

### AC5: Do not randomize below the required player count
**Given** the selected starting-player count is `3`
**And** exactly `3` fingers are recognized
**When** the touch set remains unchanged for `2,000` milliseconds
**Then** no starting player is selected
**And** the screen displays `Place at least 4 fingers`
**And** no countdown is active

### AC6: Start the settling countdown at the required player count
**Given** the selected starting-player count is `3`
**And** exactly `3` fingers are recognized
**When** a fourth finger is recognized
**Then** a `2,000` millisecond settling countdown starts
**And** the screen displays `Hold fingers in place: 2`

### AC7: Restart the countdown when a finger is added
**Given** the settling countdown has `1,200` milliseconds remaining
**When** one additional finger is recognized
**Then** the countdown restarts with `2,000` milliseconds remaining
**And** the screen displays `Hold fingers in place: 2`

### AC8: Restart the countdown when a finger is removed
**Given** the settling countdown has `1,200` milliseconds remaining
**And** removing one finger still leaves at least the required number of recognized fingers
**When** that finger is removed
**Then** the countdown restarts with `2,000` milliseconds remaining
**And** the screen displays `Hold fingers in place: 2`

### AC9: Cancel the countdown when too few fingers remain
**Given** the selected starting-player count is `2`
**And** exactly `3` fingers are recognized
**And** the settling countdown is active
**When** one finger is removed
**Then** the countdown is canceled
**And** no starting player is selected
**And** the screen displays `Place at least 3 fingers`

### AC10: Finger movement does not restart the countdown
**Given** the settling countdown has `1,200` milliseconds remaining
**When** every recognized pointer retains its pointer ID but one pointer changes position
**Then** the countdown still has `1,200` milliseconds remaining at that event time
**And** the moved pointer's touch indicator is centered on its new position

### AC11: Select the configured number of distinct starting players
**Given** the selected starting-player count is `2`
**And** the recognized pointer IDs are `[11, 22, 33, 44]`
**And** the random source is configured to select pointer IDs `[33, 11]`
**When** the touch set remains unchanged for `2,000` milliseconds
**Then** the selected pointer IDs are exactly `[33, 11]`
**And** exactly `2` selected-player effects are visible
**And** pointer IDs `22` and `44` remain unselected
**And** the screen displays `2 starting players selected`

### AC12: Select each valid group with equal probability
**Given** `p` distinct fingers are recognized
**And** the selected starting-player count is `k`
**And** `1 <= k < p <= 9`
**When** a start-player selection is generated
**Then** every distinct subset of `k` recognized pointer IDs has probability `1 / C(p, k)` of being selected
**And** no pointer ID appears more than once in the selected subset

### AC13: Show selected players using color and pattern
**Given** pointer IDs `33` and `11` were selected
**When** the result is displayed
**Then** exactly one result effect is displayed at pointer `33`'s position at selection time
**And** exactly one result effect is displayed at pointer `11`'s position at selection time
**And** both selected effects use the start-player color token
**And** both selected effects use the start-player pattern token
**And** unselected pointers use neither the start-player color token nor the start-player pattern token

### AC14: Freeze the result after selection
**Given** pointer ID `33` is selected and its result effect is centered at `(120, 400)`
**When** pointer `33` moves to `(220, 500)`
**Then** the selected pointer IDs remain unchanged
**And** the result effect remains centered at `(120, 400)`
**And** no new settling countdown starts

### AC15: Keep the result while at least one finger remains
**Given** a result selected pointer IDs `[33, 11]`
**And** at least one finger remains on the screen
**When** another recognized finger is removed
**Then** the selected pointer IDs remain exactly `[33, 11]`
**And** the screen continues to display `2 starting players selected`

### AC16: Retain the result for five seconds after all fingers are removed
**Given** a result selected pointer IDs `[33, 11]`
**When** the final active finger is removed
**Then** a result-retention timer starts at `3,000` milliseconds
**And** the selected pointer IDs remain exactly `[33, 11]` through `2,999` elapsed milliseconds
**And** the screen continues to display `2 starting players selected` through `2,999` elapsed milliseconds

### AC17: Reset after the result-retention period
**Given** no fingers are active
**And** the result-retention timer has been active for `2,999` milliseconds
**When** one additional millisecond elapses
**Then** the selected pointer ID collection is empty
**And** no selected-player effect is visible
**And** the screen displays `Players detected: 0`
**And** the selected starting-player count remains unchanged

### AC18: Reset immediately when a new finger arrives during retention
**Given** no fingers are active
**And** a previous result is `2 starting players selected`
**And** the result-retention timer has `3,000` milliseconds remaining
**When** a new finger with pointer ID `55` is recognized
**Then** the previous selected pointer ID collection is empty
**And** the result-retention timer is canceled
**And** exactly `1` ordinary touch indicator is visible for pointer ID `55`
**And** the screen displays `Players detected: 1`
**And** the screen displays `Place at least 3 fingers`

### AC19: Handle gesture cancellation
**Given** player collection or its settling countdown is active
**When** Android reports cancellation of the active gesture
**Then** the recognized pointer ID collection is empty
**And** the settling countdown is canceled
**And** no starting player is selected
**And** the screen displays `Players detected: 0`

### AC20: Limit recognized players to nine
**Given** `9` fingers are already recognized
**When** the device reports a tenth active pointer
**Then** the recognized player count remains `9`
**And** exactly `9` touch indicators are visible
**And** the screen displays `Maximum 9 players supported`
**And** the tenth pointer is excluded from random selection

### AC21: Preserve configuration across a session reset
**Given** the selected starting-player count is `3`
**And** a completed result has reset after its `3,000` millisecond retention period
**When** the start-player screen returns to player collection
**Then** the selected starting-player count remains `3`
**And** the screen displays `Starting players: 3`
**And** the screen displays `Place at least 4 fingers`

### AC22: Do not persist feature state after the app closes
**Given** the selected starting-player count was changed to `3`
**And** a start-player result was displayed
**When** the app process is closed and the app is launched again
**Then** the selected starting-player count is `1`
**And** the selected pointer ID collection is empty
**And** the screen displays `Players detected: 0`
**And** no selected-player effect is visible

## Files to Modify

The application has not been scaffolded yet. The package prefix below is resolved when the Android project is created; the feature-local filenames and responsibilities are fixed by this spec.

| File | Change |
|---|---|
| `app/src/main/java/com/example/startPlayerTbd/MainActivity.kt` | Host the Start Player screen and connect Android touch events and timers to the feature state. |
| `app/src/main/kotlin/com/example/startPlayerTbd/startplayer/StartPlayerScreen.kt` | Render count controls, status text, touch indicators, countdown, and frozen result effects. |
| `app/src/main/kotlin/com/example/startPlayerTbd/startplayer/StartPlayerState.kt` | Define collection, countdown, result, retention, reset, and cancellation state transitions. |
| `app/src/main/kotlin/com/example/startPlayerTbd/startplayer/StartPlayerSelector.kt` | Select exactly `k` unique opaque participant IDs using an injectable random source. |
| `app/src/main/kotlin/com/example/startPlayerTbd/startplayer/StartPlayerTouchAdapter.kt` | Convert Android pointer events into stable pointer-ID membership and position updates, capped at `9`. |
| `app/src/test/kotlin/com/example/startPlayerTbd/startplayer/StartPlayerSelectorTest.kt` | Prove count, uniqueness, deterministic selection, and subset-uniformity properties. |
| `app/src/test/kotlin/com/example/startPlayerTbd/startplayer/StartPlayerStateTest.kt` | Prove countdown, cancellation, result retention, and reset behavior with a fake clock. |
| `app/src/test/kotlin/com/example/startPlayerTbd/startplayer/StartPlayerTouchAdapterTest.kt` | Prove stable pointer recognition and exact indicator positions for AC4. |
| `app/src/androidTest/kotlin/com/example/startPlayerTbd/startplayer/StartPlayerScreenTest.kt` | Prove exact visible text, control bounds, indicator counts, and result semantics. |

## Risk

- What could break: Device touch hardware may report fewer than `9` simultaneous pointers; Android may cancel gestures near system-navigation areas; pointer indexes may be reordered between events; timers may race with pointer changes; color-only rendering would not distinguish results accessibly; a biased selection algorithm could favor some pointer subsets.
- Rollback: Remove the start-player navigation entry and feature-local files listed above. Because the feature stores no persistent data and has no network or schema dependencies, rollback requires no data migration.

## Testing Strategy (MANDATORY)

| Function | Case | Given | When | Then |
|---|---|---|---|---|
| Physical-device QA | AC99 one of two players | Starting-player count is `1`; two players each place one finger and the screen displays `Players detected: 2` | Keep both fingers on the screen with the detected count unchanged for `2,000 ms` | Exactly one of two indicators uses the start-player color and pattern tokens; exactly one remains unselected; text is `1 starting player selected` |
| `StartPlayerState.initial` | AC1 default | Start-player mode has just opened | Screen state is created | Count is `1`; text is `Starting players: 1` |
| `StartPlayerState.setSelectionCount` | AC2 multiple selection | Count is `1` | Set count to `3` | Count is `3`; texts are `Starting players: 3` and `Place at least 4 fingers` |
| `StartPlayerState.setSelectionCount` | AC3 bounds | Maximum players is `9` | Attempt decrement at `1` and increment at `8` | Values remain within `1..8`; decrement is disabled at `1`; increment is disabled at `8` |
| `StartPlayerTouchAdapter.onPointers` | AC4 recognition | Collection is active | Report IDs `[11,22,33,44]` and four positions | Text is `Players detected: 4`; exactly four indicators are centered on corresponding positions |
| `StartPlayerState.advanceTime` | AC5 insufficient players | `k=3`; three fingers active | Advance `2,000 ms` without membership change | Selection is empty; text is `Place at least 4 fingers`; countdown is absent |
| `StartPlayerState.onPointerAdded` | AC6 threshold reached | `k=3`; three fingers active | Add fourth finger | Countdown remaining is `2,000 ms`; text is `Hold fingers in place: 2` |
| `StartPlayerState.onPointerAdded` | AC7 addition restart | Countdown remaining is `1,200 ms` | Add one finger | Countdown remaining is `2,000 ms`; text is `Hold fingers in place: 2` |
| `StartPlayerState.onPointerRemoved` | AC8 removal restart | Countdown remaining is `1,200 ms`; enough fingers remain | Remove one finger | Countdown remaining is `2,000 ms`; text is `Hold fingers in place: 2` |
| `StartPlayerState.onPointerRemoved` | AC9 removal cancellation | `k=2`; three fingers active; countdown active | Remove one finger | Countdown is absent; selection is empty; text is `Place at least 3 fingers` |
| `StartPlayerState.onPointerMoved` | AC10 movement | Countdown remaining is `1,200 ms` | Move one pointer without changing its ID | Countdown remains `1,200 ms` at event time; indicator uses the new position |
| `StartPlayerSelector.select` | AC11 deterministic selection | IDs `[11,22,33,44]`; `k=2`; fake random selects `[33,11]` | Settle for `2,000 ms` | Selection is exactly `[33,11]`; two effects exist; `22` and `44` are unselected; text is `2 starting players selected` |
| `StartPlayerSelector.select` | AC12 subset uniformity | Every valid `1 <= k < p <= 9`; exhaustive deterministic random-decision streams | Enumerate all selector outcomes | Every `k`-ID subset occurs equally often; each has probability `1/C(p,k)`; every result contains unique IDs |
| `StartPlayerScreen` | AC13 color and pattern | IDs `33` and `11` selected | Render result | One effect per selected position uses both start-player tokens; unselected pointers use neither token |
| `StartPlayerState.onPointerMoved` | AC14 frozen result | ID `33` selected at `(120,400)` | Move ID `33` to `(220,500)` | Selection is unchanged; effect remains at `(120,400)`; countdown is absent |
| `StartPlayerState.onPointerRemoved` | AC15 partial lift | Selection is `[33,11]`; at least one finger remains | Remove another recognized finger | Selection remains `[33,11]`; text remains `2 starting players selected` |
| `StartPlayerState.onPointerRemoved` | AC16 retention starts | Selection is `[33,11]` | Remove final active finger | Retention starts at `3,000 ms`; selection and result text remain through elapsed `2,999 ms` |
| `StartPlayerState.advanceTime` | AC17 retention expires | No fingers active; retention elapsed is `2,999 ms` | Advance `1 ms` | Selection is empty; no result effect; text is `Players detected: 0`; configured count is unchanged |
| `StartPlayerState.onPointerAdded` | AC18 new session during retention | Previous two-player result; retention remaining is `3,000 ms` | Add ID `55` | Previous selection is empty; retention is canceled; one ordinary indicator; texts are `Players detected: 1` and `Place at least 3 fingers` |
| `StartPlayerTouchAdapter.onCancel` | AC19 gesture canceled | Collection or countdown active | Receive Android gesture cancellation | Pointer collection and selection are empty; countdown absent; text is `Players detected: 0` |
| `StartPlayerTouchAdapter.onPointerAdded` | AC20 tenth pointer | Nine IDs already recognized | Add a tenth pointer | Count and indicators remain `9`; text is `Maximum 9 players supported`; tenth ID is not selectable |
| `StartPlayerState.resetSession` | AC21 in-process reset | Configured count is `3`; retention expires | Return to collection | Count remains `3`; texts are `Starting players: 3` and `Place at least 4 fingers` |
| `StartPlayerState.initial` | AC22 process restart | Previous count was `3` and a result existed | Create state in a new app process | Count is `1`; selection is empty; text is `Players detected: 0`; no selected effect exists |

A physical-device QA pass must additionally confirm simultaneous multi-touch reporting, system-gesture cancellation, phone usability, and tablet smoke behavior because those properties cannot be fully established by JVM or emulator tests.

## Verification Evidence

- AC99 physical-device QA: PASS on Pixel 9a, Android 17, debug build, 2026-08-17. The tester confirmed that holding two fingers for `2,000 ms` selected exactly one indicator with the start-player color and diagonal pattern and displayed `1 starting player selected`.
- Automated verification: `testDebugUnitTest`, `lintDebug`, and `connectedDebugAndroidTest` passed on 2026-08-17; the connected suite ran `2` tests on Pixel 9a, Android 17.

## Spec Readiness checklist (run before calling the spec done)

- [x] Every AC has a precise expected value — no "works correctly"
- [x] Another person could write a test from each AC without asking
- [x] Every AC can fail — one that cannot fail proves nothing
- [x] Error and edge cases have ACs of their own
- [x] Every AC appears in the testing strategy table
