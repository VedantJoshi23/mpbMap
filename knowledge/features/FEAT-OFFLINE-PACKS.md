---
id: FEAT-OFFLINE-PACKS
title: "Etak — Feature: Offline City Packs"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M6
category: Features
priority: Medium
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
  - ADR-0003
  - DOM-OFFLINE
required_by: []
related_documents: []
related_domains:
  - DOM-OFFLINE
  - DOM-KERNEL
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: Medium
---

## Feature: Offline City Packs

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-OFFLINE` v1.0.0 and `DOM-KERNEL` v1.0.0. STD-* v1.0.0 (all Frozen).

**Tier:** **Medium.** *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **3.**

### 1. Overview

Download city-sized areas (tiles, elevation and place names) for offline use with every skin.

### 2. Owning Domain

**Owning domain:** `DOM-OFFLINE`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- **via app/**: the map and the area picker are composed by app/, which passes a bbox to `OfflineApi`.
- DOM-OFFLINE → DOM-KERNEL `core/config`: the tile endpoint.

### 3. Acceptance Criteria

- [ ] A live size estimate is shown, and the download is blocked above 50 MB / 3,000 tiles (R10).
- [ ] **≤4 parallel requests**, with an identifying User-Agent (Invariant 1).
- [ ] Downloads run under WorkManager and **never use location** (Invariant 2).
- [ ] Packs can be named, listed and deleted.
- [ ] Offline, packs render with every skin and the pack index is searchable.

### 4. API Surface

- `OfflineApi`

### 5. Events

#### Publishes

- OfflinePackInstalled
- OfflinePackRemoved

#### Consumes

- None.

*(a subset of `DOM-OFFLINE`'s declared events)*

### 6. Data Changes

- MapLibre offline DB; `offline.db`

### 7. Edge Cases & Validations

- Interrupted download: resume or roll back.
- Storage full: the download stops with a message, partial data is removed, and existing packs are kept.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-COMPLIANCE | Yes | Rule 6 and provider limits. |
| STD-SECURITY | Yes | Rule 4 (the endpoint comes from config). |
| STD-PERFORMANCE | Yes |  |
| STD-TESTING | Yes |  |
| STD-ACCESSIBILITY | Yes | Rule 2. |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-DATABASE | Yes | Rule 1. |
| STD-SKINS | Yes | Rule 7 (bundled glyphs). |
| STD-API | No | Direct tile fetch. |
| STD-OBSERVABILITY | No |  |

### 9. Definition of Done

- [ ] S4 confirms the pack and DEM budget.
- [ ] A **system test**: with the network off, the alarm, parking and saved places work (Law 9).
- [ ] Criteria pass.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).
