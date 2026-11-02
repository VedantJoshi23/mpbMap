---
id: DISC-DATA-MODEL
title: "Discovery: Data Model"
version: 1.2.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M1
category: Discovery
priority: High
depends_on:
  - DISC-FEATURE-INVENTORY
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-USER-JOURNEYS
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - discovery
  - data-model
risk: High
complexity: Medium
---

# DISC-DATA-MODEL: Data Model

One of the ten M1 investigations (`OV-001`, Frozen), over [the evidence
log](evidence/README.md). Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-125). Draft v0.1.0, then
owner-proxy rounds 1–2 (ADR-0001), then Architecture Review, then Freeze. Suggestions are marked **[S]**. Risk is
**High** because it fixes where location and account data live (Oriveda
ADR-0005).

The reference-app inferences KC-048 and KC-049 are superseded by KC-121,
which is this project's own model, derived from the Frozen capabilities
(C-n), rules (R-n) and journeys (J-n).

## Purpose

Name the core entities, their aggregates and relationships, and where
each one lives (device only, synced, or server), so that M3 storage and
M5 domains start from the same model.

## Observed Facts

- The explored-places record may live locally as GeoJSON or in a server
  database. The choice is open (KC-073).
- Guest data stays on the device. Sync covers saved places, parking and
  explored cells, with last-write-wins. Alarms, the journal and raw
  location logs never leave the device in the MVP (R7, R8).
- Account deletion removes server data, with a 30-day purge (R9, R19).
  Local erasure covers the journal, explored cells and the raw location
  log (R21).
- An interrupted trip must alert before the phone is unlocked, using trip
  state in device-protected storage (R25).
- Skins are bundles (C1). A PostgreSQL instance already runs on the VM
  (KC-085).

## Interpretation

### Entities

All entities are [S]. **Storage class** values:
- **DP**: device-protected storage, readable before unlock.
- **CE**: credential-encrypted storage, the normal app storage.
- **Srv**: the server.

| Entity | Key fields | Lives | Storage | Aggregate |
| --- | --- | --- | --- | --- |
| **Skin** | id, codename, version, styleRef, is3D, fallback2DId, fontSet, iconSet, accents | App bundle (static) | n/a | Skin |
| **Place** | providerId, name, lat/lon, category | Transient (search result, cached) | CE cache | none |
| **CodexEntry** | id, category (Waypoint…Point of Interest, Garage), name, lat/lon, note, createdAt, updatedAt, deleted | Device; synced when signed in | CE, Srv | CodexEntry |
| **ParkingSpot** | a CodexEntry with category Garage, plus parkedAt and an `active` flag | Device; synced | CE, Srv | CodexEntry |
| **Trip** | id, travelMode, destination (lat/lon, label), state, startedAt, endedAt, **arrivalDistanceM** (R17), **lastFixAt**, **lastKnownDistanceM** (R3), **lastProgressAt** (R24) | **Device only** | CE | Trip (root) |
| **Alarm** | id, tripId, radiusM, state (armed / fired / snoozed / dismissed), firedAt, **snoozeUntil** | **Device only** | CE | Trip |
| **TripBootRecord** | tripId, armed flag, destination **label only**, alarm states, **heartbeatAt** (UTC epoch ms), **heartbeatElapsedMs** (elapsedRealtime), **heartbeatBootCount** *(v1.2.0)* | **Device only** | **DP** (R25) | Trip |
| **LocationFix** | ts, lat/lon, accuracyM, source (gps / network), context (in-use / trip) | **Device only**, 30-day rolling log | CE | none |
| **ExploredCell** | cellId, firstSeenAt | Device; synced | CE, Srv | ExploredArea |
| **ExploredArea** | clearedAt epoch | Device; synced | CE, Srv | ExploredArea |
| **JournalEntry** | placeRef or cell summary, visitedAt, city | **Device only** | CE | Journal |
| **OfflinePack** | id, name, bbox, minZoom, maxZoom, sizeBytes, status | Device only | CE | OfflinePack |
| **ConsentRecord** | kind (location-disclosure, reveal-during-trips (R18), sign-in-sync), shownAt, choice, withdrawnAt, appVersion | **Device only** (v1.1.0) | CE | ConsentRecord |
| **Account** | id, authSubject, createdAt, deletionRequestedAt | Server | Srv | Account |
| **Settings** | units, defaultSkin, revealOn, revealDuringTrips, alarm defaults | Device only | CE | Settings |

- ParkingSpot is a CodexEntry with category Garage, so parking spots are
  visible in the Codex list for free. Several spots may be saved; the
  newest is `active`.
