---
id: FEAT-SKINNED-MAP
title: "Etak — Feature: Skinned Map, Locate-me & Skin Switching"
version: 0.2.0
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
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
  - ADR-0003
  - ADR-0009
  - DOM-MAP
  - STD-SKINS
  - STD-SECURITY
required_by: []
related_documents: []
related_domains:
  - DOM-MAP
  - DOM-OFFLINE
  - DOM-LOCATION
  - DOM-KERNEL
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: High
---

## Feature: Skinned Map, Locate-me & Skin Switching

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Draft** v0.2.0, the revision after owner-proxy round 1
(ADR-0001).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-MAP` v1.0.0 and `DOM-OFFLINE` v1.0.0, `DOM-LOCATION` v1.0.0, `DOM-KERNEL` v1.0.0. STD-* v1.0.0 (all Frozen).

**Tier:** **Medium.** It introduces `core/config` (bootstrap) under STD-SECURITY 4. *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **1:** one 2D skin (`terrain-sepia`), locate-me, `core/config`. **2:** the other three MVP skins (two 3D), S1, the 3D fallback.

### 1. Overview

Renders the real map in original game-inspired skins, switches between them live, shows the user's position, and loads source endpoints safely from the bootstrap file.

### 2. Owning Domain

**Owning domain:** `DOM-MAP`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- DOM-MAP consumes OfflinePackInstalled and OfflinePackRemoved (DOM-OFFLINE, Allowed).
- DOM-MAP → DOM-LOCATION (Allowed, `api/` only): consumes LocationFixAcquired for locate-me.
- DOM-MAP → DOM-KERNEL `core/config`: source endpoints.
- **via app/**: overlays are passed into the map Composable (quest marker, Codex, fog, route).

### 3. Acceptance Criteria

- [ ] The map opens straight away as a guest with the default skin (J1).
- [ ] **Locate-me** centres on the current fix. The first use triggers the disclosure (FEAT-LOCATION-CONSENT).
- [ ] **Bootstrap (`core/config`):**
- [ ]   - HTTPS only, and it carries no styles (DOM-MAP Invariant 5);
- [ ]   - an API host not on the bundled **exact-hostname** allowlist, including a different `*.duckdns.org` name, is ignored;
- [ ]   - when a host is rejected or unreachable, the app uses **last-known-good**, then the built-in default (STD-SECURITY 4).
- [ ] **No administrative boundary at any level, maritime included**, on any skin (Law 12, KC-149). Enforced by the CI lint and the runtime guard. The test fixture covers both a land and a maritime boundary layer.
- [ ] Attribution is visible on **every** map view, and the lint fails if it is hidden (DOM-MAP Invariant 2, STD-SKINS 4).
- [ ] The skin picker and swipe strip switch skins live, preserving camera, alarms and route (DOM-MAP Invariant 4).
- [ ] *(Slice 2)* 3D skins fall back to 2D below the low-end thresholds.
- [ ] Reduced motion and an animation scale of 0 disable transitions.

### 4. API Surface

- `MapApi.setSkin`, `skins`, the map Composable with overlays
- `ConfigApi` (kernel)

### 5. Events

#### Publishes

- SkinChanged

#### Consumes

- OfflinePackInstalled, OfflinePackRemoved
- LocationFixAcquired

*(a subset of `DOM-MAP`'s declared events)*

### 6. Data Changes

- Bundled `skins/`; Settings `defaultSkin`; the `core/config` last-known-good cache (CE)

### 7. Edge Cases & Validations

- Offline with no pack: the cached area plus an 'offline' state (DOM-MAP §8).
- An injected boundary layer is stripped (test).

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-SKINS | Yes | All rules. |
| STD-PERFORMANCE | Yes | fps, cold start, skin switch. |
| STD-ACCESSIBILITY | Yes | Rules 3–5. |
| STD-TESTING | Yes | Rule 6. |
| STD-COMPLIANCE | Yes | Rules 6–7. |
| STD-SECURITY | Yes | Rule 4 (bootstrap). |
| STD-CODE | Yes | Rule 1. |
| STD-CICD | Yes | Style lint. |
| STD-API | No |  |
| STD-DATABASE | No |  |
| STD-OBSERVABILITY | No |  |

### 9. Definition of Done

- [ ] Slice 1: criteria pass for one 2D skin plus bootstrap and locate-me.
- [ ] Slice 2: S1 is done and the 3D roster confirmed, with original art and LICENSES.md.
