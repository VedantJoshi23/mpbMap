---
id: ADR-0007
title: "Backend Service, Database and Deployment"
version: 0.1.0
status: Draft
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
(zero spend) and the KC-151 preferences. Lifecycle: **Draft**.

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

## Consequences

- One language across app and server.
- The JVM uses more memory than Go, which S5 bounds.
- No new database service.

## Revisit Criteria

S5 memory; VM contention (TD8); the budget review.
