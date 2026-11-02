---
id: ARCH-SYSTEM
title: "Etak System Architecture"
version: 0.2.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M3
category: Architecture
priority: Critical
depends_on:
  - CONST-CORE
  - DISC-DOMAIN-DISCOVERY
  - DISC-DATA-MODEL
  - DISC-TECHNICAL-ARCHITECTURE
required_by: []
related_documents:
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-USER-JOURNEYS
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - architecture
risk: High
complexity: High
---

# ARCH-SYSTEM: Etak System Architecture

Authored under Oriveda `OV-003` (Frozen) through `PRM-ARCHITECTURE`.
Lifecycle: **Draft** v0.2.0, the revision after owner-proxy round 1
(ADR-0001).

**Context loaded:**
- CONST-CORE v1.0.0 (**Frozen**; binds).
- DISC-DOMAIN-DISCOVERY v1.0.0, DISC-DATA-MODEL v1.1.0,
  DISC-TECHNICAL-ARCHITECTURE v1.1.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0
  and DISC-USER-JOURNEYS v1.0.0 (all **Frozen**).

This document formalises those specs by ID. Technology choices belong to
the project's `ADR-` series under `OV-004`.

**Architecture ambition:** Modular Monolith (CONST-CORE §4).

## 1. Service Boundaries

**Source: Adopted** (DISC-DOMAIN-DISCOVERY). One Android app with one
module per context, plus **one** backend service. This section satisfies
Law 13 concretely.

| Context | Owns | Does NOT own |
| --- | --- | --- |
| BC1 Trip & Alarms (core) | Trip, Alarm, TripBootRecord; arming and pre-flight (R26); the arrival and loss logic (R1–R3, R17, R24); interrupted-trip detection (R25), including the **direct-boot path**; the AlertPort contract and what each alert means | Platform location APIs (BC2); screens; notification rendering (adapters implement the AlertPort) |
| BC2 Location | Permission and disclosure state; **ConsentRecord, all kinds** (location-disclosure, reveal-during-trips, sign-in-sync), recorded and read through BC2's `api/`; acquiring fixes and fallback; the collection-scope gate (Law 3); the LocationFix log; **executing EraseLocalHistory for the LocationFix log** | Deciding when a trip alarm fires (BC1); explored cells (BC4) |
| BC3 Map & Skins (core) | Skin catalogue and bundles; rendering; 2D fallback; the attribution control; the no-boundary-lines style rule (Law 12) | Offline pack lifecycle (BC8); search results (BC6) |
| BC4 Exploration | ExploredCell, ExploredArea (`clearedAt`), Journal; the reveal toggles and the R18 gate (consent recorded through BC2); issuing EraseLocalHistory | Raw fixes (BC2); ConsentRecord (BC2); sync transport (BC9) |
| BC5 Codex | CodexEntry, including parking | Search (BC6); sync transport (BC9) |
| BC6 Places | Online search and reverse geocoding (through BC10); the offline pack search index | Provider HTTP and policy (BC10); pack files (BC8) |
| BC7 Routing | Route preview | Provider HTTP and policy (BC10) |
| BC8 Offline Maps | OfflinePack lifecycle; size and provider limits (R10); elevation tiles inside packs | Rendering (BC3) |
| BC9 Identity & Sync | Account, sign-in and sign-out, the sync protocol (LWW for the Codex, epoch-then-union for cells), account deletion (R9, R19). Sign-in-sync consent is recorded through BC2 | ConsentRecord (BC2); the content of synced entities (BC4, BC5) |
| BC10 Provider Gateway (backend) | Proxying, rate limits, caching with coarsened keys, abuse protection, User-Agent; logs without coordinates (Law 5) | Any user-linked storage of query coordinates (forbidden, Law 5) |
| BC11 Platform Surfaces | *Reserved.* Wear OS, Android Auto, widgets | Anything in the MVP |

**Shared kernel**, kept deliberately tiny: geographic primitives (LatLng,
distance) and IDs. No context reaches into another context's storage.

**Demo sandbox** (J12): BC1 runs against a synthetic fix source in place
of BC2. It starts no foreground service, persists nothing, and emits no
BC4 events.

**Direct-boot constraint** (Law 1, R25):
- The `LOCKED_BOOT_COMPLETED` receiver and its alert depend **only** on
  BC1's domain, the device-protected TripBootRecord store and an
  AlertPort adapter.
- Nothing on that path opens credential-encrypted storage, waits for the
  event bus or other subscribers, or uses the network.
- The composition root initialises credential-encrypted stores lazily, so
  a cold process during direct boot cannot crash before alerting.

## 2. Domain Model

**Source: Adopted** (DISC-DATA-MODEL v1.1.0). Each aggregate root has
exactly one owning context.

| Aggregate root | Context | Members / invariants (by reference) |
| --- | --- | --- |
| Trip | BC1 | Alarm 1..n; TripBootRecord (DP storage); R2, R17, R24, R25 |
| ExploredArea | BC4 | ExploredCell *; `clearedAt` epoch; merge is epoch-filter then union |
| Journal | BC4 | JournalEntry *; built from in-use location only (R18) |
| CodexEntry | BC5 | ParkingSpot is a Garage-category CodexEntry |
| OfflinePack | BC8 | Size and provider limits (R10) |
| Account | BC9 | Server-side; deletion and purge (R19) |
| ConsentRecord | **BC2** | Device-only; every consent kind, with withdrawal (Law 12) |
| Skin | BC3 | Static bundle; 3D skins carry a fallback2DId (C1) |
| LocationFix | BC2 | 30-day rolling log, device only (Law 5) |

