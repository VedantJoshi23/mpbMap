---
id: FEAT-DEMO-TRIP
title: "Kairos — Feature: Demo Trip (Reviewer Showcase)"
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
  - DOM-TRIP
required_by: []
related_documents: []
related_domains:
  - DOM-TRIP
related_features: []
related_decisions: []
tags:
  - feature
risk: Low
complexity: Low
---

## Feature: Demo Trip (Reviewer Showcase)

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-TRIP` v1.0.0. STD-* v1.0.0 (all Frozen). The revisions it relied on (DOM-TRIP v1) are now applied and Frozen (KC-165).1.0 (the bus-silence wording).

**Tier:** **Medium.** *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **2** (the portfolio front door, KC-066).

### 1. Overview

A labelled 30–60 second scripted replay that shows an alarm firing on a synthetic route, for portfolio reviewers. It works offline and with location and notifications denied.

### 2. Owning Domain

**Owning domain:** `DOM-TRIP`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- None to other domains. A synthetic fix source replaces DOM-LOCATION inside DOM-TRIP.
- **via app/**: the demo map and route overlay over a **bundled offline synthetic area**.

### 3. Acceptance Criteria

- [ ] From the first screen, a reviewer sees an alarm fire within 60 seconds.
- [ ] It uses no location, no foreground service and no mock provider. It persists nothing.
- [ ] It **publishes no BC1 lifecycle or alarm events on the bus**, so DOM-LOCATION never opens trip scope (Law 3). Tested (STD-TESTING 3).
- [ ] The alert is an in-app full-screen visual with sound and vibration while in the foreground. It works even with notifications denied, is labelled 'Demo', and can be dismissed with TalkBack (Law 14).
- [ ] Starting a real trip ends the demo first.
- [ ] It is never used as FGS declaration evidence (STD-COMPLIANCE 1).

### 4. API Surface

- `DemoTripApi.play()`

### 5. Events

#### Publishes

- None on the bus.

#### Consumes

- None.

*(a subset of `DOM-TRIP`'s declared events)*

### 6. Data Changes

- None.

### 7. Edge Cases & Validations

- The app is killed mid-demo: no R25 alert, because nothing was persisted.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-TESTING | Yes | Rule 3. |
| STD-COMPLIANCE | Yes | Rule 1. |
| STD-ACCESSIBILITY | Yes | Rules 1–2. |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-SKINS | Yes | The bundled synthetic area. |
| STD-PERFORMANCE | No |  |
| STD-SECURITY | No |  |
| STD-API | No |  |
| STD-DATABASE | No |  |
| STD-OBSERVABILITY | No |  |

### 9. Definition of Done

- [ ] The sandbox test passes.
- [ ] DOM-TRIP v1.1.0 is Frozen.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).

*Revision 1.0.1 (2026-11-02): app renamed from Etak to Kairos per ADR-0010 (identity text only, non-semantic).*
