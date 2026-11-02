---
id: DOM-LOCATION
title: "Domain: Location"
version: 0.2.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M5
category: Domains
priority: Critical
depends_on:
  - ARCH-SYSTEM
  - CONST-CORE
required_by: []
related_documents:
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
related_domains: [DOM-KERNEL, DOM-TRIP, DOM-EXPLORATION]
related_features: []
related_decisions: [ADR-0005, ADR-0006]
tags:
  - domain
risk: High
complexity: Medium
---

# Domain: Location (BC2)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Draft** v0.2.0, the revision after owner-proxy round 1
(ADR-0001); it waits for the ARCH-SYSTEM v1.1.0 draft. It binds to
CONST-CORE and ARCH-SYSTEM (both Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

The only gateway to device location (Law 13). It owns permission and disclosure state, every ConsentRecord, the collection-scope gate (Law 3), fix acquisition with fallback, and the 30-day LocationFix log.

## 2. Ownership

### Owns

- Permission state, the prominent-disclosure flow (R6)
- ConsentRecord, every kind, with withdrawal (Law 12)
- The collection-scope gate: in-use or an active trip session only (Law 3, R5)
- Fix acquisition: Fused, with a LocationManager fallback (ADR-0005)
- The LocationFix log (30 days) and executing EraseLocalHistory for it

### Explicitly Does NOT Own

- Deciding when an alarm fires (DOM-TRIP)
- Explored cells and the journal (DOM-EXPLORATION)

## 3. Invariants

### Invariant 1

> No location is collected unless the app is in use or a user-started trip session is running. Never ACCESS_BACKGROUND_LOCATION.

**Source:** Law 3; R5

### Invariant 2

> The prominent disclosure is shown and recorded before any location permission request.

**Source:** R6; Law 12

### Invariant 3

> Every consent kind (location-disclosure, reveal-during-trips, sign-in-sync) is recorded with shownAt, choice and withdrawnAt.

**Source:** Law 12; DISC-DATA-MODEL v1.1.0

### Invariant 4

> LocationFix rows older than 30 days are deleted. LocationFix never leaves the device.

**Source:** Law 5; DISC-DATA-MODEL

### Invariant 5

> Trip-session fixes reach DOM-EXPLORATION only under R18's double opt-in, and never reach the journal.

**Source:** R18

### Invariant 6

> EraseLocalHistory deletes every LocationFix row and confirms. The command is idempotent and retried on start.

**Source:** Law 6; ADR-0006

### Invariant 7

> Only this domain imports platform location APIs.

**Source:** Law 13

## 4. API Surface

- `LocationApi.fixes(scope)`, `access: StateFlow<LocationAccess>`
- `ConsentApi.record(kind, choice)`, `withdraw(kind)`, `current(kind)`
- `LocationApi.eraseFixes(): ErasureConfirmation`, called by DOM-EXPLORATION's persisted EraseLocalHistory command; it deletes every LocationFix and returns a confirmation

## 5. Events

### Publishes

- LocationFixAcquired
- LocationAccessChanged
- DisclosureRecorded

### Consumes

- TripStarted / TripEnded / TripResumed (DOM-TRIP), which open and close the trip scope
- ExplorationCleared (DOM-EXPLORATION), as an informational event; erasure itself arrives through `eraseFixes()`

## 6. Data Ownership

- Room `location.db` (CE): LocationFix, ConsentRecord

## 7. Dependencies

### Allowed

- `DOM-KERNEL`
- `DOM-TRIP`
- `DOM-EXPLORATION`

### Forbidden

- `DOM-MAP`
- `DOM-CODEX`
- `DOM-PLACES`
- `DOM-ROUTING`
- `DOM-OFFLINE`
- `DOM-ACCOUNT`
- `DOM-GATEWAY`
- DOM-TRIP and DOM-EXPLORATION are allowed only through their `api/` (event types).

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-LOCATION
paths:
  - "android/feature/location/**"
allowed_dependencies:
  - "DOM-KERNEL"
  - "DOM-TRIP"
  - "DOM-EXPLORATION"
forbidden_dependencies:
  - "DOM-MAP"
  - "DOM-CODEX"
  - "DOM-PLACES"
  - "DOM-ROUTING"
  - "DOM-OFFLINE"
  - "DOM-ACCOUNT"
  - "DOM-GATEWAY"
```

## 8. Edge Cases & Validations

- Approximate-only permission: report the accuracy and let DOM-TRIP block small radii.
- Play services missing: the LocationManager adapter, or report 'unavailable'.
- Consent withdrawn mid-trip: LocationAccessChanged, then the R25 path in DOM-TRIP.
- An erasure interrupted by a crash: retried on next start until confirmed.

## 9. Open Questions

- None.
