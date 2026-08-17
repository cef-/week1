# Feature: Teams

## Status

In Progress

## Problem Statement

Board-game players need a quick and impartial way to divide themselves into balanced teams without entering names or using additional physical components. The app must recognize players through simultaneous finger touches, wait until the group is stable, assign every recognized player to exactly one team, and make each assignment unambiguous on a shared phone or tablet screen.

## Proposed Change

Add a dedicated Teams mode with these behaviors:

- Represent players only as active, session-local touch-pointer IDs; do not request or store names.
- Default to `2` teams and allow values from `2` through `9`.
- Require at least `n` recognized fingers to create `n` teams, ensuring every team has at least one player.
- Recognize at most `9` players and visibly report every recognized touch.
- Start a `2,000` millisecond settling countdown once enough fingers are present.
- Restart the countdown when a finger is added or removed, cancel it when too few fingers remain, and leave it unchanged by finger movement.
- Randomly shuffle every recognized player and assign the shuffled players to numbered teams in round-robin order.
- Give every player exactly one team and keep team sizes within one player of each other.
- Identify each team using a unique combination of color token and pattern token at its members' positions at assignment time.
- Freeze the result until all fingers are removed, retain it for another `3,000` milliseconds, and then reset.
- Reset immediately if a new finger appears during result retention.
- Keep the configured team count during in-process resets, but restore the default after the app process closes.
- Keep team-assignment logic independent of Android APIs so it can later move to Kotlin Multiplatform common code.
- Target Android phones and tablets, optimize the initial layout for phones, and keep interactive controls outside system-gesture insets.

## Acceptance Criteria

### AC1: Default number of teams
**Given** Teams mode has just been opened
**When** the Teams screen is displayed
**Then** the selected team count is `2`
**And** the screen displays `Teams: 2`

### AC2: Configure the number of teams
**Given** the selected team count is `2`
**When** the user increases the count to `4`
**Then** the selected team count is `4`
**And** the screen displays `Teams: 4`
**And** the screen displays `Place at least 4 fingers`

### AC3: Enforce the team-count range
**Given** the app supports at most `9` recognized players and every team must contain at least one player
**When** the user changes the selected team count
**Then** the selectable values are the integers from `2` through `9` inclusive
**And** the decrease control is disabled when the value is `2`
**And** the increase control is disabled when the value is `9`

### AC4: Recognize and display active fingers
**Given** Teams mode is collecting players
**When** `4` distinct active pointers are reported by the device
**Then** the screen displays `Players detected: 4`
**And** exactly `4` touch indicators are visible
**And** each indicator is centered on the current position of its corresponding active pointer

### AC5: Do not randomize below the required player count
**Given** the selected team count is `4`
**And** exactly `3` fingers are recognized
**When** the touch set remains unchanged for `2,000` milliseconds
**Then** no team assignment exists
**And** the screen displays `Place at least 4 fingers`
**And** no countdown is active

### AC6: Start the settling countdown at the required player count
**Given** the selected team count is `4`
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
**And** removing one finger still leaves at least the selected number of teams
**When** that finger is removed
**Then** the countdown restarts with `2,000` milliseconds remaining
**And** the screen displays `Hold fingers in place: 2`

### AC9: Cancel the countdown when too few fingers remain
**Given** the selected team count is `3`
**And** exactly `3` fingers are recognized
**And** the settling countdown is active
**When** one finger is removed
**Then** the countdown is canceled
**And** no team assignment exists
**And** the screen displays `Place at least 3 fingers`

### AC10: Finger movement does not restart the countdown
**Given** the settling countdown has `1,200` milliseconds remaining
**When** every recognized pointer retains its pointer ID but one pointer changes position
**Then** the countdown still has `1,200` milliseconds remaining at that event time
**And** the moved pointer's touch indicator is centered on its new position

### AC11: Assign every player exactly once
**Given** the selected team count is `3`
**And** the recognized pointer IDs are `[11, 22, 33, 44, 55, 66, 77]`
**And** the controlled shuffle result is `[44, 11, 77, 22, 66, 33, 55]`
**When** the touch set remains unchanged for `2,000` milliseconds
**Then** Team `1` contains exactly `[44, 22, 55]`
**And** Team `2` contains exactly `[11, 66]`
**And** Team `3` contains exactly `[77, 33]`
**And** every recognized pointer ID appears in exactly one team
**And** the screen displays `3 teams created`

