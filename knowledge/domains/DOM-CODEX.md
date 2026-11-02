---
id: DOM-CODEX
title: "Domain: Codex"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M5
category: Domains
priority: Medium
depends_on:
  - ARCH-SYSTEM
  - CONST-CORE
required_by: []
related_documents:
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
related_domains: [DOM-KERNEL, DOM-ACCOUNT]
related_features: []
related_decisions: [ADR-0006]
tags:
  - domain
risk: Low
complexity: Low
---

# Domain: Codex (BC5)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-164), after owner-proxy
review (ADR-0001). It binds to CONST-CORE and ARCH-SYSTEM (both
Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

Saved places in gaming terminology (Waypoint … Point of Interest), including parking as Garage entries.

## 2. Ownership

### Owns

- CodexEntry, including ParkingSpot (category Garage, `active`)

### Explicitly Does NOT Own

- Search (DOM-PLACES)
- Sync transport (DOM-ACCOUNT)

## 3. Invariants

### Invariant 1

> The categories are the fixed C6 set. Users rename places, not categories.

**Source:** C6

### Invariant 2

> The newest parking spot is active. Several may exist.

**Source:** DISC-DATA-MODEL

### Invariant 3

> Deletions leave tombstones that expire after 90 days. Sync uses last-write-wins on updatedAt.

**Source:** DISC-DATA-MODEL

### Invariant 4

> Parking and saved places work offline. 'Return to vehicle' falls back to bearing plus distance, using the current location supplied to `feature/codex/ui` through a port whose adapter lives in `app/` and wraps DOM-LOCATION's `LocationApi` (ARCH-SYSTEM v1.1.0). DOM-CODEX does not depend on DOM-LOCATION.

**Source:** Law 9; J4

## 4. API Surface

- `CodexApi.add/update/delete`, `entries(category?)`, `activeParking()`

## 5. Events

### Publishes

- CodexEntryChanged

### Consumes

- RemoteChangesApplied (DOM-ACCOUNT)
- SignedIn / SignedOut / AccountDeleted (DOM-ACCOUNT), for merge or keep-or-clear

## 6. Data Ownership

- Room `codex.db` (CE): CodexEntry

## 7. Dependencies

### Allowed

- `DOM-KERNEL`
- `DOM-ACCOUNT`

### Forbidden

- `DOM-TRIP`
- `DOM-LOCATION`
- `DOM-MAP`
- `DOM-EXPLORATION`
- `DOM-PLACES`
- `DOM-ROUTING`
- `DOM-OFFLINE`
- `DOM-GATEWAY`

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-CODEX
paths:
  - "android/feature/codex/**"
allowed_dependencies:
  - "DOM-KERNEL"
  - "DOM-ACCOUNT"
forbidden_dependencies:
  - "DOM-TRIP"
  - "DOM-LOCATION"
  - "DOM-MAP"
  - "DOM-EXPLORATION"
  - "DOM-PLACES"
  - "DOM-ROUTING"
  - "DOM-OFFLINE"
  - "DOM-GATEWAY"
```

## 8. Edge Cases & Validations

- Concurrent edits on two devices: last-write-wins (TD5).
- A device offline for more than 90 days: full resync.

## 9. Open Questions

- None.

## Architecture Review (2026-11-02)

Checked against ARCH-SYSTEM v1.1.0, CONST-CORE and the DISC-DOMAIN-DISCOVERY
event catalogue: §5 and §7 match the declared api-level dependencies, and
the boundary block matches the prose. Divergences: none. Result: passes.
