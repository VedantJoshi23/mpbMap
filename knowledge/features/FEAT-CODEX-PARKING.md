---
id: FEAT-CODEX-PARKING
title: "Etak — Feature: Codex & Parking"
version: 0.2.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M6
category: Features
priority: Medium
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
  - DOM-CODEX
required_by: []
related_documents: []
related_domains:
  - DOM-CODEX
  - DOM-LOCATION
  - DOM-PLACES
  - DOM-ACCOUNT
related_features: []
related_decisions: []
tags:
  - feature
risk: Medium
complexity: Low
---

## Feature: Codex & Parking

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Draft** v0.2.0, the revision after owner-proxy round 1
(ADR-0001).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-CODEX` v1.0.0 and `DOM-LOCATION` v1.0.0, `DOM-PLACES` v1.0.0, `DOM-ACCOUNT` v1.0.0. STD-* v1.0.0 (all Frozen).

**Tier:** **Medium.** It was converted from a spec card, because it crosses domains and DOM-LOCATION is on its path. *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **2.**

### 1. Overview

Mark places in gaming terms (the Codex) and save parking spots, all available offline and without an account.

### 2. Owning Domain

**Owning domain:** `DOM-CODEX`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- DOM-PLACES → DOM-CODEX (Allowed): 'Mark as…' from a place card.
- **via app/**: the current-location port wraps DOM-LOCATION for 'Park here' and 'Return to vehicle'.
- DOM-CODEX consumes RemoteChangesApplied and SignedIn/SignedOut/AccountDeleted (DOM-ACCOUNT, Allowed).
- **via app/**: Codex markers are passed as a DOM-MAP overlay.

### 3. Acceptance Criteria

- [ ] Long-press or a place card, then 'Mark as…', with the fixed C6 categories. Places can be renamed.
- [ ] 'Park here' saves a Garage entry, and the newest is active. When location is denied: show the disclosure first, or offer manual placement on the map.
- [ ] Offline 'Return to vehicle' shows bearing and distance (J4).
- [ ] Deletions leave tombstones that expire after 90 days.
- [ ] Markers are distinguished by shape and label (STD-ACCESSIBILITY 3).
- [ ] Works with no account (Law 4).

### 4. API Surface

- `CodexApi` (DOM-CODEX §4)

### 5. Events

#### Publishes

- CodexEntryChanged

#### Consumes

- RemoteChangesApplied, SignedIn/SignedOut/AccountDeleted

*(a subset of `DOM-CODEX`'s declared events)*

### 6. Data Changes

- `codex.db`: CodexEntry

### 7. Edge Cases & Validations

- Several parking spots: the newest is active.
- A device offline for more than 90 days: full resync.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-ACCESSIBILITY | Yes | Rules 2–3. |
| STD-DATABASE | Yes | Rules 1 and 4. |
| STD-TESTING | Yes |  |
| STD-CODE | Yes |  |
| STD-CICD | Yes |  |
| STD-COMPLIANCE | Yes | Rule 2 (disclosure names parking). |
| STD-SECURITY | No |  |
| STD-API | No |  |
| STD-PERFORMANCE | No |  |
| STD-OBSERVABILITY | No |  |
| STD-SKINS | Yes | Rule 6 (marker icons). |

### 9. Definition of Done

- [ ] Criteria pass.
