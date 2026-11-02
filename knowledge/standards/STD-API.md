---
id: STD-API
title: "Backend API"
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

# STD-API: Backend API

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

HTTP endpoints of the single backend service: BC9 (accounts and sync) and
BC10 (the gateway).

## Rules

1. JSON over HTTPS, versioned under `/v1/`. A breaking change means a
   new version, and the previous version is served for at least one app
   release.
2. Errors follow RFC 9457 (problem details). No stack traces or internal
   identifiers.
3. Sync endpoints are idempotent (client-generated IDs, `updatedAt`). The
   merge rules follow DISC-DATA-MODEL (LWW for the Codex,
   epoch-then-union for cells).
4. Gateway endpoints:
   - accept coarsened coordinates where precision is not needed;
   - never echo or persist coordinates linked to a client (Law 5);
   - return provider attribution fields for display (R11).
5. Every endpoint has a documented rate limit (STD-SECURITY rule 6) and
   returns `429` with `Retry-After`.
6. An OpenAPI spec is generated from the code and committed per release.
7. Account deletion endpoint:
   - authenticated (in-app);
   - plus a web-form path (ADR-0008);
   - returns the disclosed purge window.

## Examples

- Compliant: `POST /v1/sync/codex` with a batch of
  `{id, updatedAt, deleted}` records.
- Non-compliant: `/v1/search?lat=…` writes the query into an analytics
  table.

## Exceptions

Health endpoints are unversioned (`/health`).

## Enforcement

OpenAPI diff in CI; route tests (STD-TESTING).
