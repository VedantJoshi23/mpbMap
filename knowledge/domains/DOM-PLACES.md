---
id: DOM-PLACES
title: "Domain: Places"
version: 0.2.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M5
category: Domains
priority: Medium
depends_on:
  - ARCH-SYSTEM
  - CONST-CORE
required_by: []
related_documents:
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
related_domains: [DOM-KERNEL, DOM-MAP, DOM-CODEX, DOM-OFFLINE]
related_features: []
related_decisions: [ADR-0004]
tags:
  - domain
risk: Medium
complexity: Medium
---

# Domain: Places (BC6)

**Tier:** Full

Authored under Oriveda `OV-006` (Frozen) through `PRM-DOMAIN`.
Lifecycle: **Draft** v0.2.0, the revision after owner-proxy round 1
(ADR-0001); it waits for the ARCH-SYSTEM v1.1.0 draft. It binds to
CONST-CORE and ARCH-SYSTEM (both Frozen). Ownership is restated from
ARCH-SYSTEM §1, not re-derived.

## 1. Overview

Finding destinations: online typeahead and reverse geocoding through the gateway, and an offline index over pack names and the Codex.

## 2. Ownership

### Owns

- The online search **and reverse-geocoding** client (through the gateway HTTP API)
- The `CityResolver` adapter for DOM-EXPLORATION's port (wired in `app/`)
- The offline search index (pack place names plus Codex entries)

### Explicitly Does NOT Own

- Provider HTTP, policy and caching (DOM-GATEWAY)
- Pack files (DOM-OFFLINE)

## 3. Invariants

### Invariant 1

> The client never calls a provider directly. All online calls go to the gateway.

**Source:** Law 10; ADR-0004

### Invariant 2

> Typeahead is debounced (≥300 ms, ≥3 characters), and the location bias is coarsened.

**Source:** ADR-0004; Law 5

### Invariant 3

> When the gateway is unavailable or throttled, search falls back to the offline index.

**Source:** ADR-0004; Law 9

## 4. API Surface

- `PlacesApi.search(query, bias)`, `reverse(latLng)`

## 5. Events

### Publishes

- None.

### Consumes

- SkinChanged (DOM-MAP), for result typography in `ui/`
- CodexEntryChanged (DOM-CODEX)
- OfflinePackInstalled / OfflinePackRemoved (DOM-OFFLINE)

## 6. Data Ownership

- Room `places.db` (CE): offline index

## 7. Dependencies

### Allowed

- `DOM-KERNEL`
- `DOM-MAP`
- `DOM-CODEX`
- `DOM-OFFLINE`

### Forbidden

- `DOM-TRIP`
- `DOM-LOCATION`
- `DOM-EXPLORATION`
- `DOM-ACCOUNT`
- `DOM-ROUTING`
- DOM-GATEWAY is reached only over HTTP (STD-API), never as a code dependency.

### Machine-checkable boundary

```yaml oriveda-boundary
domain: DOM-PLACES
paths:
  - "android/feature/places/**"
allowed_dependencies:
  - "DOM-KERNEL"
  - "DOM-MAP"
  - "DOM-CODEX"
  - "DOM-OFFLINE"
forbidden_dependencies:
  - "DOM-TRIP"
  - "DOM-LOCATION"
  - "DOM-EXPLORATION"
  - "DOM-ACCOUNT"
  - "DOM-ROUTING"
```

## 8. Edge Cases & Validations

- A provider returns 429: show cached and offline results.
- An empty query, or fewer than 3 characters: no request.

## 9. Open Questions

- None.
