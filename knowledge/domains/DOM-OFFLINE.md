---
id: DOM-OFFLINE
title: "Domain: Offline Maps"
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
related_domains: [DOM-KERNEL]
related_features: []
related_decisions: [ADR-0003, ADR-0005]
tags:
  - domain
risk: Medium
complexity: Medium
---

# Domain: Offline Maps (BC8)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-164), after owner-proxy
review (ADR-0001). It binds to CONST-CORE and ARCH-SYSTEM (both
Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

City-sized offline packs (tiles, elevation and place names) within provider limits, downloaded in the background.

## 2. Ownership

### Owns

- The OfflinePack lifecycle (select area, estimate, download, manage, delete)
- Pack size and request limits (R10)
- Elevation tiles inside packs

### Explicitly Does NOT Own

- Rendering (DOM-MAP)
- The search index (DOM-PLACES)

## 3. Invariants

### Invariant 1

> A pack never exceeds 50 MB / 3,000 tiles, and at most 4 parallel requests are made with an identifying User-Agent, while OpenFreeMap is the source.

**Source:** R10; KC-087

### Invariant 2

> Downloads run as WorkManager jobs and never use location.

**Source:** ADR-0005

### Invariant 3

> Region-sized packs are out of the MVP.

**Source:** DISC-FEATURE-INVENTORY C9

## 4. API Surface

- `OfflineApi.estimate(bbox)`, `download(bbox, name)`, `packs()`, `delete(id)`

## 5. Events

### Publishes

- OfflinePackInstalled
- OfflinePackRemoved

### Consumes

- None.

## 6. Data Ownership

- MapLibre offline database; Room `offline.db` (CE): OfflinePack

## 7. Dependencies

### Allowed

- `DOM-KERNEL`

### Forbidden

- `DOM-TRIP`
- `DOM-LOCATION`
- `DOM-MAP`
- `DOM-EXPLORATION`
- `DOM-CODEX`
- `DOM-PLACES`
- `DOM-ROUTING`
- `DOM-ACCOUNT`

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-OFFLINE
paths:
  - "android/feature/offline/**"
allowed_dependencies:
  - "DOM-KERNEL"
forbidden_dependencies:
  - "DOM-TRIP"
  - "DOM-LOCATION"
  - "DOM-MAP"
  - "DOM-EXPLORATION"
  - "DOM-CODEX"
  - "DOM-PLACES"
  - "DOM-ROUTING"
  - "DOM-ACCOUNT"
```

## 8. Edge Cases & Validations

- The estimate exceeds the limit: blocked with guidance.
- A download is interrupted: resumed or rolled back cleanly.
- Storage full: fail with a message, keep existing packs.

## 9. Open Questions

- S4 sets the elevation-tile budget per pack.

## Architecture Review (2026-11-02)

Checked against ARCH-SYSTEM v1.1.0, CONST-CORE and the DISC-DOMAIN-DISCOVERY
event catalogue: §5 and §7 match the declared api-level dependencies, and
the boundary block matches the prose. Divergences: none. Result: passes.

Spike clause: open questions are settled by S4. A failure revises this spec through a reviewed draft.
