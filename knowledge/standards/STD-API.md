---
id: STD-API
title: "Backend API"
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
  - ADR-0004
  - ADR-0007
  - ADR-0008
  - ADR-0009
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

# STD-API: Backend API

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). **Tier: Medium** (Oriveda ADR-0005). Lifecycle:
**Frozen** v1.0.0 (2026-11-02), approved by the owner proxy after rounds
1–2 (KC-162, ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

HTTP endpoints of the single backend service: BC9 (accounts and sync),
BC10 (the gateway), and the ACRA ingest endpoint.

## Rules

1. JSON over HTTPS, versioned under `/v1/`. A breaking change means a new
   version, and the previous version is served for **at least one app
   release and 90 days**.
2. Errors follow RFC 9457 (problem details). No stack traces or internal
   identifiers.
3. Sync endpoints are idempotent (client-generated IDs, `updatedAt`). The
   merge rules follow DISC-DATA-MODEL.
4. Gateway endpoints:
   - take **coordinates in POST bodies only, never in query strings**
     (Law 5; STD-SECURITY rule 7);
   - accept coarsened coordinates where precision is not needed;
   - never echo or persist coordinates linked to a client;
   - return provider attribution fields (R11).
5. Every endpoint has a documented rate limit. **nginx `limit_req`** sits
   in front as a defence-in-depth layer. Responses use `429` with
   `Retry-After`. The **ACRA ingest endpoint** is rate-limited like BC10
   and **rejects payloads that contain location fields**.
6. An OpenAPI spec is generated from the code and committed per release.
   CI diffs it.
7. Account deletion:
   - authenticated in-app, plus the web-form path (ADR-0008);
   - **idempotent**;
   - **revokes every server session**;
   - starts the R19 30-day purge;
   - returns the disclosed window.

## Examples

- Compliant: `POST /v1/search {"q":"cafe","bias":{"lat":28.6,"lon":77.2}}`
  with a coarsened bias.
- Non-compliant: `GET /v1/search?lat=28.61&lon=77.20`.

## Exceptions

`/health` is unversioned.

## Enforcement

The OpenAPI diff in CI; route and log tests (STD-TESTING rule 5).

## Architecture Review (2026-11-02)

Checked against CONST-CORE Laws 1–14, ARCH-SYSTEM, and ADR-0002..0009 as
amended: no contradiction. Result: passes.
