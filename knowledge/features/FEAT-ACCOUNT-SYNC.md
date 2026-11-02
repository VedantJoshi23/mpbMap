---
id: FEAT-ACCOUNT-SYNC
title: "Kairos — Feature: Optional Account, Sync & Deletion"
version: 1.0.1
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M6
category: Features
priority: High
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
  - ADR-0007
  - ADR-0008
  - DOM-ACCOUNT
  - STD-API
  - STD-SECURITY
  - STD-DATABASE
required_by: []
related_documents: []
related_domains:
  - DOM-ACCOUNT
  - DOM-CODEX
  - DOM-EXPLORATION
  - DOM-LOCATION
related_features: []
related_decisions: []
tags:
  - feature
risk: High
complexity: Medium
---

## Feature: Optional Account, Sync & Deletion

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-ACCOUNT` v1.0.0 and `DOM-CODEX` v1.0.0, `DOM-EXPLORATION` v1.0.0, `DOM-LOCATION` v1.0.0. STD-* v1.0.0 (all Frozen). The revisions it relied on (DOM-CODEX v1) are now applied and Frozen (KC-165).1.0 and DOM-EXPLORATION v1.1.0 (`applyRemote`), and ARCH-SYSTEM v1.2.0 (sync applied by the owner).

**Tier:** **High.** Security and data (Laws 4–6). *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **4.** The backend and its deployment wait for the owner's M8 approval (ADR-0001 rule 6).

### 1. Overview

Optional Google sign-in that syncs saved places, parking and explored cells, plus complete account deletion in-app and on the web.

### 2. Owning Domain

**Owning domain:** `DOM-ACCOUNT`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- DOM-ACCOUNT → DOM-CODEX (Allowed): `CodexApi.applyRemote(entries)`. CODEX applies LWW and tombstones itself.
- DOM-ACCOUNT → DOM-EXPLORATION (Allowed): `ExplorationApi.applyRemote(cells, clearedAt)`. EXPLORATION applies epoch-then-union itself.
- DOM-ACCOUNT → DOM-LOCATION (Allowed): `ConsentApi.record/withdraw` (sign-in-sync).
- DOM-ACCOUNT consumes CodexEntryChanged, ExploredCellsAdded and ExplorationCleared.

### 3. Acceptance Criteria

- [ ] Every feature except sync works without signing in (Law 4).
- [ ] The sign-in-sync ConsentRecord is recorded **before the first sync**. Withdrawing it stops sync and signs out, and is not erasure.
- [ ] Sign-in uses Credential Manager. The server verifies the ID token (audience, issuer, expiry) and stores only the subject ID and **hashed** refresh tokens. On the device, the refresh token is Keystore-wrapped.
- [ ] **Merge:** the Codex uses LWW. Cells use **epoch-then-union**: take the max `clearedAt`, drop cells first seen before it, then union and keep the earliest firstSeenAt. DOM-ACCOUNT **never writes `codex.db` or `exploration.db`**.
- [ ] Only the Codex and cells sync (Law 5).
- [ ] Sign-out offers to keep local data (the default) or clear it.
- [ ] **Deletion:**
- [ ]   - idempotent, and revokes every session (STD-API 7);
- [ ]   - purges Account, CodexEntry, ExploredCell and ExploredArea within 30 days, with **at most 37 days** including backups;
- [ ]   - after the purge, `deletion_job` keeps only an opaque ID and timestamps;
- [ ]   - web path: the GitHub Pages form signs in with Google and POSTs to the backend, with **CORS restricted to that exact origin**, plus the contact fallback;
- [ ]   - device: AccountDeleted triggers keep-or-clear.
- [ ] A CI schema test asserts the server holds only the user tables plus STD-DATABASE 5's closed list, with no coordinates and no device-only entity.
- [ ] The sync client reaches the API through the exact-host bootstrap (STD-SECURITY 4).

### 4. API Surface

- `/v1/auth/*`, `/v1/sync/codex`, `/v1/sync/cells`, `DELETE /v1/account` (STD-API)

### 5. Events

#### Publishes

- SignedIn, SignedOut, AccountDeleted
- RemoteChangesApplied

#### Consumes

- CodexEntryChanged
- ExploredCellsAdded, ExplorationCleared

*(a subset of `DOM-ACCOUNT`'s declared events)*

### 6. Data Changes

- Server: Account, CodexEntry, ExploredCell, ExploredArea, `session`, `deletion_job` (STD-DATABASE 5)

### 7. Edge Cases & Validations

- Deletion while offline: queued, with the web fallback.
- Integrity quota exhausted: rate-limit-only.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-SECURITY | Yes | Rules 4–9. |
| STD-API | Yes | Rules 1–7. |
| STD-DATABASE | Yes | Rules 3, 5 and 6. |
| STD-TESTING | Yes | Rules 4, 5 and 9. |
| STD-COMPLIANCE | Yes | Rules 3–4. |
| STD-OBSERVABILITY | Yes | Rule 4 (purge alerts). |
| STD-CICD | Yes | Rule 5. |
| STD-ACCESSIBILITY | Yes | Rule 2. |
| STD-CODE | Yes |  |
| STD-PERFORMANCE | No |  |
| STD-SKINS | No |  |

### 9. Definition of Done

- [ ] Criteria pass.
- [ ] The backend is deployed behind nginx limit_req, **after the owner's M8 approval**.
- [ ] The restore drill is done (TD13).
- [ ] The web deletion page is live.
- [ ] Flagged commercial free tiers (Law 7): Play Integrity, DuckDNS, Oracle object storage.
- [ ] The pending domain and ARCH revisions are Frozen.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).

*Revision 1.0.1 (2026-11-02): app renamed from Etak to Kairos per ADR-0010 (identity text only, non-semantic).*
