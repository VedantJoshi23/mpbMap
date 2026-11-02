---
id: ARCH-SYSTEM
title: "Etak System Architecture"
version: 0.1.0
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
Lifecycle: **Draft**, under owner-proxy review (ADR-0001).

**Context loaded:**
- CONST-CORE v1.0.0 (**Frozen**; binds).
- DISC-DOMAIN-DISCOVERY v1.0.0, DISC-DATA-MODEL v1.1.0,
  DISC-TECHNICAL-ARCHITECTURE v1.1.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0
  and DISC-USER-JOURNEYS v1.0.0 (all **Frozen**).

This document formalises those specs. It cites them by ID and does not
restate them. Technology choices are not made here: they belong to the
project's `ADR-` series under `OV-004`.

**Architecture ambition:** Modular Monolith (CONST-CORE §4).

## 1. Service Boundaries

**Source: Adopted** (DISC-DOMAIN-DISCOVERY matches the Modular Monolith
ambition). One Android app with one module per context, plus one backend
service. This section satisfies Law 13 concretely.

| Context | Owns | Does NOT own |
| --- | --- | --- |
| BC1 Trip & Alarms (core) | Trip, Alarm, TripBootRecord; arming and pre-flight (R26); the arrival and loss logic (R1–R3, R17, R24); interrupted-trip detection (R25); the AlertPort contract and what each alert means | Platform location APIs (BC2); UI screens; notification rendering (adapters implement the AlertPort) |
| BC2 Location | Permission and disclosure state, ConsentRecord for location; acquiring fixes and fallback; the collection-scope gate (Law 3); the LocationFix log | Deciding when a trip alarm fires (BC1); explored cells (BC4) |
| BC3 Map & Skins (core) | Skin catalogue and bundles; rendering; 2D fallback; the attribution control; the no-boundary-lines style rule (Law 12) | Offline pack lifecycle (BC8); search results (BC6) |
| BC4 Exploration | ExploredCell, ExploredArea (`clearedAt`), Journal; the reveal toggles and the R18 gate; issuing the EraseLocalHistory command | Raw fixes (BC2); sync transport (BC9) |
| BC5 Codex | CodexEntry, including parking | Search (BC6); sync transport (BC9) |
| BC6 Places | Online search and reverse geocoding (through BC10); the offline pack search index | Provider HTTP and policy (BC10); pack files (BC8) |
| BC7 Routing | Route preview | Provider HTTP and policy (BC10) |
| BC8 Offline Maps | OfflinePack lifecycle; size and provider limits (R10); elevation tiles inside packs | Rendering (BC3) |
| BC9 Identity & Sync | Account, sign-in and sign-out, the sync protocol (LWW for the Codex, epoch-then-union for cells), account deletion (R9, R19), ConsentRecord for sign-in-sync | Content of the synced entities (BC4, BC5) |
| BC10 Provider Gateway (backend) | Proxying, rate limits, caching with coarsened keys, abuse protection, User-Agent; logs without coordinates (Law 5) | Any user-linked storage of query coordinates (forbidden, Law 5) |
| BC11 Platform Surfaces | *Reserved.* Wear OS, Android Auto, widgets | Anything in the MVP |

**Shared kernel**, kept deliberately tiny: geographic primitives (LatLng,
distance) and IDs. No context reaches into another context's storage.
Cross-context calls go through published interfaces or events (§3).

**Demo sandbox** (J12): BC1 runs against a synthetic fix source in place
of BC2. It starts no foreground service, persists nothing, and emits no
BC4 events (DISC-DOMAIN-DISCOVERY demo rule).

## 2. Domain Model

**Source: Adopted** (DISC-DATA-MODEL v1.1.0). The aggregate roots and
their contexts:

