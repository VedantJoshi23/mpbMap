---
id: DISC-TECHNICAL-ARCHITECTURE
title: "Discovery: Technical Architecture"
version: 1.1.0
status: Frozen
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
log](evidence/README.md). Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-129). Draft v0.1.0, then
owner-proxy rounds 1–3 (ADR-0001), then Architecture Review, then Freeze.
KC-117 and KC-128 are open dependencies for M3. Suggestions are marked **[S]**.

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
| Device store | Room (SQLite) in CE storage, plus a minimal DP store holding **TripBootRecord only** | R25 before-unlock alert (DISC-DATA-MODEL v1.0.0) |
| Cells | H3 (Java binding); fallback: a pure-JVM hex-grid implementation if the binding lacks Android ABI native libraries (checked in S5) | Resolution 9 cells (DISC-DATA-MODEL) |
| Background work | WorkManager for offline-pack downloads | Avoids the time cap and extra declaration of a data-sync FGS (J7) |
| Trip watchdog | AlarmManager inexact **allow-while-idle** alarm, with no exact-alarm permission | Matches Frozen R25 and J3 |
| Auth | Sign in with Google (Credential Manager); the backend verifies the ID token | Free, no email infrastructure, low friction for reviewers |
| Backend | A small JVM (Ktor) or Go service in Docker, behind the existing nginx | Ktor shares the app's language (portfolio coherence); Go has a smaller footprint (about 2.4 GB of RAM is free). M3 decides |
| Server database | A dedicated DB and role on the existing PostgreSQL | No new service, zero spend |
| Provider gateway | Inside the backend: rate limits, cache, User-Agent | R10, KC-099 |
| Crash reports | ACRA posting to the backend, or none. It is configured to exclude logcat and any location fields | No third-party tracking SDK; no location in reports (R20) |
| Static pages (privacy policy, deletion request) | GitHub Pages; the deletion-request form posts to a backend endpoint that verifies the requester (e.g. a sign-in with the same Google account). Fallback when the backend is down (KC-104): a contact address on the page, with those requests handled by the same procedure | Free; KC-093 needs the request actually processed, not just shown |
| CI | GitHub Actions (free for public repos) | KC-112 |

### Architecture constraints

These are added in round 1.

- **Gateway abuse protection.** BC10 is a public endpoint on a shared VM.
  It needs:
  - per-client and global rate limits;
  - request-size and request-count caps;
  - an app-identity check.

  Without them, anyone can use up the shared ≤1 request/second Nominatim
  and OSRM budget, get our User-Agent banned (R10), or load the owner's
  other workloads (KC-086). Candidate identity check: Play Integrity. It
  is a **commercial free tier with quotas**, so it is flagged under the
  zero-spend rule, and a paid tier is never adopted without escalation
  (ADR-0001 rule 2). The alternative is a signed app token plus
  rate-limit-only protection. A token embedded in the APK can be
  extracted, so it only deters casual misuse and is weaker than Play
  Integrity. M3 decides.
- **SDK levels.** [S] Candidate minSdk is 26 (Android 8.0). targetSdk must
  meet Play's current requirement for new apps (R12), believed to be
  API 35 or 36 by late 2026, to be checked at M3. targetSdk decides which
  foreground-service, full-screen-intent and exact-alarm rules apply.
- **Backups.** [S] A nightly `pg_dump` of the project database to local
  disk on the VM, with an encrypted copy in a free off-VM store. A
  candidate is Oracle Always Free object storage, which is a
  **commercial free tier**: it is flagged, and any paid plan is an
  ADR-0001 rule-2 escalation. Backups are kept for 7 days. The privacy
  policy discloses the worst case under R19: deleted data can persist for
  up to **37 days** (30-day purge plus 7 days of backups).

### Spikes required before M3 freezes (from the Frozen investigations)

1. **S1 Rendering:** frame rate while panning and zooming for one 2D skin
   and one 3D skin on a low-end device. Test both MapLibre Android 13's
   Vulkan default and the OpenGL ES variant (KC-123). Verifies the 2D
   fallback (KC-070, C1).
2. **S2 Hillshade:** Terrain Tiles encoding and URL pattern, attribution
   text, and MapLibre Android hillshade output (KC-122, KC-123).
