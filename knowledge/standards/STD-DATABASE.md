---
id: STD-DATABASE
title: "Data Storage"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M4
category: Standard
priority: High
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - ADR-0006
  - ADR-0007
  - ADR-0008
  - DISC-DATA-MODEL
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions: []
tags:
  - standard
risk: High
complexity: Low
---

# STD-DATABASE: Data Storage

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). **Tier: High** (Oriveda ADR-0005). Lifecycle:
**Frozen** v1.0.0 (2026-11-02), approved by the owner proxy after rounds
1–2 (KC-170, ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Room databases and DataStore on the device; PostgreSQL on the server.

## Rules

1. **One Room database per owning context.** No cross-context joins
   (ARCH-SYSTEM §1). **Settings** live in Jetpack DataStore (ADR-0006).
2. **TripBootRecord lives only in DP storage** (Proto DataStore):
   - its fields follow DISC-DATA-MODEL v1.2.0, including the heartbeat;
   - it holds **no coordinates**;
   - **proto field numbers are never reused**;
   - nothing else goes into DP storage.
3. **Migrations:**
   - Room: tested migrations, with no destructive fallback in release
     builds;
   - server: Flyway, forward-only and **expand-then-contract**, so the
     previous image still runs on the new schema. Otherwise a rollback
     means a restore from the pre-deploy backup.
4. **Retention is enforced in code:**
   - LocationFix is pruned to 30 days;
   - tombstones expire after 90 days;
   - the server purges 30 days after account deletion (R19);
   - backups are kept 7 days;
   - crash reports are kept 90 days.
5. **Server user data** is limited to Account, CodexEntry, ExploredCell
   and ExploredArea (Law 5). Any **operational table** must be on this
   **closed list**, with a retention period, **no coordinates and no
   device-only entity**:

   | Table | Purpose | Retention | ADR |
   | --- | --- | --- | --- |
   | `session` (hashed refresh tokens) | Revocable sessions | Until expiry or revocation; deleted with the account | ADR-0008 |
   | `crash_report` (ACRA allowlisted fields) | Diagnostics | 90 days | ADR-0009 |
   | `deletion_job` | Purge queue | While pending it holds the account link. **Once the purge completes, the row keeps only an opaque job ID and timestamps** (no authSubject, no account link), for 30 days | ADR-0008 |
| `gateway_session` | Gateway access after an Integrity check | **No user identity**; TTL ≤ 24 h | ADR-0008 |
   | `flyway_schema_history` | Migrations | Permanent | ADR-0007 |

   The list is closed: nothing else is stored server-side. Adding a table for a device-only entity requires a Constitution
   amendment.
6. **Data access** (ADR-0007): Exposed over HikariCP, **max pool 5**
   (KC-086), a dedicated database, and a least-privilege role.
7. **Erasure** (ADR-0006): EraseLocalHistory is a **persisted, idempotent
   command**. There is one transaction per Room database, and each
   context retries on start until it confirms.
8. **Spatial data:** H3 resolution-9 cell IDs stored as 64-bit integers.
9. **Backups:**
   - nightly;
   - an encrypted off-VM copy, on a **flagged free tier** (Law 7);
   - a restore drill before the first release and then quarterly (TD13).

## Examples

- Compliant: `exploration.db` owns `explored_cell`.
- Non-compliant: a `trip` table on the server.

## Exceptions

None for rules 2, 5 and 7.

## Enforcement

Migration tests; schema review against rule 5's closed list in
PRM-REVIEW; tests for the retention jobs.

## Architecture Review (2026-11-02)

Checked against CONST-CORE Laws 1–14, ARCH-SYSTEM, and ADR-0002..0009 as
amended: no contradiction. Result: passes.
