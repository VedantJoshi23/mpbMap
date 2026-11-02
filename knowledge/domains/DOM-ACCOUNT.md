---
id: DOM-ACCOUNT
title: "Domain: Identity & Sync"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M5
category: Domains
priority: High
depends_on:
  - ARCH-SYSTEM
  - CONST-CORE
required_by: []
related_documents:
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
related_domains: [DOM-KERNEL, DOM-CODEX, DOM-EXPLORATION, DOM-LOCATION]
related_features: []
related_decisions: [ADR-0007, ADR-0008]
tags:
  - domain
risk: High
complexity: Medium
---

# Domain: Identity & Sync (BC9)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-164), after owner-proxy
review (ADR-0001). It binds to CONST-CORE and ARCH-SYSTEM (both
Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

Optional accounts (Google sign-in), sync of the Codex and explored cells, sign-out and account deletion, both client and server side.

## 2. Ownership

### Owns

- Account (server), sessions and refresh tokens
- The sync protocol (LWW for the Codex; epoch-then-union for cells)
- Account deletion (in-app and web) and the purge

### Explicitly Does NOT Own

- ConsentRecord (DOM-LOCATION; sign-in-sync consent is recorded there)
- The content of synced entities (DOM-CODEX, DOM-EXPLORATION)

## 3. Invariants

### Invariant 1

> No feature except sync requires sign-in.

**Source:** Law 4

### Invariant 2

> Only CodexEntry, ExploredCell and ExploredArea sync. Trip, Alarm, Journal and LocationFix never do.

**Source:** Law 5; R8

### Invariant 3

> Account deletion revokes all sessions and purges server rows within 30 days (37 days with backups), and is offered in-app and on the web.

**Source:** Law 6; R9; R19; ADR-0008

### Invariant 4

> Withdrawing sign-in-sync consent stops sync and signs the user out. It is not erasure.

**Source:** DISC-DATA-MODEL v1.1.0

### Invariant 5

> The server stores Google's subject ID only. The refresh token is Keystore-wrapped on the device.

**Source:** ADR-0008

## 4. API Surface

- Client: `AccountApi.signIn()`, `signOut(keepLocal)`, `deleteAccount()`, `syncNow()`
- Server: `/v1/auth/*`, `/v1/sync/codex`, `/v1/sync/cells`, `/v1/account` (DELETE) (STD-API)
- Calls `ConsentApi.record/withdraw` (DOM-LOCATION `api/`) for the sign-in-sync consent

## 5. Events

### Publishes

- SignedIn / SignedOut / AccountDeleted
- RemoteChangesApplied

### Consumes

- CodexEntryChanged (DOM-CODEX)
- ExploredCellsAdded / ExplorationCleared (DOM-EXPLORATION)

## 6. Data Ownership

- Server Postgres (dedicated DB): Account, CodexEntry, ExploredCell, ExploredArea, `session` (hashed refresh tokens), `deletion_job` (STD-DATABASE rule 5)
- Device: the Keystore-wrapped refresh token (CE)

## 7. Dependencies

### Allowed

- `DOM-KERNEL`
- `DOM-CODEX`
- `DOM-EXPLORATION`
- `DOM-LOCATION`

### Forbidden

- `DOM-TRIP`
- `DOM-MAP`
- `DOM-PLACES`
- `DOM-ROUTING`
- `DOM-OFFLINE`
- `DOM-GATEWAY`
- DOM-CODEX, DOM-EXPLORATION and DOM-LOCATION are reached only through their `api/`.

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-ACCOUNT
paths:
  - "android/feature/account/**"
  - "backend/account-sync/**"
allowed_dependencies:
  - "DOM-KERNEL"
  - "DOM-CODEX"
  - "DOM-EXPLORATION"
  - "DOM-LOCATION"
forbidden_dependencies:
  - "DOM-TRIP"
  - "DOM-MAP"
  - "DOM-PLACES"
  - "DOM-ROUTING"
  - "DOM-OFFLINE"
  - "DOM-GATEWAY"
```

## 8. Edge Cases & Validations

- Guest data merges at sign-in (LWW; cells by union).
- Sign-out with 'clear local data'.
- Deletion while offline: queued, with the web path as fallback.
- Integrity quota exhausted: rate-limit-only (ADR-0008).

## 9. Open Questions

- None.

## Architecture Review (2026-11-02)

Checked against ARCH-SYSTEM v1.1.0, CONST-CORE and the DISC-DOMAIN-DISCOVERY
event catalogue: §5 and §7 match the declared api-level dependencies, and
the boundary block matches the prose. Divergences: none. Result: passes.
