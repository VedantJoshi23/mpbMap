---
id: STD-CODE
title: "Code Conventions"
version: 1.0.0
status: Frozen
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
  - ADR-0002
  - ADR-0005
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
CONST-CORE Law (Frozen). **Tier: Medium** (Oriveda ADR-0005). Lifecycle:
**Frozen** v1.0.0 (2026-11-02), approved by the owner proxy after rounds
1–2 (KC-170, ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Kotlin code in `android/` and `backend/`. Excludes skin style JSON
(STD-SKINS).

## Rules

1. **Module boundaries** (ARCH-SYSTEM §4 rules 1–5) are enforced at
   build time. A feature imports only `core/*` and other features'
   `api/`. **`feature/*/api` depends only on `core/*`**, which keeps
   mutual api-level dependencies acyclic in Gradle. *Rationale: Law 13.*
2. **Domain purity.** `feature/*/domain` holds no Android framework
   imports. Time, location and storage come in through interfaces.
3. **Direct boot under Hilt** (ADR-0002, Law 1):
   - `Application.onCreate`, and every eagerly reached `@Singleton`,
     never touches CE storage;
   - CE stores are injected as `Lazy<T>` or `Provider<T>`;
   - the boot receiver **and the self-rescheduling watchdog receiver**
     live in `feature/trip/platform/boot`, within ARCH-SYSTEM §4 rule 4's
     allowlist;
   - if S3 shows this is not enough, manual DI is adopted through a draft
     revision.
4. **Coroutines and Flow** for async work. No blocking calls on the main
   thread. Inject dispatchers so tests can control them.
5. **Errors:**
   - Domain functions return typed results for expected failures. The
     safety path catches everything and converts it to an AlertPort
     alert (Law 1).
   - **Exception and log messages never interpolate coordinates or
     location objects** (Laws 5, 8), so they never reach crash reports.
6. **Formatting:** ktlint (official style) and detekt, with zero warnings
   on new code. A detekt custom rule flags string templates that include
   location types.
7. **Naming:** context names match ARCH-SYSTEM. Events are named in the
   past tense.
8. **No secrets or hostnames in code**, except the sanctioned API
   bootstrap default.

## Examples

- Compliant: `ArrivalEvaluator` takes a `Clock` and a
  `DistanceCalculator`.
- Non-compliant: `throw IllegalStateException("bad fix $latLng")`.

## Exceptions

Generated code is excluded from detekt. Any other deviation needs a code
comment citing an ADR.

## Enforcement

CI:
- ktlint and detekt, including the custom location-interpolation rule;
- module-graph assertions, plus the rule-4 allowlist check;
- a **dependency denylist** for ads, analytics, Firebase Analytics and
  Crashlytics (ARCH-SYSTEM §4 rule 5; Law 8). Play Integrity and
  Credential Manager are explicitly allowed.

## Architecture Review (2026-11-02)

Checked against CONST-CORE Laws 1–14, ARCH-SYSTEM, and ADR-0002..0009 as
amended: no contradiction. Result: passes.
