---
id: STD-TESTING
title: "Testing"
version: 0.1.0
status: Draft
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
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Unit, integration, instrumented and spike tests for the app and backend.

## Rules

1. **The alarm rules are tested exhaustively.** R1–R4, R17, R22, R24, R25
   and R26 each have unit tests on `feature/trip/domain`, with fake clock
   and location, including edge cases:
   - arming inside the radius;
   - multiple alarms;
   - location loss;
   - stale trips;
   - snooze.

   *Rationale: Law 1, Production-grade.*
2. **Safety-path instrumented tests:**
   - FGS start/stop;
   - the direct-boot receiver with DP storage only (no CE access, which
     the test asserts);
   - the watchdog firing;
   - the alert on every channel, including the non-audio one (Law 14).
3. **The demo sandbox** has a test proving it starts no FGS and persists
   nothing (J12).
4. **Erasure tests** prove EraseLocalHistory empties LocationFix,
   JournalEntry and ExploredCell, and that account deletion purges server
   rows (Law 6).
5. **Backend:** route tests for BC9 and BC10, including rate limits and
   "no coordinates in logs" (Law 5), against Testcontainers Postgres.
6. **Coverage:** branch coverage of at least 90% for
   `feature/trip/domain` and at least 70% for the other domain modules.
   UI is covered by screenshot or semantic tests on core flows.
7. **Device matrix for S3-type tests:** at least two OEMs plus one
   low-end device, recorded per release.

## Examples

- Compliant: `ArrivalEvaluatorTest.armsInsideRadius_firesAtArrivalDistance()`.
- Non-compliant: an alarm change merged without a failing-then-passing
  test.

## Exceptions

Spike code (M8 spikes) is exempt, but spike *findings* are recorded.

## Enforcement

CI coverage gates; a PRM-REVIEW checklist item ("which R-rules does this
touch, and where are their tests?").
