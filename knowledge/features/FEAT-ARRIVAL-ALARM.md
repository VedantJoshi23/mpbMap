---
id: FEAT-ARRIVAL-ALARM
title: "Etak — Feature: Arrival Alarm"
version: 1.0.0
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
  - ADR-0006
  - DOM-TRIP
  - DOM-LOCATION
  - STD-TESTING
  - STD-ACCESSIBILITY
  - STD-COMPLIANCE
required_by: []
related_documents: []
related_domains:
  - DOM-TRIP
  - DOM-LOCATION
  - DOM-MAP
related_features: []
related_decisions: []
tags:
  - feature
risk: High
complexity: High
---

## Feature: Arrival Alarm

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-TRIP` v1.0.0 and `DOM-LOCATION` v1.0.0, `DOM-MAP` v1.0.0. STD-* v1.0.0 (all Frozen). The revisions it relied on (DOM-TRIP v1) are now applied and Frozen (KC-165).1.0 (snooze semantics, the safety debug log), which blocks this feature's freeze.

**Tier:** **High.** The headline utility (Laws 1, 2, 3, 14), and it relies on DOM-TRIP v1.1.0 additions. *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **1** (walking skeleton). In slice 1 the destination comes **from a map long-press only**. The search and Codex sources arrive with their features (slices 2–3). **Slice 1 persists no LocationFix and no safety-debug-log entries**: those start in slice 2 together with FEAT-LOCAL-ERASURE (Law 6, R21).

### 1. Overview

Lets a traveller pick a destination and arm one or more distance-based alarms that wake them, loudly and reliably, as they approach. It does not depend on the route, works offline, and survives restarts and service death with a loud alert (KC-079).

### 2. Owning Domain

**Owning domain:** `DOM-TRIP`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- DOM-TRIP → DOM-LOCATION (Allowed, `api/`): `LocationApi.fixes(trip scope)`, `access`; consumes LocationFixAcquired, LocationAccessChanged, DisclosureRecorded.
- `feature/trip/ui` → DOM-MAP (Allowed, ui only): consumes SkinChanged for quest-marker styling.
- **via app/** (composition root, no module dependency): the destination (kernel `LatLng` plus label) reaches `TripApi.arm` from a map long-press (slice 1), and later from a place card or Codex entry.
- **via app/**: the quest-marker overlay is passed into the DOM-MAP Composable.

### 3. Acceptance Criteria

- [ ] Setup requires a radius and a travel mode (R22). The radius is user-set, with an allowed range of **0.2–50 km**, so the owner's 10 km example (KC-079) is included. An editable suggestion scales with trip length.
- [ ] Several alarms per trip can be added, edited and removed before and during the trip. Pre-flight reruns on every edit.
- [ ] **Pre-flight (R26)** on every Start trip, each item **blocking** or **degrading with a warning**:
- [ ]   - location permission missing, or the disclosure not recorded: **block**;
- [ ]   - system location off: **block**;
- [ ]   - Play services missing or outdated without the LocationManager fallback: **block** (a trip never starts without fixes);
- [ ]   - approximate-only accuracy: **block** any radius below the reported accuracy (R26);
- [ ]   - notifications denied: **block**, because there is no reliable alert channel;
- [ ]   - alarm volume 0: **degrade with a warning**, because vibration and visual still fire (Law 14);
- [ ]   - full-screen intent unavailable: **degrade with a warning** to a heads-up notification with looping audio and vibration.
- [ ] Starting inside a radius shows the R4 dialog. 'Arm anyway' fires at the arrival distance (R17), which is shown.
- [ ] **Arrival:** the foreground service rings on USAGE_ALARM, **looping until dismissed**, with the Law 14 vibration **repeating until dismissal** and a full-screen or heads-up visual.
- [ ] **R3 (location lost near destination):** a distinct alert with the same looping audio, repeating vibration and visual. Never the arrival ring.
- [ ] **R25 (restart):** with a trip armed, a **FLAG_INSISTENT** 'Trip alarm interrupted' notification rings **before unlock**, looping, with repeating vibration and a visual, including under DND set to 'alarms allowed' and on the lock screen.
- [ ] **R25 (service death):** a stale heartbeat (DISC-DATA-MODEL v1.2.0 fail-safe reading) raises the same alert within the S3-measured bound.
- [ ] **Resume:** resuming after R25 always takes a user action in the foreground. There is no automatic restart (R5). TripResumed is published then.
- [ ] **Snooze** (DOM-TRIP Invariant 12): a fixed **2 minutes**, not user-extendable in the MVP. The same alert re-rings when it ends, regardless of distance. A snoozed alarm stays armed for R2, R24 and R25. A restart during snooze raises R25.
- [ ] Dismissing one alarm leaves the others armed. The session ends only after the last one is dismissed or the user ends the trip (R2).
- [ ] 3 hours without progress raises a notification prompt. It never auto-ends a trip with an armed alarm (R24).
- [ ] Arm, dismiss and snooze, **including from the lock screen**, work with TalkBack and 200% font (Law 14).
- [ ] The merged manifest contains no ACCESS_BACKGROUND_LOCATION (Law 3; CI check).

### 4. API Surface

- `TripApi.arm/end/editAlarms`, `state` (DOM-TRIP §4)
- `AlertPort` phone adapter: FGS audio loop, the FLAG_INSISTENT notification, repeating vibration

### 5. Events

#### Publishes

- TripStarted, TripEnded, TripResumed
- AlarmFired, AlarmDismissed, AlarmSnoozed, ArrivalReached
- LocationLostNearDestination, StaleTripDetected, TripInterrupted

#### Consumes

- LocationFixAcquired, LocationAccessChanged, DisclosureRecorded
- SkinChanged (ui only)

*(a subset of `DOM-TRIP`'s declared events)*

### 6. Data Changes

- `trip.db`: Trip, Alarm (`snoozeUntil`)
- DP Proto DataStore: TripBootRecord with heartbeat fields (DISC-DATA-MODEL v1.2.0)
- SafetyLogEntry (DOM-TRIP v1.1.0). It is **not persisted in slice 1**.

### 7. Edge Cases & Validations

- Nested radii: dismissing the outer alarm keeps the inner one armed.
- A tunnel near the radius: R3 path.
- Overnight OS update: R25 before unlock.
- Precise permission revoked mid-trip: R25.
- A restart during snooze: R25.
- Force-stop: a known limit (TD6). The next launch reports the interrupted trip.
- Starting a real trip during a demo ends the demo first (FEAT-DEMO-TRIP).

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-TESTING | Yes | Rules 1, 2(a)–(i), 3 and 8. |
| STD-PERFORMANCE | Yes | Fix→decision ≤1 s; decision→ring ≤2 s; battery ≤3% per hour; R25 is a measured bound. |
| STD-ACCESSIBILITY | Yes | Rules 1, 2, 3 (the quest marker is distinguished by shape and label) and 5. |
| STD-COMPLIANCE | Yes | Rule 1: FGS type location, a real-trip declaration video, full-screen intent, POST_NOTIFICATIONS. |
| STD-CODE | Yes | Rules 3 and 5. |
| STD-DATABASE | Yes | Rule 2 (DP); rule 1. |
| STD-OBSERVABILITY | Yes | Rule 5 (from slice 2). |
| STD-SKINS | No | The quest marker uses design-system colours, not skin colours. |
| STD-SECURITY | No | No network or auth on this path. |
| STD-API | No | No backend calls. |
| STD-CICD | Yes | Rule 1 gates, including the manifest permission check. |

### 9. Definition of Done

- [ ] All criteria pass, including the STD-TESTING rule 2 instrumented tests on the device matrix (rule 8).
- [ ] S3 is done and its thresholds recorded. **S3 and all code wait for the owner's M8 approval (ADR-0001 rule 6).**
- [ ] DOM-TRIP v1.1.0 is Frozen.
- [ ] The FGS declaration is ready, with a real-trip video.
- [ ] No open DIV- entries.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).
