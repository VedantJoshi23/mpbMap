---
id: FEAT-ARRIVAL-ALARM
title: "Etak — Feature: Arrival Alarm"
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
  - ADR-0006
  - DOM-TRIP
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
Lifecycle: **Draft**, under owner-proxy review (ADR-0001).

**Context:**
- **Binds** (Frozen): CONST-CORE, ARCH-SYSTEM, the ADRs cited below, and
  DISC-HIDDEN-BUSINESS-RULES v1.1.0.
- **Informs** (Draft, marked as such per Oriveda ADR-0005): its owning
  `DOM-` spec and the `STD-` Standards cited. A disagreement with a Draft
  is logged as a `DIV-` entry.

**Tier:** **High.** The headline utility; Laws 1, 2, 3, 14. Frozen before its slice is implemented. **Walking-skeleton slice 1.**

### 1. Overview

Lets a traveller pick a destination and arm one or more distance-based alarms that wake them, loudly and reliably, as they approach. The alarm does not depend on the route, works offline, and survives restarts and service death with a loud alert. This is Etak's core utility (KC-079).

### 2. Owning Domain

**Owning domain:** `DOM-TRIP`, exactly one.

**Other domains involved** (dependencies only). Each call below matches
the Allowed list of the domain that initiates it:

- DOM-TRIP → DOM-LOCATION: `LocationApi.fixes(trip scope)`, `access`; consumes LocationFixAcquired, LocationAccessChanged, DisclosureRecorded (Allowed for DOM-TRIP).
- `feature/trip/ui` → DOM-MAP: consumes SkinChanged for quest-marker styling (Allowed, ui only).

### 3. Acceptance Criteria

- [ ] Choosing a destination (map long-press, search result or Codex place) and tapping 'Wake me' opens setup with a required radius, suggested from the trip length, and a travel mode (R22).
- [ ] Several alarms per trip can be added, edited and removed before and during the trip (J3 step 7).
- [ ] Pre-flight (R26) runs on every Start trip: location permission and precise accuracy, system location on, notifications, alarm volume above 0, full-screen-intent availability, Play services or the fallback. A failing item explains itself and blocks or degrades as specified.
- [ ] Starting inside a radius shows the R4 dialog. 'Arm anyway' fires at the arrival distance (R17), which is shown.
- [ ] On reaching a radius, the alarm rings on USAGE_ALARM, **looping until dismissed**, with the repeating Law 14 vibration and a full-screen or heads-up visual (ADR-0005).
- [ ] Dismissing one alarm leaves the others armed. The session ends only after the last one is dismissed or the user ends the trip (R2).
- [ ] Losing GPS falls back to network, then dead reckoning capped by mode. If the estimate crosses the radius, the distinct R3 alert sounds, never the arrival ring.
- [ ] A phone restart with the trip armed: a FLAG_INSISTENT 'Trip alarm interrupted' alert rings **before unlock** (R25).
- [ ] Service death (OEM kill): the watchdog detects a stale heartbeat and raises R25 within the S3-measured bound.
- [ ] 3 hours without progress: a notification prompt; never auto-ends with an alarm armed (R24).
- [ ] The whole flow, including dismissing from the lock screen, works with TalkBack and 200% font (Law 14).
- [ ] No ACCESS_BACKGROUND_LOCATION is requested (Law 3).

### 4. API Surface

- `TripApi.arm/end/editAlarms`, `state` (DOM-TRIP §4)
- `AlertPort`: the phone adapter (FGS audio loop plus the FLAG_INSISTENT notification)

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

- `trip.db`: Trip, Alarm (DOM-TRIP)
- DP Proto DataStore: TripBootRecord with heartbeat fields (DISC-DATA-MODEL v1.2.0)

### 7. Edge Cases & Validations

- Nested radii: dismissing the outer alarm keeps the inner one armed.
- A tunnel near the radius: R3 path.
- Overnight OS update: R25 before unlock.
- Precise permission revoked mid-trip: R25.
- DND on: rings under 'alarms allowed', and pre-flight warns otherwise.
- Force-stop: a known limit (TD6). The next launch reports the interrupted trip.
- Starting the demo during a real trip is refused.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-TESTING | Yes | Rules 1, 2(a)–(i) and 3 are mandatory. |
| STD-PERFORMANCE | Yes | Fix→decision ≤1 s; decision→ring ≤2 s; battery ≤3% per hour (far mode); the R25 bound is measured. |
| STD-ACCESSIBILITY | Yes | Rule 1 (lock-screen TalkBack, repeating vibration). |
| STD-COMPLIANCE | Yes | FGS type location; real-trip declaration video; full-screen intent; POST_NOTIFICATIONS. |
| STD-CODE | Yes | Direct-boot rule 3; no coordinates in messages. |
| STD-DATABASE | Yes | DP rule 2; Trip in CE. |
| STD-OBSERVABILITY | Yes | On-device safety debug log. |
| STD-SECURITY | No | No network or auth on this path. |
| STD-API | No | No backend calls. |
| STD-CICD | Yes | Gates apply. |
| STD-SKINS | No | The quest marker uses the design system. |

### 9. Definition of Done

- [ ] All acceptance criteria pass, with STD-TESTING rule 2 instrumented tests on the device matrix.
- [ ] Spike S3 is done and its thresholds recorded (heartbeat, watchdog period).
- [ ] The Play FGS declaration is ready, with a real-trip video.
- [ ] No open DIV- entries against the specs this feature relies on.
