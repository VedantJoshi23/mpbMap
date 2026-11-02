---
id: STD-TESTING
title: "Testing"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M4
category: Standard
priority: Critical
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - ADR-0003
  - ADR-0005
  - ADR-0006
  - ADR-0008
  - DISC-DATA-MODEL
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions: []
tags:
  - standard
risk: High
complexity: Low
---

# STD-TESTING: Testing

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). **Tier: High** (Oriveda ADR-0005). Lifecycle:
**Frozen** v1.0.0 (2026-11-02), approved by the owner proxy after rounds
1–2 (KC-162, ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Unit, integration, instrumented and spike tests for the app and backend.

## Rules

1. **The alarm rules are tested exhaustively.** R1–R4, R17, R22, R24, R25
   and R26 each have unit tests on `feature/trip/domain`, with fake clock
   and location, including:
   - arming inside the radius;
   - multiple alarms;
   - location loss;
   - stale trips;
   - snooze.
2. **Safety-path instrumented tests** (ADR-0005):
   - (a) the foreground-service ring loops on `USAGE_ALARM` until
     dismissed;
   - (b) the no-service path uses `FLAG_INSISTENT` and loops;
   - (c) the Law 14 vibration repeats with the sound;
   - (d) (a) and (b) both work under DND set to "alarms allowed" and on
     the lock screen;
   - (e) the watchdog reschedules itself on every firing;
   - (f) a stale, missing or future heartbeat raises R25
     (DISC-DATA-MODEL v1.2.0);
   - (g) a trip that ends cancels the watchdog;
   - (h) with Play services missing or outdated, pre-flight switches to
     the LocationManager adapter or blocks the start; it never starts a
     trip without fixes;
   - (i) the direct-boot receiver uses DP storage only, and the test
     asserts there is no CE access.
3. **The demo sandbox** has a test proving it starts no foreground
   service, persists nothing and emits no exploration events (J12).
4. **Erasure** (ADR-0006, ADR-0008):
   - EraseLocalHistory empties LocationFix, JournalEntry and
     ExploredCell;
   - a crash mid-erasure resumes on the next start;
   - a re-run is a no-op;
   - the UI shows "complete" only after every context has confirmed;
   - account deletion purges server rows and **revokes every session**.
5. **Backend:** route tests for BC9 and BC10 against Testcontainers
   Postgres, covering:
   - rate limits;
   - no coordinates in application **or nginx** logs (Law 5);
   - the ACRA endpoint rejecting location fields.
6. **Permanent regression tests:**
   - the **H3 golden-ID test**: the JVM fallback must produce identical
     IDs (ADR-0006);
   - the **runtime boundary guard**: a style injected with a `boundary`
     source-layer renders without it (ADR-0003);
   - the **style lint** fails on a boundary reference and on a hidden
     attribution control.
7. **Coverage:** branch coverage of at least 90% for
   `feature/trip/domain` and at least 70% for the other domain modules.
   UI is covered by semantic tests on core flows.
8. **Device matrix:**
   - minSdk 26 and the current targetSdk;
   - at least two OEMs, including one aggressive-OEM device;
   - one low-end device;
   - recorded per release.
9. The refresh token is verified to be Keystore-wrapped (ADR-0008).

## Examples

- Compliant:
  `ArrivalEvaluatorTest.armsInsideRadius_firesAtArrivalDistance()`.
- Non-compliant: an alarm change merged without a failing-then-passing
  test.

## Exceptions

Spike code is exempt, **except** the tests in rule 6, which become
permanent.

## Enforcement

CI coverage gates; a PRM-REVIEW checklist ("which R-rules does this
touch, and where are their tests?").

## Architecture Review (2026-11-02)

Checked against CONST-CORE Laws 1–14, ARCH-SYSTEM, and ADR-0002..0009 as
amended: no contradiction. Result: passes.
