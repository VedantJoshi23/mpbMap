---
id: FEAT-EXPLORE-REVEAL
title: "Etak — Feature: Explore-to-Reveal, Journal & Local Erasure"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M6
category: Features
priority: High
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - ADR-0006
  - DOM-EXPLORATION
required_by: []
related_documents: []
related_domains:
  - DOM-EXPLORATION
  - DOM-LOCATION
  - DOM-TRIP
  - DOM-ACCOUNT
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: Medium
---

## Feature: Explore-to-Reveal, Journal & Local Erasure

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Draft**, under owner-proxy review (ADR-0001).

**Context:**
- **Binds** (Frozen): CONST-CORE, ARCH-SYSTEM, the ADRs cited below, and
  DISC-HIDDEN-BUSINESS-RULES v1.1.0.
- **Informs** (Draft, marked as such per Oriveda ADR-0005): its owning
  `DOM-` spec and the `STD-` Standards cited. A disagreement with a Draft
  is logged as a `DIV-` entry.

**Tier:** **Medium**, raised to High for its erasure part (Law 6), which must be frozen before slice 3.

### 1. Overview

An opt-in fog of war over every skin that clears as the user explores, a journal of visited places, and complete local erasure.

### 2. Owning Domain

**Owning domain:** `DOM-EXPLORATION`, exactly one.

**Other domains involved** (dependencies only). Each call below matches
the Allowed list of the domain that initiates it:

- DOM-EXPLORATION → DOM-LOCATION (Allowed): consumes fixes; issues EraseLocalHistory; records the R18 consent.
- DOM-EXPLORATION → DOM-TRIP (Allowed): consumes TripStarted, TripEnded and TripResumed for R18 gating.
- DOM-EXPLORATION consumes RemoteChangesApplied from DOM-ACCOUNT (Allowed).

### 3. Acceptance Criteria

- [ ] Reveal is off by default. When off, the whole map is visible (R15).
- [ ] With reveal on, H3 resolution-9 cells clear while the app is in use.
- [ ] Trip-time reveal happens only with the double opt-in (R18), and never adds journal entries.
- [ ] The journal lists visited places and city counts.
- [ ] 'Clear history' deletes cells, the journal and the raw location log, and resumes after a crash. It shows complete only when every context has confirmed (Law 6). Signed-in users are warned that clearing affects all their devices (J11).

### 4. API Surface

- `ExplorationApi` (DOM-EXPLORATION §4)

### 5. Events

#### Publishes

- ExploredCellsAdded
- ExplorationCleared

#### Consumes

- LocationFixAcquired
- TripStarted, TripEnded, TripResumed
- RemoteChangesApplied

*(a subset of `DOM-EXPLORATION`'s declared events)*

### 6. Data Changes

- `exploration.db`: ExploredCell, ExploredArea, JournalEntry

### 7. Edge Cases & Validations

- An old offline device syncs pre-clear cells: they are dropped.
- Unknown city offline: filled in later.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-TESTING | Yes | Rule 4 erasure; rule 6 golden IDs. |
| STD-DATABASE | Yes | Rule 7. |
| STD-COMPLIANCE | Yes | Disclosure lists the exploration map. |
| STD-ACCESSIBILITY | Yes | Fog contrast; labels stay legible. |
| STD-PERFORMANCE | Yes | The fog layer within the fps budget. |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-SECURITY | No |  |
| STD-API | No | Sync is FEAT-ACCOUNT-SYNC's. |
| STD-OBSERVABILITY | Yes | The debug log is cleared on erasure. |
| STD-SKINS | No |  |

### 9. Definition of Done

- [ ] Acceptance criteria pass.
- [ ] The erasure resume test passes.
