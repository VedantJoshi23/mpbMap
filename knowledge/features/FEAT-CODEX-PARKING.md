---
id: FEAT-CODEX-PARKING
title: "Kairos — Feature: Codex & Parking"
version: 1.0.1
status: Frozen
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
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

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

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).

*Revision 1.0.1 (2026-11-02): app renamed from Etak to Kairos per ADR-0010 (identity text only, non-semantic).*
