---
id: FEAT-ACCOUNT-SYNC
title: "Etak — Feature: Optional Account, Sync & Deletion"
version: 0.1.0
status: Draft
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
  - ADR-0007
  - ADR-0008
  - DOM-ACCOUNT
  - STD-API
  - STD-SECURITY
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
Lifecycle: **Draft**, under owner-proxy review (ADR-0001).

**Context:**
- **Binds** (Frozen): CONST-CORE, ARCH-SYSTEM, the ADRs cited below, and
  DISC-HIDDEN-BUSINESS-RULES v1.1.0.
- **Informs** (Draft, marked as such per Oriveda ADR-0005): its owning
  `DOM-` spec and the `STD-` Standards cited. A disagreement with a Draft
  is logged as a `DIV-` entry.

**Tier:** **High.** Security and data (Laws 4–6).

### 1. Overview

Optional Google sign-in that syncs saved places, parking and explored cells, plus complete account deletion in-app and on the web.

### 2. Owning Domain

**Owning domain:** `DOM-ACCOUNT`, exactly one.

**Other domains involved** (dependencies only). Each call below matches
the Allowed list of the domain that initiates it:

- DOM-ACCOUNT → DOM-CODEX and DOM-EXPLORATION (Allowed): consumes changes; publishes RemoteChangesApplied.
- DOM-ACCOUNT → DOM-LOCATION (Allowed): records the sign-in-sync consent.

### 3. Acceptance Criteria

- [ ] Every feature except sync works without signing in (Law 4).
- [ ] Sign-in uses Credential Manager. The server verifies the ID token and issues sessions. The refresh token is Keystore-wrapped.
- [ ] Guest data merges on sign-in (LWW for the Codex; union for cells).
- [ ] Only the Codex and cells sync (Law 5).
- [ ] Sign-out offers to keep local data (the default) or clear it.
- [ ] Delete account (in-app and through the web form): sessions are revoked, the server purges within 30 days, and the 37-day maximum is disclosed (Law 6).

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

- Server: Account, CodexEntry, ExploredCell, ExploredArea, `session`, `deletion_job` (STD-DATABASE rule 5)

### 7. Edge Cases & Validations

- Deletion requested while offline: queued, with the web fallback.
- Integrity quota exhausted: rate-limit-only.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-SECURITY | Yes | Rules 5–8. |
| STD-API | Yes | All rules. |
| STD-DATABASE | Yes | Rules 5 and 6. |
| STD-TESTING | Yes | Rules 4 and 5. |
| STD-COMPLIANCE | Yes | Rules 3 and 4. |
| STD-OBSERVABILITY | Yes | Purge alerts. |
| STD-CICD | Yes | Backend deploy. |
| STD-ACCESSIBILITY | Yes | Sign-in UI. |
| STD-CODE | Yes |  |
| STD-PERFORMANCE | No |  |
| STD-SKINS | No |  |

### 9. Definition of Done

- [ ] Backend deployed behind nginx with limit_req.
- [ ] The restore drill is done (TD13).
- [ ] The web deletion page is live.
- [ ] Acceptance criteria pass.