3. **S3 Trip reliability:**
   - foreground-service survival overnight;
   - direct-boot alert before unlock;
   - the inexact watchdog;
   - full-screen-intent behaviour;
   - force-stop and OEM "deep clean" behaviour: a force-stop cancels the
     app's alarms and blocks its broadcasts until the app is opened again;
   - whether the stopped state survives a reboot and so suppresses
     `LOCKED_BOOT_COMPLETED`;
   - Fused location's network fallback with no mobile data, on a train;

   on at least two OEMs (R25, R26). Force-stop results are recorded as a
   known limit under TD6.

   **Acceptance criterion for the watchdog:** detection delay of at most
   10 minutes while a trip is armed, under Doze, measured for the
   AlarmManager inexact allow-while-idle alarm named in R25 and J3. The
   bound follows from how often the platform lets allow-while-idle alarms
   fire in Doze. A late detection still produces a loud R25 alert: late,
   not silent. If the spike forces a different mechanism, that is a
   revision to Frozen R25/J3 and is reviewed as a draft first.
4. **S4 Offline packs:** the MapLibre offline API against OpenFreeMap's
   per-download limits, plus offline search over pack names (C7, C9).
   Include elevation tiles in the pack budget, so `terrain-sepia` keeps
   its hillshade offline.
5. **S5 Backend footprint:** memory of the chosen backend on the shared VM
   (KC-086). Also check that the H3 binding ships Android ABI native
   libraries.

### Open dependency

KC-117 (map boundaries and DPDP) blocks the M3 tile-provider decision.
Option (iii), no country boundary lines, is a style-level change that
works with OpenFreeMap. Option (ii) may need custom tiles.

## Hidden Assumptions

- *(Superseded by KC-136 in v1.1.0)* There is no owner domain. The API
  uses a free DDNS subdomain, plus a bootstrap file on GitHub Pages.
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

1. *(Resolved in v1.1.0, KC-136: the owner has no domain)*
   - **Static pages** (privacy policy, deletion request) are served from
     the owner's GitHub Pages site (`vedantjoshi23.github.io/...`).
   - **API host:** a free dynamic-DNS subdomain (candidate: DuckDNS)
     pointing at the VM, with a Let's Encrypt certificate on the existing
     nginx. This is **flagged as a volunteer or commercial free tier**.
     Buying a domain is an ADR-0001 rule-2 escalation.
   - **Bootstrap file:** the app reads its API base URL from a small static
     file on the GitHub Pages site, with a built-in default as the
     fallback. A DDNS failure or a hostname move then needs no app
     release. The file is unauthenticated, so it is a hostname hint only:
     certificates are validated normally, and nothing sensitive goes in
     it.
   - **Public hostname:** the API hostname is the **one sanctioned public
     hostname**, because it necessarily ships in the app. The hygiene rule
     (KC-124, DISC-REPO-STRUCTURE) still keeps the owner's *other*
     hostnames out of the repo. Rate limits and abuse protection (BC10)
     apply whatever the hostname.
2. *(→ M3)* Ktor vs Go for the backend, after S5.

## Recommendations

- **Keep:** the zero-spend provider set behind the gateway; MapLibre;
  Room with a DP database.
- **Improve:** run S1–S5 before M3 freezes.
- **Remove:** `topo-mono` from the MVP and the near-term roster (KC-123).

## Architecture Review (2026-11-02)

- **Against the Frozen DISC- investigations:**
  - The stack candidates honour zero spend (KC-068).
  - DP storage holds only TripBootRecord (DISC-DATA-MODEL).
  - The watchdog mechanism matches R25 and J3.
  - The gateway protects R10.
  - Commercial free tiers are flagged.
  - `topo-mono` is excluded under F3 (KC-071, KC-123).
  - Consistent.
- **Citations:** all current.
- **Divergences:** none (no code).
- **Result:** passes.

## Revision History

- **v1.1.0 (2026-11-02):** Q1 resolved for the no-domain case (KC-136):
  GitHub Pages for static pages and the bootstrap file, a flagged free
  DDNS subdomain for the API, and the sanctioned public API hostname.
  Approved by the owner proxy (KC-141).

\1

**Overall: Inference (65).** Provider and renderer facts are at 75–90.
The candidate stack is a design pending spikes S1–S5.
