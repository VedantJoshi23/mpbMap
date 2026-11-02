---
id: STD-CODE
title: "Code Conventions"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M4
category: Standard
priority: High
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
risk: Medium
complexity: Low
---

# STD-CODE: Code Conventions

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Kotlin code in `android/` and `backend/`. Excludes skin style JSON
(STD-SKINS).

## Rules

1. **Module boundaries** (ARCH-SYSTEM §4 rules 1–5) are enforced at
   build time. A feature imports only `core/*` and other features' `api/`.
   *Rationale: Law 13.*
2. **Domain purity.** `feature/*/domain` holds no Android framework
   imports. Time, location and storage come in through interfaces.
   *Rationale: testable alarm logic.*
3. **Coroutines and Flow** for async work. No blocking calls on the main
   thread. Inject dispatchers so tests can control them.
4. **Errors.** Domain functions return typed results (sealed classes) for
   expected failures. Exceptions are for programmer errors only. The
   safety path catches everything and converts it to an AlertPort alert
   (Law 1).
5. **Formatting.** ktlint (official Kotlin style) and detekt, with zero
   warnings on new code.
6. **Naming.** Context names match ARCH-SYSTEM (`trip`, `location`, …).
   Events are named in the past tense (`TripStarted`).
7. **No secrets or hostnames in code**, except the sanctioned API
   bootstrap default (DISC-TECHNICAL-ARCHITECTURE v1.1.0).

## Examples

- Compliant:
  `feature/trip/domain/ArrivalEvaluator.kt` takes a `Clock` and a
  `DistanceCalculator`.
- Non-compliant:
  `feature/exploration` imports `feature/location/data/FixDao`.

## Exceptions

Generated code is excluded from detekt. Any other deviation needs a code
comment citing an ADR.

## Enforcement

CI: ktlint, detekt, and module dependency checks (Gradle module graph
assertions, e.g. a dependency-analysis plugin plus a custom rule for §4
rule 4's allowlist).
