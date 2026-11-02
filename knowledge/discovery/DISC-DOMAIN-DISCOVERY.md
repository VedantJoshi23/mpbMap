---
id: DISC-DOMAIN-DISCOVERY
title: "Discovery: Domain Discovery"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M1
category: Discovery
priority: High
depends_on:
  - DISC-FEATURE-INVENTORY
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-USER-JOURNEYS
  - DISC-DATA-MODEL
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - discovery
  - domain-discovery
risk: High
complexity: Medium
---

# DISC-DOMAIN-DISCOVERY: Domain Discovery

One of the ten M1 investigations (`OV-001`, Frozen), over [the evidence
log](evidence/README.md). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Suggestions are marked **[S]**. It replaces the earlier
reference-app inferences KC-050 and KC-051 with this project's own
contexts.

## Purpose

Propose bounded contexts, who owns what, and the domain events between
them. M3 (`OV-003`) declares the final contexts, and M5 writes one Domain
Specification per context.

## Observed Facts

- The alarm engine must stay independent of the UI, so Wear OS and Android
  Auto can reuse it (KC-060; C4).
- Location is collected only while the app is in use or a trip session is
  running (R5). Trip-session location can feed explored cells only under
  R18.
- Device-only data: Trip, Alarm, LocationFix and Journal. Synced data:
  Codex and explored cells (R8; DISC-DATA-MODEL).
- The backend hosts accounts, sync and a provider proxy (KC-081, KC-099).

## Interpretation

### Bounded contexts

All contexts are [S].

| # | Context | Owns | Kind | Runs on |
| --- | --- | --- | --- | --- |
| BC1 | **Trip & Alarms** | Trip, Alarm, pre-flight (R26), arrival logic (R1, R17), location loss (R3), interrupted-trip alerts (R25), stale trip (R24) | **Core.** The headline utility | Device; UI-independent module |
| BC2 | **Location** | Permission and disclosure state (R6), fix acquisition, accuracy, collection scope (R5), the rolling LocationFix log | Supporting. The only gateway to device location | Device |
| BC3 | **Map & Skins** | Skin catalogue, rendering, 2D fallback, attribution (R11) | **Core.** The concept | Device |
| BC4 | **Exploration** | ExploredCell, fog layer, Journal, local erasure (R21), R18 gating | Supporting | Device; cells sync |
| BC5 | **Codex** | CodexEntry, including parking | Supporting | Device; syncs |
| BC6 | **Places** | Online search, reverse geocoding, offline pack search index | Supporting | Device plus the gateway |
| BC7 | **Routing** | Route preview | Supporting | Device plus the gateway |
| BC8 | **Offline Maps** | OfflinePack lifecycle and size limits (R10) | Supporting | Device |
| BC9 | **Identity & Sync** | Account, sign-in and sign-out, sync protocol, deletion (R9, R19) | Generic | Device plus backend |
| BC10 | **Provider Gateway** | Proxying, rate limits, caching and User-Agent for third-party APIs (R10) | Generic | Backend |

- No context owns "settings" as a whole. Each context owns its own
  settings: alarm defaults belong to BC1, reveal toggles to BC4, the
  default skin to BC3.
- **Ownership rule:** only BC2 talks to the platform's location APIs.
  BC1 and BC4 consume BC2's events. This makes R5 and R18 enforceable in
  one place.

### Domain events

This is the `OV-001` cross-cutting check (KC-051 was inference only).

| Event | Producer | Consumers |
| --- | --- | --- |
| LocationFixAcquired | BC2 | BC1 (trip distance); BC4 (in-use, or a trip under R18); BC3 (locate-me) |
| LocationAccessChanged (granted, precise, revoked, services off) | BC2 | BC1 (pre-flight; R25 if mid-trip); BC4 |
| TripStarted / TripEnded | BC1 | BC2 (open or close the trip collection scope); BC4 (R18 gating) |
| AlarmFired / AlarmDismissed / AlarmSnoozed | BC1 | BC1 (session lifetime, R2); notifications |
| LocationLostNearDestination | BC1 | notifications (R3) |
| TripInterrupted | BC1 (boot receiver, watchdog) | notifications (R25) |
| SkinChanged | BC3 | BC6 (result typography); BC1 (quest-marker style) |
| CodexEntryChanged | BC5 | BC9 (sync); BC6 (offline index) |
| ExploredCellsAdded / ExplorationCleared | BC4 | BC9 (union sync; `clearedAt` epoch) |
| OfflinePackInstalled / OfflinePackRemoved | BC8 | BC3 (offline styles); BC6 (offline index) |
| SignedIn / SignedOut / AccountDeleted | BC9 | BC5, BC4 (merge, keep-or-clear); backend purge (R19) |

### Context relationships

- BC2 → BC1 and BC4: customer–supplier, with BC2 as the supplier.
- BC6 and BC7 → BC10: an anticorruption layer, so the app never sees a
  provider's raw API. Providers can be swapped without an app release
  (KC-099).
- BC9 (device) → backend: an open-host sync API.

## Hidden Assumptions

- Ten contexts are not too many for one developer, because many of them
  are thin. M3 may merge BC6 with BC7 and BC8 with BC3 into modules
  without merging their *ownership*.
- Events stay in-process on the device: an internal event bus, not a
  message broker.

## Strengths

- R5 and R18 are enforced at a single gateway (BC2).
- The core contexts (BC1, BC3) are isolated. BC1 has no UI dependency,
  which serves KC-060.

## Weaknesses

- BC1 depends on platform services (boot, alarms, foreground services)
  that are hard to unit-test. Fakes behind interfaces will be needed.
- The event catalogue is designed, not observed. It will need revising
  once M6 features are written.

## Questions

1. Should Map & Skins and Offline Maps be one context? Offline packs exist
   only to feed the map. [S] Keep them separate: pack size, limits and
   provider policy (R10) are a distinct concern.

## Recommendations

- **Keep:** BC1 as the UI-independent core; BC2 as the single location
  gateway.
- **Improve:** a provider gateway as the anticorruption layer.
- **Remove:** a shared "settings" context.

## Confidence Level

**Overall: Inference (65).** The contexts and events are derived from
Frozen rules, but they are a design that M3 and M5 must still prove.
