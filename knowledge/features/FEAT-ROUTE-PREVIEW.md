---
id: FEAT-ROUTE-PREVIEW
title: "Kairos — Feature: Route Preview"
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
  - DOM-ROUTING
  - FEAT-PROVIDER-GATEWAY
required_by: []
related_documents: []
related_domains:
  - DOM-ROUTING
  - DOM-MAP
  - DOM-LOCATION
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: Low
---

## Feature: Route Preview

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-ROUTING` v1.0.0 and `DOM-MAP` v1.0.0, `DOM-LOCATION` v1.0.0. STD-* v1.0.0 (all Frozen).

**Tier:** **Medium.** It was converted from a spec card, because it crosses domains through app/ and the gateway. *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **3.**

### 1. Overview

Shows line, distance and ETA for walk, cycle and drive between the user's position and a chosen place.

### 2. Owning Domain

**Owning domain:** `DOM-ROUTING`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- → DOM-GATEWAY over HTTP only.
- **via app/**: the 'from' point (from DOM-LOCATION's api); the line as a DOM-MAP overlay; 'Wake me' → TripApi.arm.

### 3. Acceptance Criteria

- [ ] Online: the route is drawn and distance and ETA are shown. Coordinates go in **POST bodies** (STD-API 4), as transient queries (Law 5).
- [ ] The route survives a skin switch (DOM-MAP Invariant 4).
- [ ] The route's mode becomes the default travel mode when arming (R22).
- [ ] The preview **never feeds the alarm trigger** (Law 2).
- [ ] Offline or on failure: 'unavailable', with 'Wake me' offered.

### 4. API Surface

- `RoutingApi.preview(from, to, mode)`

### 5. Events

#### Publishes

- None.

#### Consumes

- None.

*(a subset of `DOM-ROUTING`'s declared events)*

### 6. Data Changes

- None (in-memory).

### 7. Edge Cases & Validations

- A gateway 429 or 503: 'unavailable'.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-API | Yes | Rule 4. |
| STD-SECURITY | Yes | Rule 7. |
| STD-COMPLIANCE | Yes | Rules 3 and 6. |
| STD-ACCESSIBILITY | Yes | Rule 2. |
| STD-TESTING | Yes |  |
| STD-PERFORMANCE | No |  |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-DATABASE | No |  |
| STD-OBSERVABILITY | No |  |
| STD-SKINS | Yes | Rule 6 (route line contrast). |

### 9. Definition of Done

- [ ] Criteria pass.
- [ ] FEAT-PROVIDER-GATEWAY is deployed.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).

*Revision 1.0.1 (2026-11-02): app renamed from Etak to Kairos per ADR-0010 (identity text only, non-semantic).*
