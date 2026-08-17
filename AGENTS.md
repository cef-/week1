# AGENTS.md — StartPlayer-tbd

> Operating manual for AI agents in this repo. The model reads this file
> at the start of every session — anything written here never needs
> repeating in a prompt.
> Full reference template (adds a documentation part this greenfield
> project does not need yet):
> https://github.com/pasilastbot/ai_training/blob/main/AGENTS.md

## What this is

This app helps with randomization tasks during setup before starting board games. Supported tasks include:
1) selecting random start player(s) among the players
2) assigning players to randomized teams
Using the app requires minimal user input and does not require the user to enter any text.

## Key Commands

Run commands from the repository root.

These commands become authoritative after the Android project and
Gradle wrapper have been created. Until then, report them as unavailable
rather than claiming they were run.

After scaffolding:
1. Run the wrapper's `tasks` command.
2. Confirm every command below exists.
3. Update this section if generated task names differ.

### Gradle wrapper

- Windows: `.\gradlew.bat <task>`
- macOS/Linux: `./gradlew <task>`

### Tasks

- `assembleDebug` — build the debug APK
- `testDebugUnitTest` — run JVM unit tests
- `lintDebug` — run Android lint
- `connectedDebugAndroidTest` — run instrumented tests on a connected device or emulator
- `installDebug` — install the debug build on a connected device or emulator
- `check` — run the complete local verification suite
- `tasks` — list available Gradle tasks

### Verification Rules

- After business-logic changes, run the wrapper task `testDebugUnitTest`.
- After UI or Android integration changes, run the wrapper tasks `testDebugUnitTest` and `lintDebug`.
- Before calling a feature complete, run the wrapper task `check`.
- Run the wrapper task `connectedDebugAndroidTest` when a device or emulator is available.
- Multi-touch acceptance criteria must also be tested manually on a physical Android device.
- Do not claim physical-device ACs passed based only on unit tests or emulator tests.
- Do not run `clean` routinely; use it only when diagnosing stale build outputs.

## Workflows: research and spec

Named passes. Saying "run spec" gets the same discipline every time —
no re-explaining, no drift between sessions.

### research
Goal: understand the task before planning. Read-only.
1. Gather context: existing code when there is any, libraries and
   worked examples worth reusing, external API docs if needed
2. Identify scope: which files, what rules apply, what depends on what
3. Analyze: outline what needs doing, list what is still unknown
4. Present findings and ASK about every open question —
   never resolve a guess silently
No code, no spec, in this pass.

### spec
Goal: a specification before implementation.
Required for: new features, API changes, anything multi-file.
Optional for: typo-class fixes, config tweaks, docs.
Write specs/features/<name>.md — structure in specs/TEMPLATE.md.

Spec Readiness checklist — the spec is NOT ready until every box holds:
- [ ] Every AC is Given/When/Then with a precise expected value
- [ ] Files to modify are listed with what changes in each
- [ ] Risk: what could break, and how to roll back
- [ ] Testing strategy covers every AC, plus error and edge cases
- [ ] Every AC has at least one named test case
An incomplete testing strategy means the spec is not approved.

### Spec status

Each feature spec has exactly one status:

- `Draft` — incomplete or awaiting review
- `Approved` — readiness checklist passes and the user approved implementation
- `In Progress` — implementation has started
- `Done` — all automated tests pass and all required manual evidence exists

Allowed transitions:

`Draft -> Approved -> In Progress -> Done`

Do not implement a Draft spec. Only the user may approve a spec.
Do not mark a spec Done while an AC is unverified.

## Workflows: tdd, develop, review

### tdd
Prerequisite: a spec with a testing strategy (run spec first).
Classify each acceptance criterion from its testing-strategy row:
- Automated AC: follow RED-GREEN-REFACTOR below.
- Manual AC: label the row `Physical-device QA` and document the
  exact setup, user actions, expected visible values, and required
  device capability. Do not invent an automated RED step. Add lower-
  level automated coverage where practical, then execute the AC on a
  physical device and record device model, Android version, app build,
  date, and PASS or FAIL evidence. A manual AC remains unverified until
  that evidence exists.

For EACH automated acceptance criterion, in order:
  RED      write the failing test for THIS AC only; the test name
           states the AC; run it and confirm it fails FOR THE RIGHT
           REASON (missing behaviour, not a broken import)
  GREEN    smallest implementation that passes this test; run ALL
           tests, confirm no regressions
  REFACTOR remove duplication, improve names; tests stay green
Then repeat the cycle for edge cases: invalid input, boundaries,
error paths.

Two traps, both near-certain:
- The model writes test and implementation in one pass. The test is
  then derived from the code and always passes. Ask separately.
- The model "fixes" a failing test to match the code. The spec
  decides which one is wrong — correct the spec first, then the test.

### develop
For work that has a spec: require status `Approved`, change it to
`In Progress` before implementation, follow the listed scope, and
change it to `Done` only after every AC has verification evidence.

Change the files listed in the spec and any directly necessary
supporting files, such as Gradle configuration, manifest entries,
resources, navigation wiring, or test fixtures.

Before modifying an unlisted file:
1. Confirm the change is necessary for an AC.
2. Add the actual path and purpose to the spec's Files to Modify table.
3. Keep the change limited to that purpose.

Do not use this exception for unrelated refactoring or cleanup.

When the project has not been scaffolded, placeholder paths such as
`<package>` are provisional. Resolve them during scaffolding and update
the spec with actual paths before feature implementation continues.

