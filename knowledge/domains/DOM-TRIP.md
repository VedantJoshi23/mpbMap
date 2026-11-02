---
id: DOM-TRIP
title: "Domain: Trip & Alarms"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M5
category: Domains
priority: Critical
depends_on:
  - ARCH-SYSTEM
  - CONST-CORE
required_by: []
related_documents:
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
related_domains: [DOM-KERNEL, DOM-LOCATION, DOM-MAP]
related_features: []
related_decisions: [ADR-0005, ADR-0006]
tags:
  - domain
risk: High
complexity: High
---

# Domain: Trip & Alarms (BC1)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Draft**, under owner-proxy review (ADR-0001). It binds to
CONST-CORE and ARCH-SYSTEM (both Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

The headline utility: user-started trips that hold one or more arrival alarms and raise loud alerts on arrival, location loss, staleness and interruption. It is UI-independent (Law 13) and owns the AlertPort contract.

## 2. Ownership

### Owns

- Trip, Alarm, TripBootRecord (DP)
- Arming, pre-flight (R26) and the arm-inside-radius dialog logic (R4)
- Arrival evaluation (R1, R17), location-loss estimation (R3), stale-trip detection (R24)
- Interrupted-trip detection: the direct-boot receiver plus the heartbeat watchdog (R25, ADR-0005)
- The AlertPort contract and alert semantics (insistent audio, non-audio channel)
- The trip foreground service (type `location`)
- The demo sandbox mode (J12)

### Explicitly Does NOT Own

- Platform location APIs (DOM-LOCATION)
- ConsentRecord (DOM-LOCATION)
- Rendering the map or quest marker (DOM-MAP draws it from TRIP's public state)

## 3. Invariants

### Invariant 1

> An arrival alarm fires on straight-line distance to the destination and never depends on the route; a route-aware refinement may only make it fire earlier.

**Source:** Law 2; R1; KC-080

### Invariant 2

> One trip session holds every armed alarm. Dismissing one alarm disarms only that alarm. The session ends only when every alarm has fired and been dismissed, or the user ends the trip.

**Source:** Law 2; R2

### Invariant 3

> Arrival distance = min(the mode default: 500 m road/rail, 100 m walk/cycle; the smallest armed radius). Reaching it never ends a session with an armed alarm.

**Source:** R17, R22

### Invariant 4

> An alarm is never armed silently inside its radius. The R4 dialog is mandatory.

**Source:** R4

### Invariant 5

> Pre-flight (R26) runs on every trip start, and a trip never starts if fixes cannot be obtained (including missing Play services without the fallback).

**Source:** R26; ADR-0005

### Invariant 6

> Location loss raises the distinct 'location lost near destination' alert, never an arrival alarm.

**Source:** R3; Law 1

### Invariant 7

> Any detectable session death while an alarm is armed raises a loud 'Trip alarm interrupted' alert, including before unlock.

**Source:** R25; Law 1; ADR-0005

### Invariant 8

> Arrival and R3 alerts loop on the alarm stream until dismissed. R25 alerts use FLAG_INSISTENT. Every alert has a non-audio channel.

**Source:** ADR-0005; Law 14

### Invariant 9

> The boot path reads only TripBootRecord (DP) and touches no CE storage, bus or network.

**Source:** ARCH-SYSTEM §1 direct-boot constraint; Law 1

### Invariant 10

> Demo mode never starts the foreground service, persists nothing, and emits no exploration events.

**Source:** J12; DISC-DOMAIN-DISCOVERY demo rule

### Invariant 11

> Trip, Alarm and TripBootRecord never leave the device.

**Source:** Law 5; R8

## 4. API Surface

- `TripApi.arm(destination, mode, alarms)`, `end()`, `editAlarms()`, `state: StateFlow<TripState>`
- `AlertPort` (implemented by adapters: phone notification/FGS audio now, BC11 later)
- `DemoTripApi.play()`

## 5. Events

### Publishes

- TripStarted / TripEnded / TripResumed
- AlarmFired / AlarmDismissed / AlarmSnoozed
- ArrivalReached
- LocationLostNearDestination
- StaleTripDetected
- TripInterrupted

### Consumes

- LocationFixAcquired, LocationAccessChanged, DisclosureRecorded (DOM-LOCATION)
- SkinChanged (DOM-MAP), consumed by `feature/trip/ui` only, for quest-marker styling

## 6. Data Ownership

- Room `trip.db` (CE): Trip, Alarm
- Proto DataStore in DP storage: TripBootRecord {tripId, armed, destinationLabel, alarmStates, heartbeatAt}

## 7. Dependencies

### Allowed

- `DOM-KERNEL`
- `DOM-LOCATION`
- `DOM-MAP`

### Forbidden

- `DOM-EXPLORATION`
- `DOM-CODEX`
- `DOM-PLACES`
- `DOM-ROUTING`
- `DOM-OFFLINE`
- `DOM-ACCOUNT`
- `DOM-GATEWAY`
- DOM-MAP is allowed **only** from `feature/trip/ui` (SkinChanged). `feature/trip/domain` depends on DOM-KERNEL alone (ARCH-SYSTEM §4 rule 2).
- The boot path imports only its allowlist (ARCH-SYSTEM §4 rule 4).

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-TRIP
paths:
  - "android/feature/trip/**"
allowed_dependencies:
  - "DOM-KERNEL"
  - "DOM-LOCATION"
  - "DOM-MAP"
forbidden_dependencies:
  - "DOM-EXPLORATION"
  - "DOM-CODEX"
  - "DOM-PLACES"
  - "DOM-ROUTING"
  - "DOM-OFFLINE"
  - "DOM-ACCOUNT"
  - "DOM-GATEWAY"
```

## 8. Edge Cases & Validations

- Set an alarm while already inside the radius (R4 dialog).
- Multiple alarms with nested radii, then dismissing the outer one (R2).
- GPS lost in a tunnel near the radius: network fallback, then dead reckoning capped by mode, then the R3 alert.
- A phone restart overnight with the phone locked: the R25 alert before unlock.
- An OEM kill: the watchdog's heartbeat goes stale and raises R25 within the S3 bound. Force-stop is a known limit (TD6), and the next launch tells the user.
- Precise location revoked mid-trip: LocationAccessChanged, then R25.
- Stale trip: 3 h without progress raises a prompt that never auto-ends a trip with an armed alarm (R24).
- Alarm volume 0 or DND: pre-flight warns; S3 verifies 'alarms allowed'.
- Starting the demo while a real trip runs is refused.

## 9. Open Questions

- S3 thresholds (watchdog period, heartbeat staleness) are set by the spike (ADR-0005).
