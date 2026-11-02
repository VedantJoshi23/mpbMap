---
id: FEAT-LOCATION-CONSENT
title: "Kairos — Feature: Location Disclosure, Consent & Collection Scope"
version: 1.0.1
status: Frozen
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
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
  - ADR-0005
  - DOM-LOCATION
  - STD-COMPLIANCE
  - STD-DATABASE
required_by: []
related_documents: []
related_domains:
  - DOM-LOCATION
  - DOM-TRIP
  - DOM-EXPLORATION
  - DOM-ACCOUNT
  - DOM-MAP
related_features: []
related_decisions: []
tags:
  - feature
risk: High
complexity: Medium
---

## Feature: Location Disclosure, Consent & Collection Scope

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-LOCATION` v1.0.0 and `DOM-TRIP` v1.0.0, `DOM-EXPLORATION` v1.0.0, `DOM-ACCOUNT` v1.0.0, `DOM-MAP` v1.0.0. STD-* v1.0.0 (all Frozen).

**Tier:** **High.** Laws 3, 5, 12; Play policy. It owns the collection-scope gate and the LocationFix log. *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **1** for the disclosure, consent and scope gate. The LocationFix log is **not persisted until slice 2** (with FEAT-LOCAL-ERASURE).

### 1. Overview

The single flow through which Kairos asks for location and the gate that limits collection: a prominent disclosure, the system permission, every consent recorded and withdrawable, fixes only in use or in an active trip, and a 30-day device-only fix log.

### 2. Owning Domain

**Owning domain:** `DOM-LOCATION`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- DOM-TRIP → DOM-LOCATION (Allowed): pre-flight reads `access` and DisclosureRecorded.
- DOM-MAP → DOM-LOCATION (Allowed, `api/`): locate-me triggers the disclosure.
- DOM-EXPLORATION → DOM-LOCATION (Allowed): records the reveal-during-trips consent.
- DOM-ACCOUNT → DOM-LOCATION (Allowed): records the sign-in-sync consent.
- **via app/**: the Codex current-location port triggers the disclosure on first use.

### 3. Acceptance Criteria

- [ ] The first location-dependent action (locate-me, 'Wake me', 'Park here', reveal) shows the disclosure, which uses the word 'location', names alarms, locate-me, parking and the exploration map, and explains trip-time use (R6, R18).
- [ ] No fix is acquired before the disclosure is recorded and permission granted (Law 3).
- [ ] **Scope gate:** no fix is acquired or delivered outside in-use or an active trip scope (DOM-LOCATION Invariant 1). Verified by test.
- [ ] Only foreground precise location is requested. Approximate-only is detected and reported, and trip pre-flight blocks small radii.
- [ ] The merged manifest contains no ACCESS_BACKGROUND_LOCATION (CI check).
- [ ] Every consent kind is recorded with shownAt, choice and withdrawnAt (Law 12).
- [ ] **Withdrawal effects:**
- [ ]   - location-disclosure: acquisition stops;
- [ ]   - reveal-during-trips: the R18 gate closes;
- [ ]   - sign-in-sync: sync stops and the user is signed out. This is not erasure (DOM-ACCOUNT Invariant 4).
- [ ] **LocationFix log** (from slice 2): rows older than 30 days are pruned in code (STD-DATABASE 4). The log never leaves the device (Invariant 4). `eraseFixes()` empties it and returns a confirmation.
- [ ] Denial leaves the app usable for browsing, skins, search and the demo (J1).

### 4. API Surface

- `ConsentApi.record/withdraw/current`; `LocationApi.access`, `fixes(scope)`, `eraseFixes()`

### 5. Events

#### Publishes

- DisclosureRecorded
- LocationAccessChanged
- LocationFixAcquired

#### Consumes

- TripStarted, TripEnded, TripResumed (scope)
- ExplorationCleared (informational)

*(a subset of `DOM-LOCATION`'s declared events)*

### 6. Data Changes

- `location.db`: ConsentRecord (all kinds); LocationFix (from slice 2)

### 7. Edge Cases & Validations

- 'Don't ask again' or permanently denied: a deep link to system Settings.
- Revoked in system settings: LocationAccessChanged, and any running trip goes to R25.
- Auto-revoked for an unused app: re-disclose at the next use.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-COMPLIANCE | Yes | Rules 1–3 and 5. |
| STD-ACCESSIBILITY | Yes | Rules 1–2 (the disclosure is TalkBack-readable). |
| STD-TESTING | Yes | Scope gate, consent records, withdrawal, prune. |
| STD-DATABASE | Yes | Rules 1 and 4. |
| STD-CODE | Yes | Rule 1 (only BC2 imports location APIs). |
| STD-CICD | Yes | The manifest permission check. |
| STD-SECURITY | No | Local only. |
| STD-API | No |  |
| STD-PERFORMANCE | No |  |
| STD-SKINS | No |  |
| STD-OBSERVABILITY | No |  |

### 9. Definition of Done

- [ ] Criteria pass.
- [ ] The disclosure text has been reviewed against KC-092.
- [ ] The privacy policy states the purposes (STD-COMPLIANCE 4).

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).

*Revision 1.0.1 (2026-11-02): app renamed from Etak to Kairos per ADR-0010 (identity text only, non-semantic).*
