---
id: ADR-0009
title: "Hosting, Delivery and Operations"
version: 1.0.0
status: Frozen
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
(zero spend) and the KC-151 preferences. Lifecycle: **Frozen** v1.0.0 (2026-11-02), decided by the owner proxy
under KC-151 (EVD-045). Freezing does not authorise spikes or code
(KC-107).

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

### Amendments from owner-proxy round 1

- **The bootstrap file** carries only the API base URL plus the tile,
  glyph and DEM source endpoints, **never a whole style** (ADR-0003). The
  app treats it as an untrusted hint: normal TLS validation and the
  runtime boundary guard still apply.
- **Release upload:** Gradle Play Publisher (the Play Developer API,
  free). The Play service-account key lives only in GitHub encrypted
  secrets, with release-manager permissions only.
- **Dependency updates:** Renovate or Dependabot.
- **ACRA endpoint:** gets the same rate limits as BC10.

## Consequences

- Zero spend, a reproducible public CI, and self-owned crash data.

## Revisit Criteria

DuckDNS incidents (TD16); the budget review (a purchased domain, rule 2).

## Architecture Review (2026-11-02)

Checked against CONST-CORE and ARCH-SYSTEM (both Frozen), with no conflict. Laws 5, 7, 8 and 12 are satisfied.
Divergences: none (no code). Worked example under Oriveda `examples/`: not
applicable (Oriveda ADR-0002). Result: passes.