### AC12: Keep teams balanced and nonempty
**Given** `p` distinct fingers are recognized
**And** the selected team count is `n`
**And** `2 <= n <= p <= 9`
**When** a team assignment is generated
**Then** exactly `n` teams are created
**And** every team contains at least `1` pointer ID
**And** every team size is either `floor(p / n)` or `ceil(p / n)`
**And** the largest team size minus the smallest team size is at most `1`

### AC13: Randomize assignment without duplicate or missing players
**Given** `p` distinct fingers are recognized
**And** `2 <= p <= 9`
**When** the player list is shuffled for team assignment
**Then** every permutation of the `p` pointer IDs has probability `1 / p!`
**And** the shuffled list contains exactly the original `p` pointer IDs
**And** no pointer ID occurs more than once in the shuffled list

### AC14: Show every team using a distinct color and pattern
**Given** Team `1` contains `[44, 22, 55]`, Team `2` contains `[11, 66]`, and Team `3` contains `[77, 33]`
**When** the result is displayed
**Then** exactly `7` result effects are visible
**And** each effect is centered on its pointer's position at assignment time
**And** members of the same team use the same color token and the same pattern token
**And** Teams `1`, `2`, and `3` use `3` pairwise-distinct color-and-pattern token combinations
**And** each result effect displays its team number as `1`, `2`, or `3`

### AC15: Freeze the result after assignment
**Given** pointer ID `44` is assigned to Team `1` and its result effect is centered at `(120, 400)`
**When** pointer `44` moves to `(220, 500)`
**Then** every team assignment remains unchanged
**And** pointer `44`'s result effect remains centered at `(120, 400)`
**And** no new settling countdown starts

### AC16: Keep the result while at least one finger remains
**Given** a result contains `3` teams
**And** at least one finger remains on the screen
**When** another recognized finger is removed
**Then** every team assignment remains unchanged
**And** the screen continues to display `3 teams created`

### AC17: Retain the result for five seconds after all fingers are removed
**Given** a result contains `3` teams
**When** the final active finger is removed
**Then** a result-retention timer starts at `3,000` milliseconds
**And** every team assignment remains unchanged through `2,999` elapsed milliseconds
**And** the screen continues to display `3 teams created` through `2,999` elapsed milliseconds

### AC18: Reset after the result-retention period
**Given** no fingers are active
**And** the result-retention timer has been active for `2,999` milliseconds
**When** one additional millisecond elapses
**Then** the team-assignment collection is empty
**And** no team result effect is visible
**And** the screen displays `Players detected: 0`
**And** the selected team count remains unchanged

### AC19: Reset immediately when a new finger arrives during retention
**Given** no fingers are active
**And** a previous result is `3 teams created`
**And** the result-retention timer has `3,000` milliseconds remaining
**When** a new finger with pointer ID `88` is recognized
**Then** the previous team-assignment collection is empty
**And** the result-retention timer is canceled
**And** exactly `1` ordinary touch indicator is visible for pointer ID `88`
**And** the screen displays `Players detected: 1`
**And** the screen displays `Place at least 3 fingers`

### AC20: Handle gesture cancellation
**Given** player collection or its settling countdown is active
**When** Android reports cancellation of the active gesture
**Then** the recognized pointer ID collection is empty
**And** the settling countdown is canceled
**And** no team assignment exists
**And** the screen displays `Players detected: 0`

### AC21: Limit recognized players to nine
**Given** `9` fingers are already recognized
**When** the device reports a tenth active pointer
**Then** the recognized player count remains `9`
**And** exactly `9` touch indicators are visible
**And** the screen displays `Maximum 9 players supported`
**And** the tenth pointer is excluded from team assignment

### AC22: Preserve configuration across a session reset
**Given** the selected team count is `4`
**And** a completed result has reset after its `3,000` millisecond retention period
**When** the Teams screen returns to player collection
**Then** the selected team count remains `4`
**And** the screen displays `Teams: 4`
**And** the screen displays `Place at least 4 fingers`