## 3. Event Flow

**Source: Adopted** (the DISC-DOMAIN-DISCOVERY event catalogue, which is
authoritative).

- **Transport:** an in-process event bus inside the app. The backend is
  called through HTTP only.
- **Safety path:** detection and alerting for R3, R25 and arrival work in
  a fresh process with no subscribers, from the watchdog, the boot
  receiver or the foreground service. The bus only carries follow-up
  notifications to other contexts, and nothing on the safety path depends
  on the network (Laws 1, 9).
- **Erasure flow** (Law 6):
  - Local: BC4 issues EraseLocalHistory. BC4 deletes JournalEntry and
    ExploredCell and sets `clearedAt`. BC2 deletes LocationFix. The flow
    completes only when both confirm.
  - Account: BC9 deletes the server copies (Account, CodexEntry,
    ExploredCell, ExploredArea), purged within 30 days, plus the disclosed
    7-day backup window, so at most 37 days.

## 4. Folder Structure (design-level)

**Source: Designed**, matching §1.

```text
android/
  app/                    composition root and navigation only; lazy CE-store init
  core/kernel/            shared kernel (geo primitives, ids)
  core/eventbus/          in-process event bus
  core/designsystem/      shared UI components and theming tokens
  feature/<ctx>/api/      the context's public interfaces and events
  feature/<ctx>/domain/   pure logic
  feature/<ctx>/data/     the context's own storage
  feature/<ctx>/ui/       the context's screens
  feature/<ctx>/platform/ platform adapters (where needed)
    ctx ∈ {trip (BC1), location (BC2), map (BC3), exploration (BC4), codex (BC5),
           places (BC6), routing (BC7), offline (BC8), account (BC9)}
  demo/                   J12 sandbox (synthetic fix source)
backend/                  ONE deployable service (S5 RAM budget)
  gateway/                BC10 module
  account-sync/           BC9 server module
skins/<codename>/         original or openly licensed style, fonts, icons, textures
```

**Dependency rules**, checkable in CI:
1. `feature/*` depends only on `core/*` and other features' `api/`. It
   never depends on another feature's internals or on `app/`.
2. `feature/trip/domain` depends only on `core/kernel`.
3. Only `feature/location/platform` imports platform location APIs
   (Law 13).
4. The direct-boot path, `feature/trip/platform/boot`, imports only
   `feature/trip/domain`, the DP TripBootRecord store and its AlertPort
   adapter (Law 1).
5. There is no analytics, tracking or advertising module or dependency
   (Law 8).

## 5. Scalability Strategy

**Source: Designed**, tied to KC-068 and KC-070.

- **Users:** sized for about 300+ downloads. Server load is sync plus
  gateway only.
- **Gateway:** aggregate rate limits per provider policy (R10); caching
  with coarsened keys; per-client limits. Growth past fair use triggers
  TD1, TD2 and TD11 (each a rule-2 escalation).
- **Rendering smoothness (KC-070):** budgeted per skin, with a 2D
  fallback. Numbers come from M4 Standards.
- **Availability:** best effort (KC-104). The core works offline (Law 9).

## Constitution conformance

| Law | How this architecture satisfies it |
| --- | --- |
| L1 No silent failure | §1 direct-boot constraint; §3 safety path; §4 rule 4 |
| L2 Route-agnostic | §2 Trip aggregate (R1, R2, R17) |
| L3 No background location | §1 BC2's scope gate; §4 rule 3 |
| L4 Guest first | BC9 is optional; no other context depends on Account |
| L5 Device-only sensitive data | §2 storage; BC10's logging and caching rules |
| L6 Erasure | §3 erasure flow |
| L7 Zero spend | Not architectural; governed by the OV-004 ADRs (free tiers flagged) |
| L8 No tracking | §4 rule 5 |
| L9 Offline core | §3 network-free safety path; BC8 packs; BC6 offline index |
| L10 Compliance | BC10 enforces provider limits; Play obligations go to M4 and M6 |
| L11 Identity | `skins/` holds original or openly licensed assets only |
| L12 Consent and boundaries | BC2 owns ConsentRecord; BC3's no-boundary-lines rule |
| L13 Core boundaries | §1; §4 rules 1–3 |
| L14 The alarm is accessible | The AlertPort contract requires a non-audio channel (vibration and visual) that every adapter must provide |

## Freeze plan (Oriveda ADR-0005)

- §1–§2 are **High tier**: frozen before any implementation, which itself
  waits for the owner's explicit approval (KC-107).
- §3–§5 are **Medium**: frozen with this document.
- Worked example under Oriveda `examples/`: **not applicable**. It belongs
  to the framework's own Definition of Done (Oriveda ADR-0002), the same
  reasoning as DISC-RECOMMENDATIONS.
