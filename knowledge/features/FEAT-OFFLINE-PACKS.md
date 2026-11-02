---
id: FEAT-OFFLINE-PACKS
title: "Etak — Feature: Offline City Packs"
version: 0.1.0
status: Draft
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
  - ADR-0003
  - DOM-OFFLINE
required_by: []
related_documents: []
related_domains:
  - DOM-OFFLINE
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: Medium
---

## Feature: Offline City Packs

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Draft**, under owner-proxy review (ADR-0001).

**Context:**
- **Binds** (Frozen): CONST-CORE, ARCH-SYSTEM, the ADRs cited below, and
  DISC-HIDDEN-BUSINESS-RULES v1.1.0.
- **Informs** (Draft, marked as such per Oriveda ADR-0005): its owning
  `DOM-` spec and the `STD-` Standards cited. A disagreement with a Draft
  is logged as a `DIV-` entry.

**Tier:** **Medium.**

### 1. Overview

Download city-sized areas (tiles, elevation and place names) for offline use with every skin.

### 2. Owning Domain

**Owning domain:** `DOM-OFFLINE`, exactly one.

**Other domains involved** (dependencies only). Each call below matches
the Allowed list of the domain that initiates it:

- None initiated. Other domains consume its events.

### 3. Acceptance Criteria

- [ ] Framing an area shows a live size estimate, and the download is blocked above 50 MB / 3,000 tiles (R10).
- [ ] Downloads run under WorkManager and survive the app being backgrounded.
- [ ] Packs can be named, listed and deleted.
- [ ] Offline, maps, skins, offline search, alarms and parking work (Law 9).

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
- Storage full.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-PERFORMANCE | Yes |  |
| STD-COMPLIANCE | Yes | Provider limits, User-Agent. |
| STD-TESTING | Yes |  |
| STD-ACCESSIBILITY | Yes |  |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-DATABASE | Yes |  |
| STD-SECURITY | No |  |
| STD-API | No | Direct tile fetch, not the backend. |
| STD-OBSERVABILITY | No |  |
| STD-SKINS | Yes | Bundled glyphs. |

### 9. Definition of Done

- [ ] S4 confirms the pack and DEM budget.
- [ ] Acceptance criteria pass.
