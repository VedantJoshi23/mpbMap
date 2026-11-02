---
id: FEAT-PLACE-SEARCH
title: "Etak — Feature: Place Search (Online + Offline)"
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
  - ADR-0004
  - DOM-PLACES
required_by: []
related_documents: []
related_domains:
  - DOM-PLACES
  - DOM-MAP
  - DOM-CODEX
  - DOM-OFFLINE
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: Medium
---

## Feature: Place Search (Online + Offline)

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

Find destinations with typeahead online through the gateway, or from the downloaded packs and the Codex when offline.

### 2. Owning Domain

**Owning domain:** `DOM-PLACES`, exactly one.

**Other domains involved** (dependencies only). Each call below matches
the Allowed list of the domain that initiates it:

- DOM-PLACES consumes SkinChanged (DOM-MAP), CodexEntryChanged (DOM-CODEX) and the OfflinePack events (DOM-OFFLINE), all Allowed.
- DOM-PLACES → the gateway over HTTP only (POST bodies).

### 3. Acceptance Criteria

- [ ] Typeahead is debounced (≥300 ms, ≥3 characters), with a coarsened bias, sent in POST bodies.
- [ ] Results appear in the skin's typography.
- [ ] Offline or throttled: results come from the pack index and the Codex.
- [ ] Choosing a result opens a place card with 'Wake me', 'Mark as…' and 'Route'.

### 4. API Surface

- `PlacesApi.search`, `reverse`; gateway `/v1/search`, `/v1/reverse`

### 5. Events

#### Publishes

- None.

#### Consumes

- SkinChanged, CodexEntryChanged, OfflinePackInstalled, OfflinePackRemoved

*(a subset of `DOM-PLACES`'s declared events)*

### 6. Data Changes

- `places.db`: offline index

### 7. Edge Cases & Validations

- A 429 from the gateway: degrade to offline results.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-API | Yes | POST bodies. |
| STD-PERFORMANCE | Yes | Typeahead p95 ≤800 ms. |
| STD-ACCESSIBILITY | Yes |  |
| STD-TESTING | Yes |  |
| STD-SECURITY | Yes | Rule 7. |
| STD-COMPLIANCE | Yes | Attribution. |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-DATABASE | Yes | Rule 1. |
| STD-OBSERVABILITY | Yes | Provider error metrics. |
| STD-SKINS | No |  |

### 9. Definition of Done

- [ ] Acceptance criteria pass.
- [ ] The gateway search endpoints are deployed.
