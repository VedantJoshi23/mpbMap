---
id: STD-DATABASE
title: "Data Storage"
version: 0.1.0
status: Draft
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
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions: []
tags:
  - standard
risk: Medium
complexity: Low
---

# STD-DATABASE: Data Storage

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Room databases and DataStore on the device; PostgreSQL on the server.

## Rules

1. **One Room database per owning context.** No cross-context joins
   (ARCH-SYSTEM §1).
2. **TripBootRecord lives only in DP storage** (Proto DataStore,
   ADR-0006). Nothing else goes there.
3. **Migrations:**
   - Room: auto-migrations or tested manual migrations, with no
     destructive fallback in release builds;
   - server: Flyway, forward-only.
4. **Retention is enforced in code:**
   - LocationFix is pruned to 30 days;
   - tombstones expire after 90 days;
   - server purge 30 days after account deletion (R19);
   - backups kept 7 days.
5. **Server schema** holds only Account, CodexEntry, ExploredCell and
   ExploredArea (Law 5). Adding a table for a device-only entity
   requires a Constitution amendment.
6. **Spatial data:** H3 resolution-9 cell IDs stored as 64-bit integers.
7. **Backups:**
   - nightly;
   - an encrypted off-VM copy;
   - a restore drill before the first release and then quarterly
     (TD13).

## Examples

- Compliant: `exploration.db` owns `explored_cell`.
- Non-compliant: the codex DAO queries `exploration.db`.

## Exceptions

None for rules 2 and 5.

## Enforcement

Migration tests; schema review in PRM-REVIEW; a retention job test.
