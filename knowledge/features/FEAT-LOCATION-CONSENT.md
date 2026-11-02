---
id: FEAT-LOCATION-CONSENT
title: "Etak — Feature: Location Disclosure & Consent"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M6
category: Features
priority: Critical
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - ADR-0005
  - DOM-LOCATION
required_by: []
related_documents: []
related_domains:
  - DOM-LOCATION
  - DOM-TRIP
  - DOM-EXPLORATION
  - DOM-ACCOUNT
related_features: []
related_decisions: []
tags:
  - feature
risk: High
complexity: Medium
---

## Feature: Location Disclosure & Consent

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Draft**, under owner-proxy review (ADR-0001).

**Context:**
- **Binds** (Frozen): CONST-CORE, ARCH-SYSTEM, the ADRs cited below, and
  DISC-HIDDEN-BUSINESS-RULES v1.1.0.
- **Informs** (Draft, marked as such per Oriveda ADR-0005): its owning
  `DOM-` spec and the `STD-` Standards cited. A disagreement with a Draft
  is logged as a `DIV-` entry.

**Tier:** **High.** Laws 3 and 12; Play policy. **Walking-skeleton slice 1.**

### 1. Overview

The single flow through which Etak asks for location: a prominent disclosure naming every location-using feature, then the system permission, with every consent recorded and withdrawable.

### 2. Owning Domain

**Owning domain:** `DOM-LOCATION`, exactly one.

**Other domains involved** (dependencies only). Each call below matches
the Allowed list of the domain that initiates it:

- DOM-TRIP → DOM-LOCATION (Allowed): pre-flight reads `access` and DisclosureRecorded.
- DOM-EXPLORATION → DOM-LOCATION (Allowed): records the reveal-during-trips consent through `ConsentApi`.
- DOM-ACCOUNT → DOM-LOCATION (Allowed): records the sign-in-sync consent through `ConsentApi`.

### 3. Acceptance Criteria

- [ ] The first location-dependent action shows the disclosure, which uses the word 'location', names alarms, locate-me and the exploration map, and explains trip-time use (R6, R18).
- [ ] The disclosure appears before the system prompt every time it is needed. It is never buried in settings.
- [ ] Only foreground precise location is requested. Approximate-only is detected and reported.
- [ ] Every consent kind is recorded with shownAt, choice and withdrawnAt, and can be withdrawn in Settings (Law 12).
- [ ] Denial leaves the app usable for browsing, skins, search and the demo (J1).

### 4. API Surface

- `ConsentApi.record/withdraw/current`; `LocationApi.access`

### 5. Events

#### Publishes

- DisclosureRecorded
- LocationAccessChanged

#### Consumes

- None.

*(a subset of `DOM-LOCATION`'s declared events)*

### 6. Data Changes

- `location.db`: ConsentRecord (all kinds)

### 7. Edge Cases & Validations

- The user revokes permission in system settings: LocationAccessChanged, and any running trip goes to R25.
- Android auto-revokes permissions for an unused app: re-disclose at the next use.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-COMPLIANCE | Yes | Rules 1 and 2 (prominent disclosure). |
| STD-ACCESSIBILITY | Yes | TalkBack-readable disclosure. |
| STD-TESTING | Yes | Consent records and withdrawal. |
| STD-DATABASE | Yes | ConsentRecord is device-only. |
| STD-SECURITY | No | Local only. |
| STD-API | No |  |
| STD-PERFORMANCE | No |  |
| STD-SKINS | No |  |
| STD-CODE | Yes | Only BC2 imports location APIs. |
| STD-OBSERVABILITY | No |  |
| STD-CICD | Yes | Gates. |

### 9. Definition of Done

- [ ] Acceptance criteria pass.
- [ ] The disclosure text has been reviewed against the Play policy (KC-092).
- [ ] The privacy policy states the purposes (STD-COMPLIANCE rule 4).
