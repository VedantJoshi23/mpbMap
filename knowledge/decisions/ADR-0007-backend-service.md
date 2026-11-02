---
id: ADR-0007
title: "Backend Service, Database and Deployment"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M3
category: Decision
priority: High
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
required_by: []
related_documents:
  - DISC-TECHNICAL-ARCHITECTURE
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - technology
risk: Medium
complexity: Medium
---

# ADR-0007: Backend Service, Database and Deployment

Technology decision under Oriveda `OV-004`, with **Hybrid** stack freedom
(KC-151): options are compared, one is recommended, and the owner proxy
decides (ADR-0001). Constraints: CONST-CORE (Frozen), especially Law 7
(zero spend) and the KC-151 preferences. Lifecycle: **Frozen** v1.0.0 (2026-11-02), decided by the owner proxy
under KC-151 (EVD-045). Freezing does not authorise spikes or code
(KC-107).

## Context

One deployable service (ARCH-SYSTEM §4) hosts BC10 (the gateway) and BC9
(accounts and sync). It runs on a shared ARM VM with about 2.4 GB of free
RAM (KC-085, KC-086), behind the existing nginx, with the existing
PostgreSQL, Docker available, zero spend, and backups disclosed (37 days
at most).

## Options Considered

- **Ktor (Kotlin, JVM).** The same language as the app (portfolio
  coherence; shared DTOs through a common module). Lightweight for a JVM.
  About 150–250 MB RSS, tunable.
- **Go (net/http plus chi).** The smallest footprint (about 20–40 MB) and
  simple deployment. A second language in the repo.
- **Spring Boot.** Feature-rich, but the heaviest memory and startup cost
  on a shared VM.

**Database**
- a dedicated database and role on the existing PostgreSQL;
- versus a new container (duplicate memory);
- versus SQLite (weaker concurrency for sync).

## Decision

- **Ktor on JVM 21**, with heap capped (e.g. `-Xmx256m`). Spike S5 checks
  memory. If S5 exceeds the budget, **Go** is the pre-approved fallback.
- **A dedicated database and role** on the existing PostgreSQL. Flyway
  migrations.
- **Docker image** (arm64), behind the existing nginx. Logs exclude
  coordinates (Law 5).
- **Backups:** nightly `pg_dump`, 7-day retention, an encrypted off-VM
  copy (the flagged free tier, rule 2 if paid).
- The restore drill runs before the first release (TD13).

### Amendments from owner-proxy round 1

- **Data access:** **Exposed** (Kotlin SQL DSL) over **HikariCP**, with a
  **small pool** (max 5), because the shared PostgreSQL serves other
  workloads (KC-086).
- **Database access:** the container reaches the host PostgreSQL through
  the Docker host gateway, as a dedicated **least-privilege role** limited
  to the project database.
- **nginx `limit_req`** in front of the service: a defence-in-depth layer
  below BC10's limits.
- **Spike clause:** confirmed by S5 (memory). A failure adopts the
  pre-approved **Go** fallback through a draft revision.

## Consequences

- One language across app and server.
- The JVM uses more memory than Go, which S5 bounds.
- No new database service.

## Revisit Criteria

S5 memory; VM contention (TD8); the budget review.

## Architecture Review (2026-11-02)

Checked against CONST-CORE and ARCH-SYSTEM (both Frozen), with no conflict. Laws 5 and 7 are satisfied; one deployable service.
Divergences: none (no code). Worked example under Oriveda `examples/`: not
applicable (Oriveda ADR-0002). Result: passes.
