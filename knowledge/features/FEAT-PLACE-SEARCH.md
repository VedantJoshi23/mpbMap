---
id: FEAT-PLACE-SEARCH
title: "Kairos — Feature: Place Search (Online + Offline)"
version: 1.0.1
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
  - ADR-0004
  - DOM-PLACES
  - FEAT-PROVIDER-GATEWAY
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
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-PLACES` v1.0.0 and `DOM-MAP` v1.0.0, `DOM-CODEX` v1.0.0, `DOM-OFFLINE` v1.0.0. STD-* v1.0.0 (all Frozen).

**Tier:** **Medium.** *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **3**, after FEAT-PROVIDER-GATEWAY.

### 1. Overview

Find destinations with typeahead online through the gateway, or from downloaded packs and the Codex offline.

### 2. Owning Domain

**Owning domain:** `DOM-PLACES`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- DOM-PLACES consumes SkinChanged (DOM-MAP), CodexEntryChanged (DOM-CODEX) and the OfflinePack events (DOM-OFFLINE), all Allowed.
- DOM-PLACES → DOM-CODEX (Allowed): 'Mark as…'.
- **via app/**: 'Wake me' (→ TripApi.arm) and 'Route' (→ RoutingApi), passing a kernel LatLng plus label.
- **via app/**: the location bias (the viewport centre) is supplied by the caller and coarsened before it leaves the module.
- → DOM-GATEWAY over HTTP only.

### 3. Acceptance Criteria

- [ ] Typeahead is debounced (≥300 ms, ≥3 characters), with a coarsened bias, in POST bodies.
- [ ] Results use skin typography and show provider attribution (R11).
- [ ] Offline or throttled: results come from the pack index and the Codex.
- [ ] The place card offers 'Wake me', 'Mark as…' and 'Route' (handoffs via app/).

### 4. API Surface

- `PlacesApi.search`, `reverse`

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
| STD-API | Yes | Rule 4. |
| STD-SECURITY | Yes | Rules 4 and 7. |
| STD-COMPLIANCE | Yes | Rules 3 and 6. |
| STD-SKINS | Yes | Rule 6 (result typography). |
| STD-PERFORMANCE | Yes | Typeahead p95. |
| STD-ACCESSIBILITY | Yes | Rules 2 and 5. |
| STD-TESTING | Yes |  |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-DATABASE | Yes | Rule 1. |
| STD-OBSERVABILITY | No | Gateway-side. |

### 9. Definition of Done

- [ ] Criteria pass.
- [ ] FEAT-PROVIDER-GATEWAY is deployed.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).

*Revision 1.0.1 (2026-11-02): app renamed from Etak to Kairos per ADR-0010 (identity text only, non-semantic).*