### AC23: Do not persist feature state after the app closes
**Given** the selected team count was changed to `4`
**And** a team result was displayed
**When** the app process is closed and the app is launched again
**Then** the selected team count is `2`
**And** the team-assignment collection is empty
**And** the screen displays `Players detected: 0`
**And** no team result effect is visible

## Files to Modify

The Android application uses package `com.example.startPlayerTbd`.

| File | Change |
|---|---|
| `app/src/main/java/com/example/startPlayerTbd/MainActivity.kt` | Add separate Start Player and Teams mode controls and connect Teams touch events and timers to its state. |
| `app/src/main/kotlin/com/example/startPlayerTbd/teams/TeamsScreen.kt` | Render team-count controls, status text, touch indicators, countdown, and frozen team effects. |
| `app/src/main/kotlin/com/example/startPlayerTbd/teams/TeamsState.kt` | Define collection, countdown, result, retention, reset, and cancellation state transitions. |
| `app/src/main/kotlin/com/example/startPlayerTbd/teams/TeamRandomizer.kt` | Shuffle opaque participant IDs and partition them into balanced, nonempty numbered teams. |
| `app/src/main/kotlin/com/example/startPlayerTbd/teams/TeamsTouchAdapter.kt` | Convert Android pointer events into stable pointer-ID membership and position updates, capped at `9`. |
| `app/src/test/kotlin/com/example/startPlayerTbd/teams/TeamRandomizerTest.kt` | Prove completeness, uniqueness, balance, deterministic assignment, and shuffle-uniformity properties. |
| `app/src/test/kotlin/com/example/startPlayerTbd/teams/TeamsStateTest.kt` | Prove countdown, cancellation, result retention, and reset behavior with a fake clock. |
| `app/src/test/kotlin/com/example/startPlayerTbd/teams/TeamsTouchAdapterTest.kt` | Prove pointer recognition, exact positions, cancellation, and the nine-player cap. |
| `app/src/androidTest/kotlin/com/example/startPlayerTbd/teams/TeamsScreenTest.kt` | Prove exact visible text, control bounds, indicator counts, and team-result semantics. |

## Risk

- What could break: Device touch hardware may report fewer than `9` simultaneous pointers; Android may cancel gestures near system-navigation areas; pointer indexes may be reordered between events; timers may race with pointer changes; insufficiently distinct color-and-pattern combinations could make teams ambiguous; a biased shuffle could favor some assignments.
- Rollback: Remove the Teams navigation entry and feature-local files listed above. Because the feature stores no persistent data and has no network or schema dependencies, rollback requires no data migration.

## Testing Strategy (MANDATORY)

