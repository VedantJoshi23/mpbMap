---
id: DOM-ROUTING
title: "Domain: Routing"
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
related_domains: [DOM-KERNEL]
related_features: []
related_decisions: [ADR-0004]
tags:
  - domain
risk: Low
complexity: Low
---

# Domain: Routing (BC7)

**Tier:** Thin. A pure client of the gateway's routing endpoint. It holds no data and no rules of its own beyond those owned by DOM-GATEWAY (ARCH-SYSTEM §1: 'Route preview').

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-164), after owner-proxy
review (ADR-0001). It binds to CONST-CORE and ARCH-SYSTEM (both
Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

An online route preview (line, distance, ETA) for walk, cycle and drive.

## 2. Ownership

### Owns

- The route-preview client

### Explicitly Does NOT Own

- Provider policy (DOM-GATEWAY)
- Turn-by-turn navigation (out of the MVP, TD10)

## 3. Invariants

N/A: derived from DOM-GATEWAY's routing endpoint. The preview shows 'unavailable' offline or on failure (ADR-0004).

## 4. API Surface

- `RoutingApi.preview(from, to, mode)`

## 5. Events

### Publishes

- None.

### Consumes

- None.

## 6. Data Ownership

- None (in-memory only).

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
- `DOM-OFFLINE`
- `DOM-ACCOUNT`
- DOM-GATEWAY is reached only over HTTP.

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-ROUTING
paths:
  - "android/feature/routing/**"
allowed_dependencies:
  - "DOM-KERNEL"
forbidden_dependencies:
  - "DOM-TRIP"
  - "DOM-LOCATION"
  - "DOM-MAP"
  - "DOM-EXPLORATION"
  - "DOM-CODEX"
  - "DOM-PLACES"
  - "DOM-OFFLINE"
  - "DOM-ACCOUNT"
```

## 8. Edge Cases & Validations

N/A: derived. Failure means 'unavailable'.

## 9. Open Questions

- None.

## Architecture Review (2026-11-02)

Checked against ARCH-SYSTEM v1.1.0, CONST-CORE and the DISC-DOMAIN-DISCOVERY
event catalogue: §5 and §7 match the declared api-level dependencies, and
the boundary block matches the prose. Divergences: none. Result: passes.
