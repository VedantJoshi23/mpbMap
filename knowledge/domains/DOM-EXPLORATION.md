---
id: DOM-EXPLORATION
title: "Domain: Exploration"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M5
category: Domains
priority: High
depends_on:
  - ARCH-SYSTEM
  - CONST-CORE
required_by: []
related_documents:
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
related_domains: [DOM-KERNEL, DOM-LOCATION, DOM-TRIP, DOM-ACCOUNT]
related_features: []
related_decisions: [ADR-0006]
tags:
  - domain
risk: Medium
complexity: Medium
---

# Domain: Exploration (BC4)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Draft**, under owner-proxy review (ADR-0001). It binds to
CONST-CORE and ARCH-SYSTEM (both Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

Explore-to-reveal (fog of war) and the journal: H3 cells revealed from in-use location (and, under R18's double opt-in, trip location), and local erasure.

## 2. Ownership

### Owns

- ExploredCell, ExploredArea (`clearedAt`), Journal
- The reveal toggles and the R18 gate (consent recorded through DOM-LOCATION)
- Issuing EraseLocalHistory, and its local part

### Explicitly Does NOT Own

- Raw fixes and ConsentRecord (DOM-LOCATION)
- Sync transport (DOM-ACCOUNT)

## 3. Invariants

### Invariant 1

> Reveal is off by default. When off, the whole map is visible.

**Source:** R15; KC-072

### Invariant 2

> Trip-session fixes add cells only when reveal is on, the disclosure lists the exploration map, and 'Reveal map during trips' is on. They never add journal entries.

**Source:** R18

### Invariant 3

> Cell merge: take the max `clearedAt`, drop cells first seen before it, then union and keep the earliest firstSeenAt.

**Source:** DISC-DATA-MODEL

### Invariant 4

> EraseLocalHistory is persisted and idempotent, and shows as complete only when DOM-LOCATION has also confirmed.

**Source:** Law 6; ADR-0006

### Invariant 5

> Cells use H3 resolution 9 with identical IDs across implementations.

**Source:** DISC-DATA-MODEL; ADR-0006

## 4. API Surface

- `ExplorationApi.revealOn`, `cells(viewport)`, `journal()`, `eraseLocalHistory()`

## 5. Events

### Publishes

- ExploredCellsAdded
- ExplorationCleared

### Consumes

- LocationFixAcquired (DOM-LOCATION)
- TripStarted / TripEnded / TripResumed (DOM-TRIP), for R18 gating
- RemoteChangesApplied (DOM-ACCOUNT)

## 6. Data Ownership

- Room `exploration.db` (CE): ExploredCell, ExploredArea, JournalEntry

## 7. Dependencies

### Allowed

- `DOM-KERNEL`
- `DOM-LOCATION`
- `DOM-TRIP`
- `DOM-ACCOUNT`

### Forbidden

- `DOM-MAP`
- `DOM-CODEX`
- `DOM-PLACES`
- `DOM-ROUTING`
- `DOM-OFFLINE`
- `DOM-GATEWAY`
- The fog layer is handed to DOM-MAP by `app/` composition, never by importing DOM-MAP.

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-EXPLORATION
paths:
  - "android/feature/exploration/**"
allowed_dependencies:
  - "DOM-KERNEL"
  - "DOM-LOCATION"
  - "DOM-TRIP"
  - "DOM-ACCOUNT"
forbidden_dependencies:
  - "DOM-MAP"
  - "DOM-CODEX"
  - "DOM-PLACES"
  - "DOM-ROUTING"
  - "DOM-OFFLINE"
  - "DOM-GATEWAY"
```

## 8. Edge Cases & Validations

- Clearing on one signed-in device clears the server and other devices (J11 warning).
- An old offline device syncs pre-clear cells: they are dropped by epoch filtering.
- Journal city name unknown offline: filled in later (DISC-DATA-MODEL).

## 9. Open Questions

- None.
