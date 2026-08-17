# Start Player Test Plan

`[A]` means machine-checkable automation. `[?]` means the full AC requires
physical-device observation and must not be claimed from an emulator or unit
test. Virtual time and controlled randomness are used wherever timing or
selection is involved.

| AC | Test | Exact input | Exact proof |
|---|---|---|---|
| AC99 | `[?] Physical-device QA: one of two players` | Physical Android device; count `1`; two people hold one finger each for `2,000 ms` while detected count stays `2` | Exactly one of two indicators has both result tokens, one remains unselected, and text is `1 starting player selected`. |
| AC1 | `[A] StartPlayerStateTest.AC1_defaultCount` | Create initial state | Count `1`; text `Starting players: 1`. |
| AC2 | `[A] StartPlayerStateTest.AC2_setCountToThree` | Initial count `1`; set count to `3` | Count `3`; texts `Starting players: 3` and `Place at least 4 fingers`. |
| AC3 | `[A] StartPlayerScreenTest.AC3_countBounds` | Attempt decrement at `1`; attempt increment at `8` | Values offered are exactly `1..8`; decrement disabled at `1`; increment disabled at `8`. |
| AC4 | `[A] StartPlayerScreenTest.AC4_fourPointers` | IDs `[11,22,33,44]` at `[(10,20),(30,40),(50,60),(70,80)]` | Text `Players detected: 4`; four indicators centered at the supplied positions. |
| AC5 | `[A] StartPlayerStateTest.AC5_belowThreshold` | `k=3`; IDs `[11,22,33]`; advance `2,000 ms` | Empty selection; no countdown; text `Place at least 4 fingers`. |
| AC6 | `[A] StartPlayerStateTest.AC6_thresholdStartsCountdown` | `k=3`; active `[11,22,33]`; add `44` | Countdown `2,000 ms`; text `Hold fingers in place: 2`. |
| AC7 | `[A] StartPlayerStateTest.AC7_addRestartsCountdown` | Countdown `1,200 ms`; add ID `55` | Countdown resets to `2,000 ms`; text `Hold fingers in place: 2`. |
| AC8 | `[A] StartPlayerStateTest.AC8_removeRestartsCountdown` | `k=2`; active `[11,22,33,44]`; countdown `1,200 ms`; remove `44` | Countdown resets to `2,000 ms`; text `Hold fingers in place: 2`. |
| AC9 | `[A] StartPlayerStateTest.AC9_removeCancelsCountdown` | `k=2`; active `[11,22,33]`; countdown active; remove `33` | No countdown, empty selection, text `Place at least 3 fingers`. |
| AC10 | `[A] StartPlayerStateTest.AC10_moveDoesNotRestart` | Countdown `1,200 ms`; move ID `11` from `(10,20)` to `(30,40)` | Countdown remains `1,200 ms` at event time; indicator center is `(30,40)`. |
| AC11 | `[A] StartPlayerSelectorTest.AC11_selectConfiguredIDs` | IDs `[11,22,33,44]`; `k=2`; controlled choices `[33,11]`; advance `2,000 ms` | Selection `[33,11]`; two effects; `22`,`44` unselected; text `2 starting players selected`. |
| AC12 | `[A] StartPlayerSelectorTest.AC12_allSubsetsUniform` | Every `p=2..9`, every `k=1..p-1`, and exhaustive controlled decision streams | Each `k`-subset occurs equally often, maps to `1/C(p,k)`, and contains no duplicate ID. |
| AC13 | `[A] StartPlayerScreenTest.AC13_colorAndPattern` | Selected IDs `33`,`11` with saved positions; other IDs unselected | One effect at each selected saved position has both tokens; unselected indicators have neither. |
| AC14 | `[A] StartPlayerStateTest.AC14_resultPositionFrozen` | ID `33` selected at `(120,400)`; move it to `(220,500)` | Selection unchanged; result stays `(120,400)`; no countdown. |
| AC15 | `[A] StartPlayerStateTest.AC15_partialLiftKeepsResult` | Selection `[33,11]`; two active fingers; remove one | Selection `[33,11]`; text `2 starting players selected`. |
| AC16 | `[A] StartPlayerStateTest.AC16_retentionBoundary` | Selection `[33,11]`; remove final finger; advance `2,999 ms` | Retention starts at `3,000 ms`; selection and text remain through `2,999 ms`. |
| AC17 | `[A] StartPlayerStateTest.AC17_retentionExpires` | No fingers; retention elapsed `2,999 ms`; advance `1 ms` | Empty selection; zero effects; text `Players detected: 0`; configured count unchanged. |
| AC18 | `[A] StartPlayerStateTest.AC18_newFingerDuringRetention` | `k=2`; prior two-player result; retention `3,000 ms`; add ID `55` | Previous selection cleared; timer canceled; one ordinary indicator; exact count and threshold texts. |
| AC19 | `[A] StartPlayerTouchAdapterTest.AC19_cancelGesture` | Collection and countdown active; send Android cancel event | Empty pointer and selection collections; no countdown; text `Players detected: 0`. |
| AC20 | `[A] StartPlayerTouchAdapterTest.AC20_ignoreTenthPointer` | Nine active IDs `[1..9]`; add ID `10` | Nine recognized IDs and indicators; maximum text shown; ID `10` absent from selector input. |
| AC21 | `[A] StartPlayerStateTest.AC21_keepCountAfterReset` | Count `3`; completed result; advance retention by `3,000 ms` | Count `3`; texts `Starting players: 3` and `Place at least 4 fingers`. |
| AC22 | `[A] StartPlayerStateTest.AC22_newProcessStateIsDefault` | Dispose state having count `3` and a result; construct a new application state with no saved state | Count `1`; empty selection; text `Players detected: 0`; zero selected effects. |

## Additional Manual Coverage

Record device model, Android version, app build, date, and PASS/FAIL for AC99.
The same physical-device pass also observes real multi-touch reporting, system-
gesture cancellation, phone usability, and tablet smoke behavior; these checks
supplement but do not replace the automated AC mappings above.
