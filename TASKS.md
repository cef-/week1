# Start Player Tasks

Follow RED-GREEN-REFACTOR for each automated AC. Before implementation, change
the approved spec to `In Progress`, scaffold the project, replace `<package>` in
the spec and these documents with the actual package, and add necessary scaffold
paths to the spec's Files to Modify table.

1. Scaffold the minimal Android/Compose app and implement initial Start Player state plus exact default UI text. Closes AC2.
2. Add selection-count state transition and threshold guidance text. Closes AC3.
3. Add `1..8` count controls and disabled boundary semantics. Closes AC4.
4. Add stable-ID pointer collection, live positions, detected count, and four-indicator rendering. Closes AC5.
5. Keep an insufficient touch set in collection with no timer or selection. Closes AC6.
6. Start the virtual-time settling countdown when `k + 1` pointers become active. Closes AC7.
7. Restart the countdown when a pointer is added. Closes AC8.
8. Restart the countdown when a pointer is removed but the threshold remains met. Closes AC9.
9. Cancel the countdown when pointer removal drops below the threshold. Closes AC10.
10. Update pointer position without restarting the countdown. Closes AC11.
11. Implement injected, Android-free selection of exactly `k` distinct controlled IDs. Closes AC12.
12. Exhaustively prove and preserve uniform `k`-subset selection for all supported `p,k` boundaries. Closes AC13.
13. Render selected positions with both the result color and pattern tokens and leave other pointers ordinary. Closes AC14.
14. Snapshot selected coordinates and ignore later movement for result placement. Closes AC15.
15. Keep the result unchanged while any finger remains active. Closes AC16.
16. Start `3,000 ms` retention on final lift and prove the `2,999 ms` boundary. Closes AC17.
17. Reset result state after the final retention millisecond while preserving configuration. Closes AC18.
18. Cancel retention and begin a fresh collection immediately when a new pointer arrives. Closes AC19.
19. Translate Android gesture cancellation into complete collection/countdown cleanup. Closes AC20.
20. Cap recognized pointers at nine, expose the exact maximum message, and exclude the tenth ID from selector input. Closes AC21.
21. Return from an in-process result reset with the configured count and matching threshold text intact. Closes AC22.
22. Ensure a newly constructed process state receives no prior count, pointers, result, or effects. Closes AC23.
23. Run the two-person, two-finger physical-device procedure and record the required device evidence. Closes AC1.
24. Run verified wrapper tasks `testDebugUnitTest`, `lintDebug`, `connectedDebugAndroidTest` when a target is available, and `check`; resolve failures against their mapped ACs. Closes AC2–AC23 only when all corresponding evidence passes.

