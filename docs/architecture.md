# Start Player Architecture

This document covers only the approved Start Player feature. The package prefix
is written as `<package>` until Android project scaffolding establishes the real
application ID; the spec must be updated with that path before feature work
continues.

## Ownership

| File | Owner and one-line justification |
|---|---|
| `settings.gradle.kts` | Declares the single Android application module required to build the app. |
| `build.gradle.kts` | Declares repository-wide Android and Kotlin plugins without feature behavior. |
| `gradle/libs.versions.toml` | Keeps the small set of Android, Compose, Kotlin, coroutine, and test versions in one place. |
| `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties` | Provide the repository-owned, cross-platform Gradle runtime required by `AGENTS.md`. |
| `app/build.gradle.kts` | Configures the Android application, Compose, and the unit/instrumented test source sets needed by the ACs. |
| `app/src/main/AndroidManifest.xml` | Declares the launcher activity; no extra permissions or services are needed. |
| `app/src/main/kotlin/<package>/MainActivity.kt` | Hosts Compose and opens the Start Player screen as the initial feature entry point. |
| `app/src/main/kotlin/<package>/ui/theme/Theme.kt` | Supplies app-level Compose theming while feature-specific result tokens remain owned by the feature screen. |
| `app/src/main/res/values/strings.xml` | Owns every user-visible string so exact AC text is centralized and testable. |
| `app/src/main/kotlin/<package>/startplayer/StartPlayerState.kt` | Owns immutable UI state and all collection, countdown, result, retention, reset, count, and cancellation transitions. |
| `app/src/main/kotlin/<package>/startplayer/StartPlayerSelector.kt` | Owns Android-free selection of exactly `k` unique opaque IDs through an injected random source. |
| `app/src/main/kotlin/<package>/startplayer/StartPlayerTouchAdapter.kt` | Translates Android pointer events into stable-ID add, move, remove, and cancel actions while enforcing the nine-player cap. |
| `app/src/main/kotlin/<package>/startplayer/StartPlayerScreen.kt` | Renders controls, exact status text, current touch positions, and frozen color-plus-pattern result effects from state. |
| `app/src/test/kotlin/<package>/startplayer/StartPlayerSelectorTest.kt` | Proves deterministic selection, uniqueness, exact count, and subset uniformity without Android. |
| `app/src/test/kotlin/<package>/startplayer/StartPlayerStateTest.kt` | Proves count boundaries and timed state transitions with injected randomness and virtual time. |
| `app/src/androidTest/kotlin/<package>/startplayer/StartPlayerScreenTest.kt` | Proves Compose text, semantics, controls, indicator counts, positions, and visual-token assignments. |
| `app/src/androidTest/kotlin/<package>/startplayer/StartPlayerTouchAdapterTest.kt` | Proves Android pointer-event translation, cancellation, movement, and the tenth-pointer exclusion at the platform boundary. |

## Data and Event Flow

`StartPlayerScreen` converts gestures through `StartPlayerTouchAdapter` into
actions for `StartPlayerState`. State invokes `StartPlayerSelector` only after a
stable `2,000 ms` countdown, then exposes immutable render data back to the
screen. Android pointer objects and coordinates never enter the selector; no
state leaves the process.

## Not Built

- Teams mode is not built because it is a separate feature and result flow.
- Player names, accounts, text entry, and persisted identifiers are not built because touches are session-local players.
- Persistence, databases, networking, analytics, and backend services are not built because all state resets with the process.
- Navigation infrastructure is not built beyond the initial activity entry because only one screen is in this implementation scope.
- A shared/Kotlin Multiplatform module and iOS UI are not built yet; Android-free selector logic preserves a later extraction path without premature tooling.
- Third-party randomization, cryptographic randomness, and network randomness are not built because injected Kotlin standard randomness satisfies the feature.
- Custom tablet layouts are not built because tablets require smoke support while the initial layout is phone-optimized.
- Nine-touch emulation is not treated as proof because physical devices vary in simultaneous-touch capability.

