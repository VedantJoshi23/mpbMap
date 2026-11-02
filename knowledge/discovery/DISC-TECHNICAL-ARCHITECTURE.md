---
id: DISC-TECHNICAL-ARCHITECTURE
title: "Discovery: Technical Architecture"
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
  - DISC-BUSINESS-VISION
  - DISC-FEATURE-INVENTORY
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-DATA-MODEL
  - DISC-DOMAIN-DISCOVERY
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - discovery
  - technical-architecture
risk: High
complexity: High
---

# DISC-TECHNICAL-ARCHITECTURE: Technical Architecture

One of the ten M1 investigations (`OV-001`, Frozen), over [the evidence
log](evidence/README.md). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Suggestions are marked **[S]**.

This is Discovery, not M3. It records the constraints and evidence, and
the agent's **candidate** stack. The binding choices are made in M3
(`OV-003`, `OV-004`), after the spikes listed below.

## Purpose

Establish what the architecture must satisfy and what the evidence makes
feasible, so that M3 starts from tested constraints, not preferences.

## Observed Facts

**Constraints**
- Zero spend until 300+ downloads (KC-068). Free, no ads (KC-075). Play
  distribution (KC-058).
- Phone first, extendable to Wear OS and Android Auto (KC-060). The alarm
  engine is UI-independent (C4).
- Backend availability is best effort; the core works offline (KC-104,
  R16).
- Maps must feel as smooth as Google or Apple Maps (KC-070). Accessibility
  is in scope (KC-103).

**Host**
- The VM is an Oracle A1.Flex (ARM64), with 2 OCPUs and 12 GB RAM, of
  which about 2.4 GB is free. 31 GB of disk is free. It runs Ubuntu 24.04,
  Docker, PostgreSQL and nginx (KC-085), and is shared with other
  workloads (KC-086).
- nginx already terminates Let's Encrypt TLS for the owner's existing
  domains (KC-124).

**Data providers** (policies: R10)
- Tiles: OpenFreeMap, free with no key, OpenMapTiles schema, offline packs
  of 50 MB / 3,000 tiles (KC-087).
- Search: Photon for typeahead (KC-090); Nominatim at ≤1 request per
  second (KC-088).
- Routing: the OSRM demo, non-commercial and ≤1 request per second
  (KC-089).
- Weather (after the MVP): Open-Meteo, non-commercial (KC-091).
- Region packs: Protomaps PMTiles extracts, planet about 120 GB (KC-094).
- Elevation: AWS Terrain Tiles, with no account needed (KC-122).

**Renderer**
- MapLibre Native Android supports hillshade from raster-dem sources.
  **3D terrain is not yet supported on Android** (KC-123).

## Interpretation

### Feasibility consequences

- **`terrain-sepia` hillshade: feasible** (KC-122 plus hillshade, KC-123).
  This settles KC-111 for 2D hillshade, subject to a spike.
- **`topo-mono` (3D terrain): not feasible today** (KC-123). Under F3
  (KC-071) it is **excluded** until MapLibre Android ships terrain. It
  goes on a revisit list, not into the MVP.
- **3D buildings** (`vertical-night`, `cube-3d`): feasible through
  fill-extrusion on the OpenMapTiles building layer (KC-084). Smoothness
  on low-end devices is a spike, with the 2D fallback required by C1.

### Candidate stack

All choices are [S], to be decided in M3.

| Layer | Candidate | Why |
| --- | --- | --- |
| App language and UI | Kotlin, Jetpack Compose | Platform default, and the same language is reusable for Wear OS and Auto |
| Map | MapLibre Native Android | Free, open, vector, fill-extrusion, hillshade, offline regions, attribution control |
| Location | Fused location provider (Play services) | Built-in GPS→network fallback (R3); free |
| Device store | Room (SQLite); a separate DP-storage database for Trip and Alarm | R25 before-unlock reads (DISC-DATA-MODEL) |
| Cells | H3 (Java binding) | Resolution 9 cells (DISC-DATA-MODEL) |
| Background work | WorkManager (downloads; possibly the watchdog) | Avoids the time cap and extra declaration of a data-sync FGS (J7) |
| Auth | Sign in with Google (Credential Manager); the backend verifies the ID token | Free, no email infrastructure, low friction for reviewers |
| Backend | A small JVM (Ktor) or Go service in Docker, behind the existing nginx | Ktor shares the app's language (portfolio coherence); Go has a smaller footprint (about 2.4 GB of RAM is free). M3 decides |
| Server database | A dedicated DB and role on the existing PostgreSQL | No new service, zero spend |
| Provider gateway | Inside the backend: rate limits, cache, User-Agent | R10, KC-099 |
| Crash reports | ACRA posting to the backend, or none | No third-party tracking SDK (R20) |
| Static pages (privacy policy, deletion request) | GitHub Pages | Free; KC-093, KC-097 |
| CI | GitHub Actions (free for public repos) | KC-112 |

### Spikes required before M3 freezes (from the Frozen investigations)

1. **S1 Rendering:** frame rate while panning and zooming for one 2D skin
   and one 3D skin on a low-end device. Verifies the 2D fallback (KC-070,
   C1).
2. **S2 Hillshade:** Terrain Tiles encoding and URL pattern, attribution
   text, and MapLibre Android hillshade output (KC-122, KC-123).
3. **S3 Trip reliability:**
   - foreground-service survival overnight;
   - direct-boot alert before unlock;
   - the inexact watchdog;
   - full-screen-intent behaviour;
   on at least two OEMs (R25, R26).
4. **S4 Offline packs:** the MapLibre offline API against OpenFreeMap's
   per-download limits, plus offline search over pack names (C7, C9).
5. **S5 Backend footprint:** memory of the chosen backend on the shared VM
   (KC-086).

### Open dependency

KC-117 (map boundaries and DPDP) blocks the M3 tile-provider decision.
Option (iii), no country boundary lines, is a style-level change that
works with OpenFreeMap. Option (ii) may need custom tiles.

## Hidden Assumptions

- The API can live on a subdomain of a domain the owner already has, at
  no cost (KC-124). Which domain to use is the owner's choice.
- The free providers' current policies hold through launch (R10).
- Fused location's network fallback works without mobile data in the
  places where trains lose GPS. This is plausible but unverified (S3).

## Strengths

- Every layer has a free candidate that fits zero spend.
- Feasibility claims are tied to evidence (KC-122, KC-123), and each
  remaining unknown has a named spike.

## Weaknesses

- Heavy dependence on volunteer-run free services with no SLA (KC-087,
  KC-089, KC-090).
- The shared VM has little free RAM, which limits backend choices.
- `topo-mono` is excluded for now, so the skin roster is down to 8
  feasible inspirations.

## Questions

1. **For the owner (ADR-0001 rule 4, public identity):** which existing
   domain should host the API and the privacy/deletion pages, given the
   privacy-policy URL is shown on Play? Or should the pages use a free
   GitHub Pages URL?
2. *(→ M3)* Ktor vs Go for the backend, after S5.

## Recommendations

- **Keep:** the zero-spend provider set behind the gateway; MapLibre;
  Room with a DP database.
- **Improve:** run S1–S5 before M3 freezes.
- **Remove:** `topo-mono` from the MVP and the near-term roster (KC-123).

## Confidence Level

**Overall: Inference (65).** Provider and renderer facts are at 75–90.
The candidate stack is a design pending spikes S1–S5.