### review
Compare the diff against the spec: which AC each change serves, what
changed that no AC asked for, which tests prove what. End with a
verdict: APPROVED or CHANGES_REQUIRED — never prose that cannot be
branched on.

## Coding Conventions

### Kotlin

- Follow the official Kotlin coding conventions.
- Use Kotlin strict null safety; do not use `!!` unless the reason is documented.
- Prefer immutable values and collections. Use `val` unless mutation is required.
- Use descriptive names; avoid abbreviations except established terms such as `UI`, `ID`, and `AC`.
- Keep functions focused on one responsibility.
- Do not suppress compiler or lint warnings without an explanatory comment.

### Architecture

- Keep randomization and team-assignment logic free of Android APIs.
- Represent players in shared logic using opaque, session-local identifiers.
- Keep Android pointer handling, coordinates, drawing, and lifecycle behavior in the Android UI layer.
- Use unidirectional data flow: UI sends actions; state holders update immutable UI state; UI renders that state.
- Use one explicit state model for collection, countdown, result display, retention, and reset.
- Structure portable business logic so it can later move to Kotlin Multiplatform `commonMain`.

### Jetpack Compose

- Prefer stateless composables with state and event callbacks passed as parameters.
- Hoist screen-level state out of leaf composables.
- Do not perform randomization, timer creation, or pointer-state mutation directly while composing UI.
- Put user-visible text in Android string resources.
- Use stable keys for touch indicators.
- Keep interactive controls outside system-gesture insets.
- Do not use color as the only result indicator; combine color with pattern and a visible label or number.
- Provide content descriptions or semantics for controls and status information.

### Multi-touch

- Track fingers by stable pointer ID, never by pointer-array index.
- Treat pointer addition, removal, and cancellation as membership changes.
- Treat pointer movement as a position update, not a membership change.
- Cap recognized players at `9`.
- Clear active pointers when Android cancels the gesture.
- Keep result positions frozen at their selection or assignment coordinates.
- Do not assume every Android device supports nine simultaneous touches.

### Randomness and time

- Inject the random source into selection and team-assignment logic.
- Never create an uncontrolled random source inside business-logic functions.
- Inject or abstract time for countdown and retention behavior.
- Do not use `Thread.sleep` in application code or tests.
- Define `2,000 ms` randomization delay and `3,000 ms` result-retention delay as named constants.
- Random selection must not return duplicate players.
- Team assignment must include every player exactly once and keep team sizes within one player.

### Coroutines

- Use structured concurrency.
- Do not use `GlobalScope`.
- Cancel countdown and retention jobs when their state becomes invalid.
- Keep dispatcher selection injectable when required by tests.
- Do not block the main thread.

### Testing

- Use controlled randomness in unit tests.
- Use a fake clock or test scheduler for timing tests.
- Test business logic without Compose or Android dependencies.
- Test exact state values and exact displayed strings.

## Guardrails

### Specification authority

- Treat approved files in `specs/features/` as the authoritative product behavior.
- Do not implement behavior that contradicts an approved acceptance criterion.
- If requirements conflict, stop and ask for clarification before changing code.
- When requested behavior changes an approved AC, update the specification before implementing it.
- Do not silently weaken, remove, or reinterpret an AC to simplify implementation or testing.
- Do not mark a feature complete unless every AC has corresponding verification evidence.

### Scope control

- Implement only the requested feature and its necessary supporting changes.
- Do not add unrelated features, screens, settings, persistence, analytics, accounts, networking, or permissions.
- Do not combine Start Player and Teams into one feature or one result flow.
- Do not perform broad refactoring unless required by the requested change.
- Preserve unrelated files and user-authored changes.
- Do not reformat files outside the active change.

### Architecture boundaries

- Do not identify players using names, accounts, or persisted identifiers.
- Do not persist touch data, player assignments, or results.
- Do not introduce a third-party randomization library; use Kotlin’s standard facilities behind an injectable abstraction.

### Multi-touch safety

- Never include more than `9` players in a result.
- Never generate a Start Player result unless at least `k + 1` fingers are recognized.
- Never generate a Teams result unless at least `n` fingers are recognized.
- Never create an empty team or assign one player to multiple teams.

### Randomness and timing

- Do not use deterministic production seeds.
- Do not use cryptographic or network-based randomness; this app does not require it.
- Do not write flaky statistical tests.
- Never generate more than one result for the same settled touch session.

### UI and accessibility

- Do not display internal pointer IDs to users.
- Keep the detected-player count visible during touch collection.
- Do not move or recalculate a displayed result after randomization.
- Do not add text-entry requirements for players.

### Dependencies and configuration

- Do not add or upgrade dependencies without checking whether the standard library or existing dependencies already solve the problem.
- Ask before adding a dependency that materially changes architecture, build tooling, licensing, or application permissions.
- Use the repository Gradle wrapper; do not require globally installed Gradle.
- Do not edit signing configurations, keystores, publishing credentials, or release settings without explicit authorization.
- Never commit secrets, API keys, local SDK paths, keystores, or `local.properties`.

### Git and filesystem safety

- Do not discard, overwrite, or revert user changes.
- Do not run destructive Git commands such as `git reset --hard` or forced checkout without explicit authorization.
- Do not force-push.
- Do not commit, push, publish, or open a pull request unless explicitly requested.
- Do not modify files outside this repository unless explicitly requested.
- Keep generated build output out of version control.

### Verification and completion

- Do not claim tests passed unless they were actually executed successfully.
- Report tests that could not be run and the exact reason.
- Do not call a feature complete while lint, required tests, or mapped AC checks are failing.
- Do not delete or skip a failing test merely to obtain a green build.
