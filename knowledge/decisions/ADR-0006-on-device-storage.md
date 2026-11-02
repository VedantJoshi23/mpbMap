---
id: ADR-0006
title: "On-Device Storage and Spatial Indexing"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M3
category: Decision
priority: High
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
risk: Medium
complexity: Medium
---

# ADR-0006: On-Device Storage and Spatial Indexing

Technology decision under Oriveda `OV-004`, with **Hybrid** stack freedom
(KC-151): options are compared, one is recommended, and the owner proxy
decides (ADR-0001). Constraints: CONST-CORE (Frozen), especially Law 7
(zero spend) and the KC-151 preferences. Lifecycle: **Draft**.

## Context

DISC-DATA-MODEL v1.1.0 sets the storage classes:
- TripBootRecord in device-protected (DP) storage;
- everything else in credential-encrypted (CE) storage;
- H3 resolution-9 cells;
- a 30-day LocationFix log;
- tombstones;
- Law 6 erasure.

## Options Considered

- **Room (SQLite) for CE.** Jetpack standard, migrations, Flow/coroutines,
  testable.
- **SQLDelight.** Typed SQL and multiplatform. Not needed: the app is
  Android only.
- **Realm.** A heavier runtime, now in maintenance mode, and a lock-in
  risk.

**DP store:**
- a tiny Room database opened from `createDeviceProtectedStorageContext()`;
- or a Proto DataStore file in DP storage. A single small record suits it
  well.

**Hex grid:**
- **H3 Java binding.** Native libraries; ABI coverage checked in S5.
- **A pure-JVM hex implementation.** Fallback.

**Settings:** Jetpack DataStore (Preferences).

## Decision

- **Room** for CE entities: one database per owning context, consistent
  with ARCH-SYSTEM's no-shared-storage rule.
- **Proto DataStore in DP storage** for TripBootRecord. It is minimal, has
  no SQL engine on the boot path, and is atomic.
- **Jetpack DataStore** for settings.
- **H3 Java.** If S5 finds missing Android ABIs, use a pure-JVM fallback
  that produces the same cell IDs.
- Erasure runs as one transaction per database, with confirmations
  aggregated by the EraseLocalHistory flow.

## Consequences

- Standard, well-documented tooling, and a small direct-boot surface.
- Several Room databases add some boilerplate. That is accepted, because
  it buys boundary clarity.

## Revisit Criteria

S5 ABI results; S3 boot-path timing with DataStore.
