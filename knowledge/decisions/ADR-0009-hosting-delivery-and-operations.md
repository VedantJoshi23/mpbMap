---
id: ADR-0009
title: "Hosting, Delivery and Operations"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M3
category: Decision
priority: Medium
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
complexity: Low
---

# ADR-0009: Hosting, Delivery and Operations

Technology decision under Oriveda `OV-004`, with **Hybrid** stack freedom
(KC-151): options are compared, one is recommended, and the owner proxy
decides (ADR-0001). Constraints: CONST-CORE (Frozen), especially Law 7
(zero spend) and the KC-151 preferences. Lifecycle: **Draft**.

## Context

There is no domain (KC-136). The other inputs:
- GitHub Pages hosts the static pages and the bootstrap file;
- the API uses a free DDNS hostname (TD16);
- the repo is public;
- zero spend;
- crash reports must not contain location (Law 8);
- Play distribution (KC-058).

## Options Considered

**DDNS**
- **DuckDNS.** Free, long-running, token-based updates.
- **No-IP free.** Monthly confirmation required.
- **A Cloudflare Tunnel quick URL.** The URL is random and changes.

**CI**
- **GitHub Actions.** Free for public repos.
- **Self-hosted runner on the VM.** Uses the shared VM's resources.

**Crash reporting**
- **ACRA to the backend.** Self-owned data.
- **Firebase Crashlytics.** A third-party SDK (Law 8 risk).
- **None.**

**Release:** Play Console internal testing, then production, uploaded
from CI with Play App Signing.

## Decision

- **DuckDNS** API hostname (flagged free tier), with Let's Encrypt on the
  existing nginx.
- **The bootstrap file** on GitHub Pages carries the API base URL and the
  style URL (ADR-0003).
- **GitHub Actions** for CI:
  - build;
  - unit tests;
  - the ARCH-SYSTEM §4 dependency rules;
  - the style lint (no boundary layer);
  - secret scanning;
  - Android Lint and accessibility checks.
- **ACRA** posting to the backend, configured without logcat or location.
- **Play App Signing**, with the upload key kept out of the repo
  (DISC-REPO-STRUCTURE hygiene).

## Consequences

- Zero spend, a reproducible public CI, and self-owned crash data.

## Revisit Criteria

DuckDNS incidents (TD16); the budget review (a purchased domain, rule 2).
