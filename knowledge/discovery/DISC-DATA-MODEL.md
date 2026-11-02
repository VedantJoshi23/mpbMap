---
id: DISC-DATA-MODEL
title: "Discovery: Data Model"
version: 0.1.0
status: Draft
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
log](evidence/README.md). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Suggestions are marked **[S]**. Risk is **High** because it
fixes where location and account data live (Oriveda ADR-0005).

The earlier claims KC-048 and KC-049 inferred entities from the
*reference* app. This investigation replaces them with this project's own
model, derived from the Frozen capabilities (C-n) and rules (R-n).

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
  Users can clear local data (R21).
- Skins are bundles: style, fonts, icon set, marker, accents, a 3D flag
  and a 2D fallback (C1).
- A PostgreSQL instance already runs on the VM (KC-085).

## Interpretation

### Entities

All entities are [S].

| Entity | Key fields | Lives | Aggregate |
| --- | --- | --- | --- |
| **Skin** | id, codename, version, styleRef, is3D, fallback2DId, fontSet, iconSet, accents | App bundle (static data) | Skin |
| **Place** | providerId, name, lat/lon, category | Transient (search result, cached) | none |
| **CodexEntry** | id, category (Waypoint…Point of Interest, Garage), name, lat/lon, note, createdAt, updatedAt, deleted | Device; synced when signed in | CodexEntry |
| **ParkingSpot** | a CodexEntry with category Garage, plus parkedAt and an `active` flag | Device; synced | CodexEntry |
| **Trip** | id, travelMode, destination (lat/lon, label), startedAt, endedAt, state | **Device only** | Trip (root) |
| **Alarm** | id, tripId, radiusM, state (armed / fired / snoozed / dismissed), firedAt | **Device only** | Trip |
| **LocationFix** | ts, lat/lon, accuracyM, source (gps / network), context (in-use / trip) | **Device only** | none (rolling log) |
| **ExploredCell** | cellId, firstSeenAt | Device; synced | ExploredArea |
| **JournalEntry** | placeRef or cell summary, visitedAt, city | **Device only** | Journal |
| **OfflinePack** | id, name, bbox, minZoom, maxZoom, sizeBytes, status | Device only | OfflinePack |
| **Account** | id, authSubject, createdAt, deletionRequestedAt | Server | Account |
| **Settings** | units, defaultSkin, revealOn, revealDuringTrips, alarm defaults | Device only | Settings |

- ParkingSpot is a CodexEntry and not its own table. The Codex already
  has the Garage category (C6), so parking spots are visible in the Codex
  list for free. Several spots may be saved; the newest is `active`.
- [S] **ExploredCell uses hexagonal cells**, e.g. H3 at resolution 9
  (about 0.1 km² per cell, edges about 170 m). That is the closest
  standard size to C3's "about 150 m". The library choice goes to M3.
- [S] **Explored cells merge as a grow-only set**: sync is a set union,
  with no conflicts. "Clear explored map" (R21) is recorded as a
  `clearedAt` epoch that sync honours, so cells first seen before the
  epoch are dropped on every device.

### Storage choice (KC-073)

[S] **Both, with distinct roles.**
- **On the device:** an embedded relational store (SQLite family; the
  library is chosen in M3). It is authoritative for guests and for every
  device-only entity.
- **On the server:** a dedicated database and role on the VM's
  PostgreSQL, separate from other workloads (KC-085). It holds Account,
  CodexEntry and ExploredCell for signed-in users only.
- **GeoJSON** is the export and interchange format for explored cells and
  the Codex, offered as a user-facing export. It is not the primary
  store. This satisfies KC-073's "GeoJSON or server DB" without making
  GeoJSON files the source of truth.

### Relationships

- A Trip has 1..n Alarms. Alarms live and die with the trip (R2).
- An Account owns 0..n CodexEntries and 0..n ExploredCells (server side).
- A LocationFix with context `trip` may create ExploredCells only under
  R18's double opt-in. It never creates JournalEntries (R18).

### Retention

[S]
- LocationFix is a rolling log, kept on the device for 30 days and then
  deleted. The journal and explored cells keep only derived data.
- Server: synced entities persist until edited, deleted or the account is
  deleted, with a 30-day purge (R19).

## Hidden Assumptions

- Last-write-wins is enough for CodexEntry, because a single user rarely
  edits the same place on two devices at once. Explored cells avoid the
  problem altogether by being a set.
- A 30-day raw-location window is enough for any journal rebuild and
  limits stored history (KC-098 spirit).
- Hexagons at resolution 9 are fine-grained enough to feel like
  exploration on foot.

## Strengths

- Every device-only entity matches R8. Alarms and raw location
  structurally cannot sync.
- Set semantics make explored-cell sync conflict-free.

## Weaknesses

- Two stores (device and server) mean a mapping layer and migrations on
  both sides.
- Deleted CodexEntries need tombstones (`deleted`) until every device has
  synced. The tombstones need their own expiry rule.

## Questions

1. Is a 30-day rolling window for raw LocationFix acceptable, or should
   raw fixes be discarded as soon as they are turned into cells and
   journal entries? The second option means less data held.
2. *(→ M3)* The device database library, the H3 binding and the server
   schema/migration tooling.

## Recommendations

- **Keep:** device-authoritative storage for guests; a server copy only
  for synced entities.
- **Improve:** grow-only-set sync for explored cells; tombstone expiry for
  the Codex.
- **Remove:** GeoJSON as a primary store. It stays as the export format.

## Confidence Level

**Overall: Inference (70).** Entities are derived from Frozen capabilities
and rules. Storage roles and retention are design proposals.
