---
id: DOM-PLATFORM-SURFACES
title: "Domain: Platform Surfaces (reserved)"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M5
category: Domains
priority: Low
depends_on:
  - ARCH-SYSTEM
  - CONST-CORE
required_by: []
related_documents:
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
related_domains: [DOM-KERNEL, DOM-TRIP, DOM-MAP]
related_features: []
related_decisions: []
tags:
  - domain
risk: Low
complexity: Low
---

# Domain: Platform Surfaces (reserved) (BC11)

**Tier:** Thin. Reserved by ARCH-SYSTEM §1 for Wear OS, Android Auto and widgets. Nothing is in the MVP, and it holds no data or rules of its own.

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-164), after owner-proxy
review (ADR-0001). It binds to CONST-CORE and ARCH-SYSTEM (both
Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

A reserved extension point (KC-060). Future modules implement DOM-TRIP's AlertPort and consume DOM-MAP for themed surfaces.

## 2. Ownership

### Owns

- Nothing in the MVP

### Explicitly Does NOT Own

- Alarm logic (DOM-TRIP)
- Rendering rules (DOM-MAP)

## 3. Invariants

N/A: reserved. When activated, it inherits DOM-TRIP's alert invariants through the AlertPort contract.

## 4. API Surface

- None in the MVP.

## 5. Events

### Publishes

- None.

### Consumes

- None in the MVP.

## 6. Data Ownership

- None.

## 7. Dependencies

### Allowed

- `DOM-KERNEL`

### Forbidden

- `DOM-TRIP`, `DOM-MAP` (until activation)
- `DOM-LOCATION`
- `DOM-ACCOUNT`
- `DOM-GATEWAY`
- Activating BC11 requires an ARCH-SYSTEM revision that adds the module and its relationships. It will then implement DOM-TRIP's AlertPort (Law 13) and consume DOM-MAP. The path below is **provisional**

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-PLATFORM-SURFACES
paths:
  - "android/surfaces/**"
allowed_dependencies:
  - "DOM-KERNEL"
forbidden_dependencies:
  - "DOM-TRIP"
  - "DOM-MAP"
  - "DOM-LOCATION"
  - "DOM-ACCOUNT"
  - "DOM-GATEWAY"
```

## 8. Edge Cases & Validations

N/A: reserved.

## 9. Open Questions

- Activation needs its own Feature specs and an ARCH-SYSTEM revision adding the module.

## Architecture Review (2026-11-02)

Checked against ARCH-SYSTEM v1.1.0, CONST-CORE and the DISC-DOMAIN-DISCOVERY
event catalogue: §5 and §7 match the declared api-level dependencies, and
the boundary block matches the prose. Divergences: none. Result: passes.
