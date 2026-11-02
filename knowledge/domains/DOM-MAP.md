---
id: DOM-MAP
title: "Domain: Map & Skins"
version: 1.0.0
status: Frozen
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
related_domains: [DOM-KERNEL, DOM-OFFLINE]
related_features: []
related_decisions: [ADR-0003]
tags:
  - domain
risk: Medium
complexity: High
---

# Domain: Map & Skins (BC3)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-164), after owner-proxy
review (ADR-0001). It binds to CONST-CORE and ARCH-SYSTEM (both
Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

Renders the real-world map in game-inspired skins. It owns the skin catalogue, live switching, 2D fallbacks, attribution and the no-boundary-lines rule.

## 2. Ownership

### Owns

- The skin catalogue and bundles (`skins/`), live switching
- MapLibre rendering, the camera, the 2D fallback for 3D skins
- The attribution control (R11)
- The runtime boundary-layer guard (Law 12, ADR-0003)

### Explicitly Does NOT Own

- Offline pack lifecycle (DOM-OFFLINE)
- Search results (DOM-PLACES)
- Trip state (DOM-TRIP)

## 3. Invariants

### Invariant 1

> No layer with `source-layer` `boundary` is ever rendered. Styles are bundled and linted, and the runtime guard strips such layers.

**Source:** Law 12; ADR-0003

### Invariant 2

> Attribution is visible on every map view.

**Source:** R11; Law 10

### Invariant 3

> Every 3D skin has a 2D fallback, used automatically below the STD-PERFORMANCE thresholds.

**Source:** C1; KC-070

### Invariant 4

> A skin switch preserves camera, armed alarms and route preview.

**Source:** C1; J2

### Invariant 5

> Remote config may change only source endpoints, never styles.

**Source:** ADR-0003; ADR-0009

### Invariant 6

> All skin assets are original or openly licensed.

**Source:** Law 11

## 4. API Surface

- `MapApi.currentSkin`, `setSkin(id)`, `skins()`
- A map Composable that accepts overlays (the quest marker, the Codex, fog)

## 5. Events

### Publishes

- SkinChanged

### Consumes

- OfflinePackInstalled / OfflinePackRemoved (DOM-OFFLINE)
- LocationFixAcquired (DOM-LOCATION), for locate-me

## 6. Data Ownership

- Bundled skin assets; Settings field `defaultSkin`

## 7. Dependencies

### Allowed

- `DOM-KERNEL`
- `DOM-OFFLINE`
- `DOM-LOCATION` (`api/` only, for locate-me)

### Forbidden

- `DOM-TRIP`
- `DOM-EXPLORATION`
- `DOM-CODEX`
- `DOM-PLACES`
- `DOM-ROUTING`
- `DOM-ACCOUNT`
- `DOM-GATEWAY`
- Overlays from other domains are passed in by `app/` composition; DOM-MAP never imports them.

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-MAP
paths:
  - "android/feature/map/**"
  - "skins/**"
allowed_dependencies:
  - "DOM-KERNEL"
  - "DOM-OFFLINE"
  - "DOM-LOCATION"
forbidden_dependencies:
  - "DOM-TRIP"
  - "DOM-EXPLORATION"
  - "DOM-CODEX"
  - "DOM-PLACES"
  - "DOM-ROUTING"
  - "DOM-ACCOUNT"
  - "DOM-GATEWAY"
```

## 8. Edge Cases & Validations

- A low-end device: the 3D skin falls back to 2D.
- Offline with no pack: show the cached area and a clear 'offline' state.
- A remote source endpoint is unreachable: fall back to the built-in default.
- Reduced motion: no animated transitions.

## 9. Open Questions

- S1 picks Vulkan or GL ES and the final 3D roster.

## Architecture Review (2026-11-02)

Checked against ARCH-SYSTEM v1.1.0, CONST-CORE and the DISC-DOMAIN-DISCOVERY
event catalogue: §5 and §7 match the declared api-level dependencies, and
the boundary block matches the prose. Divergences: none. Result: passes.

Spike clause: open questions are settled by S1. A failure revises this spec through a reviewed draft.
