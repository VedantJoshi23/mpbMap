---
id: ADR-0005
title: "Location and Background Execution"
version: 0.1.0
status: Draft
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
(zero spend) and the KC-151 preferences. Lifecycle: **Draft**.

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

## Consequences

- No background-location declaration is needed.
- One Play-declared foreground-service type.
- The full-screen-intent declaration needs justification (an alarm-type
  core function).
- Force-stop remains the known limit (TD6).

## Revisit Criteria

S3 results; Play policy changes to FGS types or full-screen intent; each
targetSdk bump (TD14).
