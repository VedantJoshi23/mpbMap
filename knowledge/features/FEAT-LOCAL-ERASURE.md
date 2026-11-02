---
id: FEAT-LOCAL-ERASURE
title: "Etak — Feature: Local History Erasure"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M6
category: Features
priority: Critical
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
  - ADR-0006
  - DOM-EXPLORATION
  - STD-DATABASE
  - STD-TESTING
required_by: []
related_documents: []
related_domains:
  - DOM-EXPLORATION
  - DOM-LOCATION
  - DOM-TRIP
related_features: []
related_decisions: []
tags:
  - feature
risk: High
complexity: Medium
---

## Feature: Local History Erasure

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-EXPLORATION` v1.0.0 and `DOM-LOCATION` v1.0.0, `DOM-TRIP` v1.0.0. STD-* v1.0.0 (all Frozen). The revisions it relied on (DOM-EXPLORATION v1) are now applied and Frozen (KC-165).1.0, DOM-TRIP v1.1.0 and ARCH-SYSTEM v1.2.0 (the safety-log part of erasure).

**Tier:** **High.** Law 6. Split out of FEAT-EXPLORE-REVEAL. *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **2.** Persistence of LocationFix and the safety debug log starts in the same slice. Slice 2 creates `exploration.db` with the command table only. The erasure deletes nothing there until slice 4 adds the content tables.

### 1. Overview

'Clear history' removes the journal, explored cells, the raw location log and the safety debug log, completely and verifiably, even across crashes.

### 2. Owning Domain

**Owning domain:** `DOM-EXPLORATION`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- DOM-EXPLORATION → DOM-LOCATION (Allowed): `LocationApi.eraseFixes()`.
- DOM-EXPLORATION → DOM-TRIP (Allowed, BC4→BC1): `TripApi.clearSafetyLog()` (DOM-TRIP v1.1.0).

### 3. Acceptance Criteria

- [ ] The EraseLocalHistory command is **persisted** in `exploration.db` before any deletion.
- [ ] It deletes JournalEntry and ExploredCell and sets `clearedAt`.
- [ ] It calls `LocationApi.eraseFixes()` and `TripApi.clearSafetyLog()` and records each confirmation.
- [ ] It **retries on start** until every part is confirmed. A re-run is a no-op.
- [ ] ExplorationCleared is published, and 'complete' shown, **only after every part has confirmed**.
- [ ] Signed-in users are warned that the `clearedAt` epoch syncs and clears the server and their other devices (J11). Pre-clear cells cannot resurrect (epoch-then-union).

### 4. API Surface

- `ExplorationApi.eraseLocalHistory()`

### 5. Events

#### Publishes

- ExplorationCleared

#### Consumes

- None.

*(a subset of `DOM-EXPLORATION`'s declared events)*

### 6. Data Changes

- `exploration.db`: the pending-erasure command, ExploredArea.clearedAt

### 7. Edge Cases & Validations

- A crash mid-erasure: resumed on the next start.
- Location revoked during erasure: eraseFixes still runs (local delete).

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-TESTING | Yes | Rule 4. |
| STD-DATABASE | Yes | Rule 7. |
| STD-OBSERVABILITY | Yes | Rule 5 (the log is cleared). |
| STD-COMPLIANCE | Yes | Rule 4 (retention facts). |
| STD-ACCESSIBILITY | Yes | Rule 2. |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-SECURITY | No |  |
| STD-API | No |  |
| STD-PERFORMANCE | No |  |
| STD-SKINS | No |  |

### 9. Definition of Done

- [ ] Criteria pass, including the crash-resume test.
- [ ] DOM-EXPLORATION v1.1.0, DOM-TRIP v1.1.0 and ARCH-SYSTEM v1.2.0 are Frozen.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).
