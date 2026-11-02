---
id: FEAT-EXPLORE-REVEAL
title: "Kairos — Feature: Explore-to-Reveal & Journal"
version: 1.0.1
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M6
category: Features
priority: Medium
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
  - ADR-0006
  - DOM-EXPLORATION
required_by: []
related_documents: []
related_domains:
  - DOM-EXPLORATION
  - DOM-LOCATION
  - DOM-TRIP
  - DOM-ACCOUNT
  - DOM-PLACES
  - DOM-MAP
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: Medium
---

## Feature: Explore-to-Reveal & Journal

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-EXPLORATION` v1.0.0 and `DOM-LOCATION` v1.0.0, `DOM-TRIP` v1.0.0, `DOM-ACCOUNT` v1.0.0, `DOM-PLACES` v1.0.0, `DOM-MAP` v1.0.0. STD-* v1.0.0 (all Frozen).

**Tier:** **Medium.** Erasure moved to FEAT-LOCAL-ERASURE. *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **4.**

### 1. Overview

An opt-in fog of war over every skin that clears as the user explores, and a journal of visited places.

### 2. Owning Domain

**Owning domain:** `DOM-EXPLORATION`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- DOM-EXPLORATION → DOM-LOCATION (Allowed): fixes; LocationAccessChanged; records the R18 consent.
- DOM-EXPLORATION → DOM-TRIP (Allowed): TripStarted, TripEnded and TripResumed for R18 gating.
- DOM-EXPLORATION consumes RemoteChangesApplied and SignedIn/SignedOut/AccountDeleted (DOM-ACCOUNT, Allowed).
- **via app/**: the `CityResolver` port. Its adapter wraps `PlacesApi.reverse()`, a backend call.
- **via app/**: the fog overlay is passed into the DOM-MAP Composable.

### 3. Acceptance Criteria

- [ ] Reveal is off by default. When off, the whole map is visible (R15).
- [ ] With reveal on, H3 resolution-9 cells clear while the app is in use.
- [ ] Trip-time reveal happens only with the double opt-in (R18). The journal grows **from in-use location only**.
- [ ] The journal lists visited places and city counts. The city comes from the `CityResolver`, whose reverse-geocode coordinates go in **POST bodies, coarsened** (STD-API 4). Offline: 'unknown city', filled in later.
- [ ] The fog meets contrast over every skin (STD-ACCESSIBILITY 5).

### 4. API Surface

- `ExplorationApi` (DOM-EXPLORATION §4)
- Port `CityResolver`

### 5. Events

#### Publishes

- ExploredCellsAdded

#### Consumes

- LocationFixAcquired, LocationAccessChanged
- TripStarted, TripEnded, TripResumed
- RemoteChangesApplied, SignedIn/SignedOut/AccountDeleted (keep-or-clear)

*(a subset of `DOM-EXPLORATION`'s declared events)*

### 6. Data Changes

- `exploration.db`: ExploredCell, ExploredArea, JournalEntry

### 7. Edge Cases & Validations

- An old offline device syncs pre-clear cells: dropped by epoch.
- Unknown city offline: filled in later.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-API | Yes | Rule 4 (reverse geocoding via CityResolver). |
| STD-COMPLIANCE | Yes | Rule 3 (transient transfer); the disclosure lists the exploration map. |
| STD-TESTING | Yes | Rule 6 (golden IDs). |
| STD-DATABASE | Yes | Rules 1 and 8. |
| STD-ACCESSIBILITY | Yes | Rule 5 (fog contrast). |
| STD-SKINS | Yes | Rule 6 (fog legible on every skin). |
| STD-PERFORMANCE | Yes | The fog layer within the fps budget. |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-SECURITY | Yes | Rule 7 (via the gateway). |
| STD-OBSERVABILITY | No |  |

### 9. Definition of Done

- [ ] Criteria pass.
- [ ] FEAT-LOCAL-ERASURE ships no later than this feature.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).

*Revision 1.0.1 (2026-11-02): app renamed from Etak to Kairos per ADR-0010 (identity text only, non-semantic).*
