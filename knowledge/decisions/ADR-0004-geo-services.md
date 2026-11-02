---
id: ADR-0004
title: "Geo Services: Search, Reverse Geocoding, Routing, Weather"
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

# ADR-0004: Geo Services: Search, Reverse Geocoding, Routing, Weather

Technology decision under Oriveda `OV-004`, with **Hybrid** stack freedom
(KC-151): options are compared, one is recommended, and the owner proxy
decides (ADR-0001). Constraints: CONST-CORE (Frozen), especially Law 7
(zero spend) and the KC-151 preferences. Lifecycle: **Draft**.

## Context

C7 (typeahead plus reverse geocoding), C8 (route preview for walk, cycle
and drive), and weather after the MVP. All calls go through BC10 (Law 10)
and obey R10 and Law 5. Evidence: KC-088–KC-091.

## Options Considered

**Search and typeahead**
- **Photon (public).** Supports typeahead, fair use, no SLA (KC-090).
- **Nominatim (public).** ≤1 request per second overall, and no
  autocomplete allowed (KC-088). Unfit for typeahead.
- **Self-hosted Photon.** A large index, beyond the VM's free RAM and
  disk (KC-086).

**Reverse geocoding:** Nominatim at ≤1 request per second, cached, versus
Photon reverse.

**Routing**
- **OSRM demo.** Car, foot and bike; non-commercial; ≤1 request per
  second; no SLA (KC-089).
- **The FOSSGIS Valhalla public instance.** Similar terms. Not verified in
  the evidence log.
- **Self-hosted OSRM or Valhalla.** Exceeds the VM budget (TD2).

**Weather (after the MVP):** Open-Meteo (non-commercial, CC-BY, KC-091).

## Decision

- **Photon** for search and typeahead.
- **Photon reverse** first for reverse geocoding, with **Nominatim** as
  the fallback at ≤1 request per second.
- **OSRM demo** for route preview.
- **Open-Meteo** for weather when it ships.
- All four sit behind BC10 adapters with:
  - a per-provider aggregate rate limit;
  - coarsened cache keys;
  - an identifying User-Agent;
  - no stored coordinates (Law 5).
- The client debounces typeahead (≥300 ms, ≥3 characters).

## Consequences

- Zero spend.
- Providers can be swapped server-side without an app release.
- Exposure is to volunteer services (TD1, TD2, TD11), mitigated by
  caching and graceful degradation. Offline search still works (C7).

## Revisit Criteria

Throttling or a policy change; any move toward monetisation (TD2, TD11);
the budget review (self-hosting is a rule-2 escalation).
