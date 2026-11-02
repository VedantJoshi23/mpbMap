---
id: FEAT-DEMO-TRIP
title: "Etak — Feature: Demo Trip (Reviewer Showcase)"
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
Lifecycle: **Draft**, under owner-proxy review (ADR-0001).

**Context:**
- **Binds** (Frozen): CONST-CORE, ARCH-SYSTEM, the ADRs cited below, and
  DISC-HIDDEN-BUSINESS-RULES v1.1.0.
- **Informs** (Draft, marked as such per Oriveda ADR-0005): its owning
  `DOM-` spec and the `STD-` Standards cited. A disagreement with a Draft
  is logged as a `DIV-` entry.

**Tier:** **Medium** (it touches the High-tier DOM-TRIP sandbox rule).

### 1. Overview

A labelled 30–60 second scripted replay that shows an alarm firing on a synthetic route, for portfolio reviewers (KC-066). It works offline and with location denied.

### 2. Owning Domain

**Owning domain:** `DOM-TRIP`, exactly one.

**Other domains involved** (dependencies only). Each call below matches
the Allowed list of the domain that initiates it:

- None. It uses a synthetic fix source in place of DOM-LOCATION, inside DOM-TRIP.

### 3. Acceptance Criteria

- [ ] Starting from the first screen, a reviewer sees an alarm fire within 60 seconds.
- [ ] It uses no location, no foreground service and no mock provider, persists nothing, and emits no exploration events (J12).
- [ ] It is refused while a real trip runs.
- [ ] It is labelled 'Demo' throughout.

### 4. API Surface

- `DemoTripApi.play()`

### 5. Events

#### Publishes

- None to other contexts.

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
| STD-COMPLIANCE | Yes | Never used as FGS evidence. |
| STD-ACCESSIBILITY | Yes |  |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-PERFORMANCE | No |  |
| STD-SECURITY | No |  |
| STD-API | No |  |
| STD-DATABASE | No |  |
| STD-OBSERVABILITY | No |  |
| STD-SKINS | Yes | A bundled synthetic area. |

### 9. Definition of Done

- [ ] The sandbox test passes.
