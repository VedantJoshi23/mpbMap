---
id: DOM-KERNEL
title: "Domain: Shared Kernel"
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
related_domains: []
related_features: []
related_decisions: [ADR-0002]
tags:
  - domain
risk: Low
complexity: Low
---

# Domain: Shared Kernel (core/kernel, core/eventbus, core/designsystem)

**Tier:** Thin. DOM-KERNEL groups the `core/*` modules of ARCH-SYSTEM §4 (`kernel`, `eventbus`, `designsystem`, `config`) as the shared unit, per the ARCH-SYSTEM v1.1.0 draft. None of them holds business rules.

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-164), after owner-proxy
review (ADR-0001). It binds to CONST-CORE and ARCH-SYSTEM (both
Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

Geographic primitives (LatLng, distance), typed IDs, the in-process event bus and shared design-system components. It is kept deliberately tiny (ARCH-SYSTEM §1).

## 2. Ownership

### Owns

- LatLng, the distance function (great-circle), typed IDs
- The in-process event bus
- Design-system components and tokens (`core/designsystem`)
- The bootstrap-config client (`core/config`): fetch, the **exact-host allowlist**, last known good, and supplying source endpoints (ADR-0009, STD-SECURITY rule 4)

### Explicitly Does NOT Own

- Any business entity or rule
- Any business entity or user data

## 3. Invariants

N/A: a shared kernel with no business rules. Its one technical rule is that the distance function is the single implementation used by BC1 for R1 (consistency), and it is tested in STD-TESTING.

## 4. API Surface

- `Geo.distanceMeters(a, b)`, `LatLng`, typed ID value classes
- `EventBus.publish/subscribe` (in-process)
- `ConfigApi.apiBaseUrl`, `sources` (tiles, glyphs, DEM)

## 5. Events

### Publishes

- None.

### Consumes

- None.

## 6. Data Ownership

- The `core/config` last-known-good endpoint cache: CE DataStore, non-user, injected lazily (ARCH-SYSTEM v1.1.0)

## 7. Dependencies

### Allowed

- None.

### Forbidden

- Depending on any feature module (the kernel sits at the bottom of the graph).

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-KERNEL
kind: shared
paths:
  - "android/core/**"
```

## 8. Edge Cases & Validations

N/A: no business rules.

## 9. Open Questions

- None.

## Architecture Review (2026-11-02)

Checked against ARCH-SYSTEM v1.1.0, CONST-CORE and the DISC-DOMAIN-DISCOVERY
event catalogue: §5 and §7 match the declared api-level dependencies, and
the boundary block matches the prose. Divergences: none. Result: passes.
