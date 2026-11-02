---
id: ADR-0005
title: "Location and Background Execution"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M3
category: Decision
priority: Critical
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
required_by: []
related_documents:
  - DISC-TECHNICAL-ARCHITECTURE
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - technology
risk: High
complexity: High
---

# ADR-0005: Location and Background Execution

Technology decision under Oriveda `OV-004`, with **Hybrid** stack freedom
(KC-151): options are compared, one is recommended, and the owner proxy
decides (ADR-0001). Constraints: CONST-CORE (Frozen), especially Law 7
(zero spend) and the KC-151 preferences. Lifecycle: **Frozen** v1.0.0 (2026-11-02), decided by the owner proxy
under KC-151 (EVD-045). Freezing does not authorise spikes or code
(KC-107).

## Context

The safety path for the headline utility. It must satisfy:
- Laws 1, 3 and 13;
- R2, R3, R25 and R26;
- the direct-boot constraint (ARCH-SYSTEM §1);
- no background location (Law 3);
- Play policy (R12).

Spike S3 validates it (DISC-TECHNICAL-ARCHITECTURE).

## Options Considered

**Location source**
- **Fused Location Provider (Play services).** GPS→network fallback
  (R3), battery-aware, free. A Play-services dependency (TD15).
- **Platform LocationManager.** No GMS dependency, but manual fusion and
  worse battery.

**Trip session:** a foreground service of type `location`, started from a
user action (KC-092), versus WorkManager (cannot hold continuous location)
versus background location (forbidden by Law 3).

**Watchdog**
- **AlarmManager inexact `setAndAllowWhileIdle`.** No exact-alarm
  permission; matches R25 and J3.
- **WorkManager periodic.** 15-minute minimum, which fails S3's 10-minute
  bound.
- **Exact alarms.** Restricted by Play.

**Boot:** a direct-boot-aware receiver for `LOCKED_BOOT_COMPLETED`.

## Decision

- **Location:** Fused Location Provider behind BC2. LocationManager is
  kept as a TD15 fallback adapter in BC2's platform layer.
- **Trip session:** a **foreground service** of type `location`.
- **Watchdog:** **AlarmManager `setAndAllowWhileIdle`**.
- **Boot:** a **direct-boot-aware receiver**.
- **Alerts:**
  - a high-importance notification channel on the alarm audio
    attributes, with `USE_FULL_SCREEN_INTENT` where granted;
  - a heads-up fallback;
  - a distinct vibration pattern per alert kind (Law 14).
- **Offline downloads:** WorkManager.
- Every mechanism is **confirmed by S3**. If S3 forces a change, R25 and
  J3 are revised as drafts first (precedent).

### Amendments from owner-proxy round 1

- **Insistent alerts** (Laws 1, 14):
  - The arrival ring and the R3 alert are played **by the running
    foreground service** on the alarm stream (`USAGE_ALARM`), **looping
    until the user dismisses them**.
  - Alerts raised with no service running (R25, from the boot receiver or
    the watchdog) use a notification with **`FLAG_INSISTENT`**, so the
    system loops the sound.
  - Both use the Law 14 vibration patterns.
  - S3 verifies both with DND set to "alarms allowed" and on the lock
    screen.
- **Watchdog mechanism (R25):**
  - the foreground service writes a **heartbeat timestamp** into the DP
    TripBootRecord;
  - the direct-boot-aware watchdog receiver **reschedules itself** on
    every firing;
  - if a trip is armed and the heartbeat is older than the threshold, it
    raises the R25 alert;
  - the foreground service cancels the watchdog when the trip ends.

  This requires a heartbeat field in TripBootRecord: a DISC-DATA-MODEL
  v1.2.0 revision, reviewed as a draft.
- **Missing or outdated Play services:** pre-flight (R26) switches to the
  LocationManager adapter, or blocks with an explanation. It never starts
  a trip that cannot get fixes.
- **Spike clause:** confirmed by S3. A failure changes R25 and J3 through
  draft revisions first.

## Consequences

- No background-location declaration is needed.
- One Play-declared foreground-service type.
- The full-screen-intent declaration needs justification (an alarm-type
  core function).
- Force-stop remains the known limit (TD6).

## Revisit Criteria

S3 results; Play policy changes to FGS types or full-screen intent; each
targetSdk bump (TD14).

## Architecture Review (2026-11-02)

Checked against CONST-CORE and ARCH-SYSTEM (both Frozen), with no conflict. Laws 1, 3, 13 and 14 are satisfied; ARCH-SYSTEM's direct-boot constraint is concretised.
Divergences: none (no code). Worked example under Oriveda `examples/`: not
applicable (Oriveda ADR-0002). Result: passes.