| Aggregate root | Context | Members / invariants (by reference) |
| --- | --- | --- |
| Trip | BC1 | Alarm 1..n; TripBootRecord (DP storage); R2, R17, R24, R25 |
| ExploredArea | BC4 | ExploredCell *; `clearedAt` epoch; merge is epoch-filter then union |
| Journal | BC4 | JournalEntry *; built from in-use location only (R18) |
| CodexEntry | BC5 | ParkingSpot is a Garage-category CodexEntry |
| OfflinePack | BC8 | Size and provider limits (R10) |
| Account | BC9 | Server-side; deletion and purge (R19) |
| ConsentRecord | BC2 / BC9 | Device-only; every consent kind (Law 12) |
| Skin | BC3 | Static bundle; 3D skins carry a fallback2DId (C1) |
| LocationFix | BC2 | 30-day rolling log, device only (Law 5) |

Storage classes, sync scope and retention are set by DISC-DATA-MODEL
v1.1.0 and Laws 5 and 6.

## 3. Event Flow

**Source: Adopted** (the DISC-DOMAIN-DISCOVERY event catalogue). This
section is optional for a Modular Monolith (`OV-003`). It is kept because
the boundaries depend on it.

- **Transport:** an in-process event bus inside the app. There is no
  message broker. The backend is called through HTTP only (BC9 sync, BC10
  gateway).
- **Catalogue:** DISC-DOMAIN-DISCOVERY's event table, which is
  authoritative.
- **Delivery rules** [S]:
  - Events in BC1's safety path are handled synchronously in process:
    LocationFixAcquired → BC1, LocationAccessChanged, TripInterrupted.
  - Everything else may be asynchronous.
  - No event in the safety path may depend on the network.

## 4. Folder Structure (design-level)

**Source: Designed**, matching §1 (DISC-REPO-STRUCTURE fixes only the top
level).

```text
android/
  app/                    composition root, navigation, DI wiring
  core/kernel/            shared kernel (geo primitives, ids)
  core/eventbus/          in-process event bus
  feature/trip/           BC1  domain/ (pure, no Android UI), alert-port/, platform/ (FGS, boot receiver, watchdog)
  feature/location/       BC2  domain/, platform/
  feature/map/            BC3  domain/, render/, skins-loader/
  feature/exploration/    BC4
  feature/codex/          BC5
  feature/places/         BC6
  feature/routing/        BC7
  feature/offline/        BC8
  feature/account/        BC9  (client side)
  ui/                     screens per context; depends on feature/* public APIs only
  demo/                   J12 sandbox (synthetic fix source)
backend/
  gateway/                BC10
  account-sync/           BC9  (server side)
skins/<codename>/         style, fonts, icons, textures (data)
```

**Dependency rule:**
- `feature/trip/domain` depends on nothing but `core/kernel`.
- Only `feature/location/platform` imports platform location APIs
  (Law 13).
- `ui/` never touches storage directly.

## 5. Scalability Strategy

**Source: Designed**, tied to KC-068 and KC-070.

- **Users:** sized for about 300+ downloads (the budget horizon). Device
  work scales with the device. Server load is sync plus gateway only.
- **Gateway:** aggregate rate limits per provider policy (R10); caching
  with coarsened keys; per-client limits. Growth past the free-provider
  fair use triggers TD1, TD2 and TD11 (each a rule-2 escalation).
- **Rendering smoothness (KC-070):** budgeted per skin. 3D skins fall back
  to 2D on weak devices (C1). Numeric targets come from M4 Standards.
- **Unknowns:** no availability target beyond best effort (KC-104).

## Constitution conformance

- §1 satisfies Laws 3, 5, 12 and 13 (boundary ownership).
- §2 and the data model satisfy Laws 5 and 6.
- §3's network-free safety path serves Laws 1 and 9.
- §4's dependency rule makes Law 13 checkable in CI.
- No section conflicts with a Law.

## Freeze plan (Oriveda ADR-0005)

- §1–§2 are **High tier**: frozen before any implementation.
- §3–§5 are **Medium**: frozen with this document, or before the first
  release they govern.
