---
id: DOM-KERNEL
title: "Domain: Shared Kernel"
version: 0.1.0
status: Draft
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

**Tier:** Thin. It is the shared kernel declared in ARCH-SYSTEM §1 (geo primitives, IDs, the event bus, design tokens). It has no business rules of its own.

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Draft**, under owner-proxy review (ADR-0001). It binds to
CONST-CORE and ARCH-SYSTEM (both Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

Geographic primitives (LatLng, distance), typed IDs, the in-process event bus and shared design-system components. It is kept deliberately tiny (ARCH-SYSTEM §1).

## 2. Ownership

### Owns

- LatLng, the distance function (great-circle), typed IDs
- The in-process event bus
- Design-system components and tokens (`core/designsystem`)

### Explicitly Does NOT Own

- Any business entity or rule
- Any storage

## 3. Invariants

N/A: a shared kernel with no business rules. Its one technical rule is that the distance function is the single implementation used by BC1 for R1 (consistency), and it is tested in STD-TESTING.

## 4. API Surface

- `Geo.distanceMeters(a, b)`, `LatLng`, typed ID value classes
- `EventBus.publish/subscribe` (in-process)

## 5. Events

### Publishes

- None.

### Consumes

- None.

## 6. Data Ownership

- None.

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
