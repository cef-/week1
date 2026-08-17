# Feature: Start Player Selection

## Overview

Select one or more starting players from people simultaneously touching the device screen. Players are represented only by active finger touches; the feature does not request or store player names. The result is shown using color-and-pattern effects at the selected players' touch positions.

## User Story

As a group starting a board game, we want the app to randomly select one or more of us from our fingers on the screen so that we can choose starting players without entering names or using physical components.

## Acceptance Criteria

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
**When** the touch set remains unchanged for `3,000` milliseconds
**Then** no starting player is selected
**And** the screen displays `Place at least 4 fingers`
**And** no countdown is active

### AC6: Start the settling countdown at the required player count

**Given** the selected starting-player count is `3`
**And** exactly `3` fingers are recognized
**When** a fourth finger is recognized
**Then** a `3,000` millisecond settling countdown starts
**And** the screen displays `Hold fingers in place: 3`

### AC7: Restart the countdown when a finger is added

**Given** the settling countdown has `1,200` milliseconds remaining
**When** one additional finger is recognized
**Then** the countdown restarts with `3,000` milliseconds remaining
**And** the screen displays `Hold fingers in place: 3`

### AC8: Restart the countdown when a finger is removed

**Given** the settling countdown has `1,200` milliseconds remaining
**And** removing one finger still leaves at least the required number of recognized fingers
**When** that finger is removed
**Then** the countdown restarts with `3,000` milliseconds remaining
**And** the screen displays `Hold fingers in place: 3`

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
**When** the touch set remains unchanged for `3,000` milliseconds
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
**Then** a result-retention timer starts at `5,000` milliseconds
**And** the selected pointer IDs remain exactly `[33, 11]` through `4,999` elapsed milliseconds
**And** the screen continues to display `2 starting players selected` through `4,999` elapsed milliseconds

### AC17: Reset after the result-retention period

**Given** no fingers are active
**And** the result-retention timer has been active for `4,999` milliseconds
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
**And** a completed result has reset after its `5,000` millisecond retention period
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

## Technical Constraints

- Target Android phones and tablets; optimize the initial layout for phones.
- Use stable pointer IDs, not pointer indexes, to track fingers during an active gesture.
- Treat only pointer addition, pointer removal, and gesture cancellation as touch-set changes; position changes do not change membership.
- Keep Android touch-event handling and drawing concerns outside the random-selection logic.
- Represent participants as opaque, session-local identifiers in the random-selection logic.
- Keep selection logic free of Android APIs so it can later move to Kotlin Multiplatform common code.
- Use an injectable or controllable random source for deterministic tests.
- Do not request player names, network access, accounts, or storage permissions.
- Do not persist player, result, or configuration state between app processes.
- Avoid interactive touch targets inside system gesture insets.

## Test Strategy

- Unit-test the pure selection logic, including uniqueness, configured selection count, and deterministic selections with a controlled random source.
- Property-test or exhaustively test small inputs to verify that exactly `k` of `p` distinct participants are selected for every valid `1 <= k < p <= 9` input.
- Unit-test the state machine with a fake clock for the `3,000` millisecond settling timer and `5,000` millisecond retention timer.
- Test pointer addition, removal, movement, cancellation, and the nine-pointer cap through the Android touch adapter.
- Use Compose UI tests for exact status text, enabled/disabled count controls, indicator counts, and result rendering semantics.
- Validate multi-touch behavior on physical Android devices because emulator input and device hardware can differ in simultaneous-pointer support.
- Validate phone layouts first and run a tablet smoke test to confirm the touch surface and controls remain usable.

## Spec Readiness Checklist

- [x] The feature scope is limited to start-player selection; team assignment is excluded.
- [x] Every acceptance criterion is numbered and uses Given/When/Then structure.
- [x] Every Then clause names an exact value, state, collection, count, coordinate, duration, token, or output string.
- [x] Defaults are explicit: `1` starting player, `3,000` millisecond settling delay, `5,000` millisecond result retention, and no persisted state.
- [x] Valid bounds are explicit: `1` through `8` starting players and at most `9` recognized players.
- [x] The minimum-player rule is explicit: selecting `k` starting players requires at least `k + 1` recognized fingers.
- [x] Pointer addition, removal, movement, cancellation, and overflow behavior are specified.
- [x] Random-selection uniqueness and fairness are specified in testable mathematical terms.
- [x] Result presentation and result lifetime have precise expected states.
- [x] Error and blocked states use exact user-visible strings.
- [x] Each acceptance criterion can be mapped to a unit, state-machine, adapter, UI, or device test.
- [x] Cross-platform preparation is limited to separating pure selection logic from Android UI code.
