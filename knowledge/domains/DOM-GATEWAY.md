---
id: DOM-GATEWAY
title: "Domain: Provider Gateway"
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
related_domains: [DOM-KERNEL]
related_features: []
related_decisions: [ADR-0004, ADR-0007, ADR-0008]
tags:
  - domain
risk: High
complexity: Medium
---

# Domain: Provider Gateway (BC10)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-164), after owner-proxy
review (ADR-0001). It binds to CONST-CORE and ARCH-SYSTEM (both
Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

A backend anticorruption layer for third-party geo services (search, reverse geocoding, routing, and weather later). It enforces provider policy, caching, abuse protection and coordinate privacy.

## 2. Ownership

### Owns

- Provider adapters (Photon, Nominatim, OSRM demo; Open-Meteo later)
- Rate limits (per provider, per client, global), caching with coarsened keys, User-Agent
- Abuse protection (Integrity, request caps); nginx limit_req sits in front

### Explicitly Does NOT Own

- Any user-linked storage of query coordinates (forbidden)
- Accounts (DOM-ACCOUNT)

## 3. Invariants

### Invariant 1

> Aggregate request rates never exceed each provider's published limit (e.g. Nominatim and OSRM ≤1 request/second).

**Source:** R10; Law 10; KC-088; KC-089

### Invariant 2

> Query coordinates are transient: never stored linked to a client, absent from logs, and coarsened in cache keys.

**Source:** Law 5

### Invariant 3

> Responses carry provider attribution for display.

**Source:** R11

### Invariant 4

> Provider swaps happen server-side without an app release.

**Source:** ADR-0004

### Invariant 5

> No paid provider tier is used without the owner (rule 2).

**Source:** Law 7

## 4. API Surface

- `/v1/search`, `/v1/reverse`, `/v1/route` (STD-API): **coordinates in POST bodies only**, each rate-limited with 429 and Retry-After
- **ACRA crash ingest** `/v1/crash`: BC10 rate limits; payloads with location fields are rejected (ARCH-SYSTEM v1.1.0)

## 5. Events

### Publishes

- None (HTTP only).

### Consumes

- None.

## 6. Data Ownership

- An in-memory and short-TTL cache with coarsened keys; no persistent user data
- `crash_report` (ACRA allowlisted fields, 90 days) and `gateway_session` (no user identity, TTL ≤ 24 h) (STD-DATABASE rule 5)

## 7. Dependencies

### Allowed

- `DOM-KERNEL`

### Forbidden

- `DOM-ACCOUNT`
- It shares the deployable with DOM-ACCOUNT but not its tables (ARCH-SYSTEM §4).

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-GATEWAY
paths:
  - "backend/gateway/**"
allowed_dependencies:
  - "DOM-KERNEL"
forbidden_dependencies:
  - "DOM-ACCOUNT"
```

## 8. Edge Cases & Validations

- Provider down or throttled: return 503 or 429, and clients degrade (ADR-0004).
- Abuse burst: per-client 429 plus nginx limit_req.
- Integrity quota exhausted: rate-limit-only.

## 9. Open Questions

- The FOSSGIS Valhalla terms are to be verified before M8 (ADR-0004).

## Architecture Review (2026-11-02)

Checked against ARCH-SYSTEM v1.1.0, CONST-CORE and the DISC-DOMAIN-DISCOVERY
event catalogue: §5 and §7 match the declared api-level dependencies, and
the boundary block matches the prose. Divergences: none. Result: passes.