| Function | Case | Given | When | Then |
|---|---|---|---|---|
| `TeamsState.initial` | AC1 default | Teams mode has just opened | Screen state is created | Team count is `2`; text is `Teams: 2` |
| `TeamsState.setTeamCount` | AC2 configuration | Team count is `2` | Set team count to `4` | Count is `4`; texts are `Teams: 4` and `Place at least 4 fingers` |
| `TeamsState.setTeamCount` | AC3 bounds | Maximum players is `9` | Attempt decrement at `2` and increment at `9` | Values remain within `2..9`; decrement is disabled at `2`; increment is disabled at `9` |
| `TeamsTouchAdapter.onPointers` | AC4 recognition | Collection is active | Report IDs `[11,22,33,44]` and four positions | Text is `Players detected: 4`; exactly four indicators are centered on corresponding positions |
| `TeamsState.advanceTime` | AC5 insufficient players | `n=4`; three fingers active | Advance `2,000 ms` without membership change | Assignment is empty; text is `Place at least 4 fingers`; countdown is absent |
| `TeamsState.onPointerAdded` | AC6 threshold reached | `n=4`; three fingers active | Add fourth finger | Countdown remaining is `2,000 ms`; text is `Hold fingers in place: 2` |
| `TeamsState.onPointerAdded` | AC7 addition restart | Countdown remaining is `1,200 ms` | Add one finger | Countdown remaining is `2,000 ms`; text is `Hold fingers in place: 2` |
| `TeamsState.onPointerRemoved` | AC8 removal restart | Countdown remaining is `1,200 ms`; enough fingers remain | Remove one finger | Countdown remaining is `2,000 ms`; text is `Hold fingers in place: 2` |
| `TeamsState.onPointerRemoved` | AC9 removal cancellation | `n=3`; three fingers active; countdown active | Remove one finger | Countdown is absent; assignment is empty; text is `Place at least 3 fingers` |
| `TeamsState.onPointerMoved` | AC10 movement | Countdown remaining is `1,200 ms` | Move one pointer without changing its ID | Countdown remains `1,200 ms` at event time; indicator uses the new position |
| `TeamRandomizer.assign` | AC11 deterministic assignment | IDs `[11,22,33,44,55,66,77]`; `n=3`; controlled shuffle `[44,11,77,22,66,33,55]` | Settle for `2,000 ms` | Teams are exactly `1:[44,22,55]`, `2:[11,66]`, `3:[77,33]`; every ID occurs once; text is `3 teams created` |
| `TeamRandomizer.assign` | AC12 balance | Every valid `2 <= n <= p <= 9` | Generate an assignment | Exactly `n` nonempty teams; each size is `floor(p/n)` or `ceil(p/n)`; size difference is at most `1` |
| `TeamRandomizer.shuffle` | AC13 shuffle properties | Every valid set of `2..9` distinct IDs; exhaustive deterministic random-decision streams | Enumerate all shuffle outcomes | Every permutation occurs equally often with probability `1/p!`; output contains every input ID exactly once |
| `TeamsScreen` | AC14 color and pattern | Three specified teams containing seven IDs | Render result | Seven effects at assignment positions; same-team tokens match; three token pairs are distinct; labels are `1`, `2`, or `3` |
| `TeamsState.onPointerMoved` | AC15 frozen result | ID `44` in Team `1` at `(120,400)` | Move ID `44` to `(220,500)` | Assignment is unchanged; effect remains at `(120,400)`; countdown is absent |
| `TeamsState.onPointerRemoved` | AC16 partial lift | Three-team result; at least one finger remains | Remove another recognized finger | Assignment remains unchanged; text remains `3 teams created` |
| `TeamsState.onPointerRemoved` | AC17 retention starts | Three-team result | Remove final active finger | Retention starts at `3,000 ms`; assignment and text remain through elapsed `2,999 ms` |
| `TeamsState.advanceTime` | AC18 retention expires | No fingers active; retention elapsed is `2,999 ms` | Advance `1 ms` | Assignment is empty; no team effect; text is `Players detected: 0`; configured count is unchanged |
| `TeamsState.onPointerAdded` | AC19 new session during retention | Previous three-team result; retention remaining is `3,000 ms` | Add ID `88` | Previous assignment is empty; retention canceled; one ordinary indicator; texts are `Players detected: 1` and `Place at least 3 fingers` |
| `TeamsTouchAdapter.onCancel` | AC20 gesture canceled | Collection or countdown active | Receive Android gesture cancellation | Pointer collection and assignment are empty; countdown absent; text is `Players detected: 0` |
| `TeamsTouchAdapter.onPointerAdded` | AC21 tenth pointer | Nine IDs already recognized | Add a tenth pointer | Count and indicators remain `9`; text is `Maximum 9 players supported`; tenth ID is not assignable |
| `TeamsState.resetSession` | AC22 in-process reset | Configured team count is `4`; retention expires | Return to collection | Count remains `4`; texts are `Teams: 4` and `Place at least 4 fingers` |
| `TeamsState.initial` | AC23 process restart | Previous count was `4` and a result existed | Create state in a new app process | Count is `2`; assignment is empty; text is `Players detected: 0`; no team effect exists |

A physical-device QA pass must additionally confirm simultaneous multi-touch reporting, system-gesture cancellation, phone usability, and tablet smoke behavior because those properties cannot be fully established by JVM or emulator tests.

## Spec Readiness checklist (run before calling the spec done)

- [x] Every AC has a precise expected value — no "works correctly"
- [x] Another person could write a test from each AC without asking
- [x] Every AC can fail — one that cannot fail proves nothing
- [x] Error and edge cases have ACs of their own
- [x] Every AC appears in the testing strategy table
