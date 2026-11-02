---
id: FEAT-SKINNED-MAP
title: "Etak — Feature: Skinned Map & Skin Switching"
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
  - ADR-0003
  - DOM-MAP
  - STD-SKINS
required_by: []
related_documents: []
related_domains:
  - DOM-MAP
  - DOM-OFFLINE
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: High
---

## Feature: Skinned Map & Skin Switching

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Draft**, under owner-proxy review (ADR-0001).

**Context:**
- **Binds** (Frozen): CONST-CORE, ARCH-SYSTEM, the ADRs cited below, and
  DISC-HIDDEN-BUSINESS-RULES v1.1.0.
- **Informs** (Draft, marked as such per Oriveda ADR-0005): its owning
  `DOM-` spec and the `STD-` Standards cited. A disagreement with a Draft
  is logged as a `DIV-` entry.

**Tier:** **Medium.** The concept surface; Law 12 is guarded by the lint and runtime guard. **Slice 1 ships one 2D skin (`terrain-sepia`). The remaining MVP skins come in slice 2.**

### 1. Overview

Renders the real map in original game-inspired skins and switches between them live. The MVP has four skins: `terrain-sepia`, `vertical-night` (3D), `cube-3d` (3D) and `tactical-grid` (C2), each with a 2D fallback where it is 3D.

### 2. Owning Domain

**Owning domain:** `DOM-MAP`, exactly one.

**Other domains involved** (dependencies only). Each call below matches
the Allowed list of the domain that initiates it:

- DOM-MAP consumes OfflinePackInstalled and OfflinePackRemoved from DOM-OFFLINE (Allowed).

### 3. Acceptance Criteria

- [ ] The map opens straight away as a guest with the default skin (J1).
- [ ] The skin picker and swipe strip switch skins live, preserving the camera, armed alarms and route (C1, J2).
- [ ] No boundary lines are drawn on any skin (lint plus the runtime guard; Law 12).
- [ ] Attribution is visible on the map (R11).
- [ ] 3D skins fall back to 2D below the low-end thresholds (STD-PERFORMANCE).
- [ ] Reduced motion and an animation scale of 0 disable transitions.

### 4. API Surface

- `MapApi.setSkin`, `skins`, the map Composable with overlays

### 5. Events

#### Publishes

- SkinChanged

#### Consumes

- OfflinePackInstalled, OfflinePackRemoved

*(a subset of `DOM-MAP`'s declared events)*

### 6. Data Changes

- Bundled `skins/`; Settings `defaultSkin`

### 7. Edge Cases & Validations

- An unreachable remote source endpoint: fall back to the built-in endpoint.
- A style injected with a boundary layer: stripped (test).

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-SKINS | Yes | All rules. |
| STD-PERFORMANCE | Yes | fps, cold start, skin switch. |
| STD-ACCESSIBILITY | Yes | Rules 3–5. |
| STD-TESTING | Yes | Rule 6 boundary guard and lint. |
| STD-COMPLIANCE | Yes | Rules 6 and 7. |
| STD-CODE | Yes |  |
| STD-CICD | Yes | Style lint. |
| STD-SECURITY | Yes | Bootstrap allowlist (rule 4). |
| STD-API | No |  |
| STD-DATABASE | No |  |
| STD-OBSERVABILITY | No |  |

### 9. Definition of Done

- [ ] S1 has been run on the reference devices, with the 3D roster confirmed.
- [ ] Skin art is original, with LICENSES.md present.
- [ ] Acceptance criteria pass.
