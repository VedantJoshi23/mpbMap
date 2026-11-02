---
id: FEAT-PROVIDER-GATEWAY
title: "Etak — Feature: Provider Gateway"
version: 1.0.0
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
  - ADR-0004
  - ADR-0007
  - ADR-0008
  - DOM-GATEWAY
  - STD-API
  - STD-SECURITY
required_by: []
related_documents: []
related_domains:
  - DOM-GATEWAY
related_features: []
related_decisions: []
tags:
  - feature
risk: High
complexity: Medium
---

## Feature: Provider Gateway

Authored under Oriveda `OV-007` (Frozen) through `PRM-FEATURE`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-166), after owner-proxy
rounds 1–2 (ADR-0001). Freezing does not start implementation (KC-107).

**Context.** Binds: CONST-CORE v1.0.0, ARCH-SYSTEM v1.1.0, ADR-0002..0009
v1.0.0, DISC-HIDDEN-BUSINESS-RULES v1.1.0 and DISC-DATA-MODEL v1.2.0;
its owning `DOM-GATEWAY` v1.0.0. STD-* v1.0.0 (all Frozen).

**Tier:** **High.** Security-bearing (Laws 5, 7, 10). *Tier rule (proxy ruling):* a feature "touches" a
High-tier spec only when it adds to or changes that spec or its
enforcement path. Using frozen, already-declared APIs does not count.

**Slice:** **3**, before any online search or route. It waits for the owner's M8 approval.

### 1. Overview

The backend anticorruption layer that every online geo call passes through. It enforces provider policy, coordinate privacy and abuse protection, and hosts ACRA ingest.

### 2. Owning Domain

**Owning domain:** `DOM-GATEWAY`, exactly one.

**Cross-domain calls.** Each call matches the Allowed list of the domain
that initiates it. `app/` handoffs go through the composition root and
add no module dependency.

- None initiated. Clients reach it over HTTP only.

### 3. Acceptance Criteria

- [ ] Aggregate rates never exceed each provider's limit: Nominatim and OSRM ≤1 request per second; Photon fair use. An identifying User-Agent is sent (R10).
- [ ] There is no client-side Nominatim autocomplete. Typeahead goes to Photon only.
- [ ] Coordinates are accepted **in POST bodies only**. Cache keys use coarsened coordinates. Nothing is stored linked to a client (Law 5).
- [ ] The API vhost's nginx access log omits query strings, and application logs contain no coordinates (STD-SECURITY 7). Tested.
- [ ] Responses carry provider attribution (R11).
- [ ] Per-client and global limits, plus nginx `limit_req`. Responses use 429 with Retry-After.
- [ ] Play Integrity on gateway sessions within its free quota (**flagged**). When the quota is exhausted, rate-limit-only.
- [ ] ACRA ingest `/v1/crash`: rate-limited; payloads with location fields are rejected; reports kept 90 days.
- [ ] Provider error rates are exported as metrics (STD-OBSERVABILITY 3).

### 4. API Surface

- `POST /v1/search`, `POST /v1/reverse`, `POST /v1/route`, `POST /v1/crash`

### 5. Events

#### Publishes

- None (HTTP only).

#### Consumes

- None.

*(a subset of `DOM-GATEWAY`'s declared events)*

### 6. Data Changes

- Short-TTL cache with coarsened keys; `crash_report`; `gateway_session` (no user identity, TTL ≤24 h)

### 7. Edge Cases & Validations

- Provider down or throttled: 503 or 429.
- Abuse burst: 429 plus limit_req.

### 8. Non-Functional Considerations

| Standard | Relevant? | Notes |
| --- | --- | --- |
| STD-API | Yes | Rules 1–6. |
| STD-SECURITY | Yes | Rules 6–8. |
| STD-OBSERVABILITY | Yes | Rules 2–4. |
| STD-TESTING | Yes | Rule 5. |
| STD-DATABASE | Yes | Rule 5. |
| STD-COMPLIANCE | Yes | Rule 3. |
| STD-CICD | Yes | Rules 1 and 5. |
| STD-CODE | Yes |  |
| STD-PERFORMANCE | Yes | Typeahead p95 ≤800 ms. |
| STD-ACCESSIBILITY | No | No UI. |
| STD-SKINS | No |  |

### 9. Definition of Done

- [ ] Criteria pass, including the log tests.
- [ ] The FOSSGIS Valhalla terms are researched (ADR-0004).
- [ ] Deployed after the owner's M8 approval.

### Architecture Review (2026-11-02)

Checked against the owning domain's Frozen spec: one owner; every cross-domain
call matches the initiator's Allowed list or goes through an `app/` handoff;
events and data stay within the domain; §8 cites the relevant Standards.
Result: passes (KC-166).