- **Heartbeat** *(v1.2.0, required by ADR-0005's watchdog)*:
  - **What it means:** liveness of the trip foreground service. The
    service writes it on its own timer, independent of whether fixes
    arrive. Fix loss is R3's job; the watchdog is R25's.
  - **Clock:** within one boot (same `heartbeatBootCount`), staleness is
    judged on the monotonic `heartbeatElapsedMs`. A different boot count
    means the boot-receiver path applies. If the boot count cannot be
    read, the record is treated as a different boot (boot path) or as
    STALE.
  - **Fail-safe reading:** an armed record whose heartbeat is missing,
    unparseable, older than the threshold, or (by wall clock) later than
    now plus tolerance counts as **STALE** and raises R25 ("an uncertain
    alert over silence").
  - **Thresholds and cadence:** set by spike S3, with the threshold at
    least twice the write interval.
  - **Storage:** the Proto DataStore field numbers are new and never
    reused. No coordinates and no location history, so the minimal-DP
    footprint holds. The writer is the trip FGS. The reader (the
    watchdog receiver) lives in `feature/trip/platform/boot`.
- Only **TripBootRecord** sits in DP storage. It holds the minimum R25
  needs to ring before unlock: no coordinates and no location history.
  The full Trip and Alarm stay in normal (CE) storage, because resuming
  happens after unlock, and a trip only starts after the user has
  unlocked the phone, so CE storage is writable during it.
- **Withdrawing sign-in-sync consent** stops sync and signs the user out. Server data is deleted only through the R9 account-deletion path, so withdrawal is not erasure under CONST-CORE Law 6. *(v1.1.0)*
- **Settings** is one storage record. Each field is owned by the context
  that uses it (DISC-DOMAIN-DISCOVERY).
- [S] **ExploredCell uses H3 resolution 9 cells** (edges about 174 m,
  about 0.105 km² per cell), the closest standard size to C3's "about
  150 m". The library choice goes to M3.
- **ExploredCell merge**, in this order:
  1. Take the greatest `clearedAt` seen on any side.
  2. Drop every cell whose `firstSeenAt` is earlier than that epoch.
  3. Take the union, keeping the earliest remaining `firstSeenAt` per
     cell.

  Filtering before the union stops an old pre-clear copy from an offline
  device pulling a re-explored cell's timestamp back before the epoch.
  Because the epoch syncs, clearing on one signed-in device clears the
  server and the user's other devices (stated to the user in J11).
- **Journal `city`:**
  - online: reverse geocoding through the gateway, limited to ≤1 request
    per second (R10) and cached;
  - offline: place names in installed packs;
  - otherwise left blank and filled in later.

### Storage choice (KC-073)

[S] **Both, with distinct roles.**
- **On the device:** an embedded relational store (SQLite family; the
  library is chosen in M3). It is authoritative for guests and for every
  device-only entity.
- **On the server:** a dedicated database and role on the VM's
  PostgreSQL, separate from other workloads (KC-085). It holds Account,
  CodexEntry, ExploredCell and ExploredArea for signed-in users only.
- **GeoJSON** is the export and interchange format for explored cells and
  the Codex, offered as a user-facing export. It is not the primary
  store.

### Relationships

- A Trip has 1..n Alarms. Alarms live and die with the trip (R2).
- An Account owns 0..n CodexEntries and ExploredCells, plus one
  ExploredArea (server side).
- A LocationFix with context `trip` may create ExploredCells only under
  R18's double opt-in. It never creates JournalEntries (R18).

### Retention and erasure

- LocationFix is a 30-day rolling log, kept on the device only (the proxy
  answered Q1). It never syncs (R8).
- R21 erasure deletes LocationFix, JournalEntry and ExploredCell rows,
  and sets the `clearedAt` epoch.
- Server: synced entities persist until edited, deleted or the account is
  deleted, with a 30-day purge (R19).

## Hidden Assumptions

- Last-write-wins is enough for CodexEntry, because a single user rarely
  edits the same place on two devices at once.
- Device-protected storage holding only trip and alarm state, with no
  location history, is acceptable exposure for the before-unlock alert.

## Strengths

- Device-only entities match R8 by construction. Raw location cannot sync
  because it has no server table.
- Explored-cell sync is conflict-free, and erasure is honoured across
  devices.
- R3, R17, R24 and R25 can all be evaluated after a restart, from
  persisted Trip fields.

## Weaknesses

- Two stores, and two storage classes on the device, mean a mapping layer
  and migrations on both sides.
- Deleted CodexEntries need tombstones until every device has synced.
  [S] Tombstones expire 90 days after deletion. A device offline for
  longer than that gets a full resync.

## Questions

1. *(Resolved by the proxy)* LocationFix uses a 30-day rolling window,
   kept on the device only and clearable.
2. *(→ M3)* The device database library, the H3 binding and the server
   migration tooling.
3. *(Resolved in v1.1.0)* ConsentRecord is device-only. Guests have no server
   identity, and KC-134 needs no server copy.

## Recommendations

- **Keep:** device-authoritative storage for guests; a server copy only
  for synced entities.
- **Improve:** a minimal DP footprint for R25; filtering by epoch before
  the union.
- **Remove:** GeoJSON as a primary store. It stays as the export format.

## Architecture Review (2026-11-02)

- **Against the Frozen DISC- investigations:**
  - Device-only and synced entities match R8.
  - Erasure matches R21 (v1.1.0).
  - TripBootRecord in DP serves R25 (J3).
  - Trip fields serve R3, R17 and R24.
  - Consistent.
- **Citations:** all current. KC-048 and KC-049 are superseded by KC-121.
- **Divergences:** none (no code).
- **Result:** passes.

## Architecture Review: v1.2.0 re-review (2026-11-02)

- Consistent with ADR-0005, R25 and J3.
- The DP footprint is still minimal (no coordinates).
- ARCH-SYSTEM §2 cites v1.1.0. That will be updated at ARCH-SYSTEM's
  next revision.

## Revision History

- **v1.1.0 (2026-11-02):** ConsentRecord widened to every consent the
  app requests (CONST-CORE Law 12, KC-134), with `withdrawnAt` and the
  withdrawal semantics; Q3 closed as device-only. Additive only. Approved
  by the owner proxy (KC-141).
- **v1.2.0 (2026-11-02):** adds the TripBootRecord heartbeat fields and
  their fail-safe reading. Additive only; required by ADR-0005 (the
  watchdog). Also restores two lines lost in v1.1.0 to a script defect
  (non-semantic). Approved by the owner proxy (KC-168).

## Confidence Level

**Overall: Inference (70).** Entities are derived from Frozen capabilities,
rules and journeys. Storage roles and retention are design proposals.
