---
id: DISC-FEATURE-INVENTORY
title: "Discovery: Feature Inventory"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M1
category: Discovery
priority: Critical
depends_on:
  - DISC-BUSINESS-VISION
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - discovery
  - feature-inventory
risk: Medium
complexity: Medium
---

# DISC-FEATURE-INVENTORY: Feature Inventory

One of the ten M1 investigations (`OV-001`, Frozen), over [the evidence
log](evidence/README.md). It binds to DISC-BUSINESS-VISION (Frozen).
Lifecycle: **Draft**, under review by the owner proxy (ADR-0001).

The project is greenfield (KC-007), so this investigation records the
capabilities the project *will* have: what the owner stated, plus the
agent's suggestions, each marked. The reference app's inventory
(KC-012, KC-029–031, KC-039–040) is context only (KC-063).

## Purpose

Fix the capability list, and which capabilities make the MVP, so that the
user journeys, data model, domains and Constitution all work from the
same inventory.

## Observed Facts

Owner-stated capabilities (fact, 95–100):

| # | Capability | Source |
| --- | --- | --- |
| F1 | Real-world map reskinned in game-inspired styles; switch skins live | KC-056 |
| F2 | Skins inspired by ten games, each for one geographic trait | KC-083 |
| F3 | 3D inspirations only where 3D rendering is feasible; otherwise dropped | KC-071 |
| F4 | Explore-to-reveal: an opt-in toggle on every skin; off = whole map visible | KC-072 |
| F5 | Arrival alarm: rings by distance to destination; the user sets the distance; multiple alarms | KC-079 |
| F6 | The alarm must not depend on the route taken (the C-vs-D case) | KC-080 |
| F7 | Saved parking spots | KC-074 |
| F8 | Marked locations named in gaming terminology | KC-074 |
| F9 | Offline maps, comparable to Google Maps | KC-074, KC-076 |
| F10 | Accessibility options | KC-103 |
| F11 | Optional accounts with a guest mode; syncs on sign-in | KC-102 |
| F12 | Record of visited and explored places (GeoJSON locally or server DB) | KC-073 |
| F13 | Location logging, scoped to in-use or active sessions | KC-075, KC-101 |
| — | Performance: feels like Google or Apple Maps | KC-070 |
| — | English only | KC-076 |

Obligations that follow from these capabilities, and so are features too
(fact/inference, 80–90): in-app and web account deletion (KC-093); a
prominent location disclosure before the permission request (KC-092);
map-data attribution (KC-087, KC-091); a privacy policy (KC-097); and
credit to MapShifter (KC-003).

## Interpretation

The agent's suggestions are marked **[S]**. Each needs proxy approval.

### Capability catalogue

**C1. Skinned map (F1, F3)**
- MapLibre-rendered vector map. Pan, zoom, rotate and tilt; compass;
  locate-me.
- [S] Skin switching through a skin picker sheet, plus a horizontal swipe
  on a skin strip. The switch keeps camera, alarms and route intact.
- [S] Each skin is a bundle: map style, fonts, POI icon set, location
  marker, UI accent colours, and a "3D: yes/no" flag. That makes skins
  data, not code.

**C2. Skin collection (F2, F3, KC-078)**
[S] Every inspiration is assessed against F3. The names are working
codenames only, because final public names are identity and escalate
under ADR-0001 rule 4.

| Inspiration (internal reference only) | Trait | Codename | Rendering | Feasibility |
| --- | --- | --- | --- | --- |
| RDR2 | natural geography | Frontier | 2D, sepia, terrain hillshade | Feasible (hillshade: spike) |
| Cyberpunk 2077 | vertical urban | Neon Sprawl | **3D** building extrusion, night neon | Feasible (KC-084) |
| Zelda: BOTW | exploration geography | Wildlands | 2D painterly; pairs with F4 | Feasible |
| GTA San Andreas | regional diversity | Sunset State | 2D, bold land-use colour regions | Feasible |
| Death Stranding | terrain and infrastructure | Courier | **3D** terrain plus contour lines, monochrome roads | Spike needed: free DEM source |
| Subnautica | 3D underwater | (none) | needs bathymetry | **Excluded** under F3 unless a spike finds free data (KC-084) |
| AC Odyssey | archipelago | Aegean | 2D, water-dominant parchment | Feasible |
| Minecraft | procedural world | Blockworld | **3D** blocky extrusion, pixel textures | Feasible (KC-084) |
| Hollow Knight | underground topology | Hollow | 2D ink map; tunnels and metro emphasised | Feasible as 2D (its map is 2D, so F3 does not apply) |
| PUBG | tactical geography | Drop Zone | 2D grid overlay, tactical palette | Feasible |

[S] **MVP ships 4 skins:** Frontier (2D classic), Neon Sprawl (3D night),
Blockworld (3D playful) and Drop Zone (tactical grid). Together they prove
both rendering modes and are visibly varied. The rest come after launch.

**C3. Explore-to-reveal (F4, F12)**
- Off by default. The toggle is global and works on every skin.
- [S] Explored area is stored as a set of fixed-size hexagonal cells
  (around 150 m) visited while logging is allowed (F13). It renders as a
  fog layer above the skin. Cells, not raw tracks, keep storage small and
  sync cheap.

**C4. Arrival alarm (F5, F6). The headline utility.**
- An alarm is a destination plus a trigger radius (default 10 km,
  editable). It fires on straight-line distance from the device to the
  destination, so it is route-agnostic and works offline (KC-095).
- Multiple alarms per trip, e.g. "10 km: wake up" and "2 km: get ready".
- [S] Optional *checkpoint* alarms (the owner's "C") take a set of points
  and fire on **any** of them (C or D). They are labelled
  "route-dependent".
- [S] Alarm-clock behaviour: full-screen ring, alarm audio stream,
  vibration, snooze, dismiss. It runs as a user-started foreground
  service with a persistent notification that shows the live distance,
  and it stops on dismissal or arrival (KC-096).
- [S] Battery-adaptive GPS sampling: sparse when far, dense near the
  radius.
- [S] Safety net: if no GPS fix arrives for a set time while the
  estimated distance is near the radius, notify the user rather than
  stay silent.
- [S] Game terminology: an active alarm shows as a "Quest" with an
  objective marker.

**C5. Saved parking (F7)**
- One tap saves "parked here" with time, an optional note and an optional
  meter timer. The spot is shown with a distinct marker and a
  "Return to vehicle" action.

**C6. Marked locations, the "Codex" (F8)**
[S] Generic gaming terms, none franchise-owned:

| Term | Meaning |
| --- | --- |
| Waypoint | a generic pin |
| Quest Marker | a planned destination; alarms target these |
| Safehouse | home or work |
| Fast Travel | favourites with one-tap access |
| Merchant | a shop |
| Inn | a stay |
| Stable | parking (C5's marker) |
| Point of Interest | anything else |

Users can rename any of them.

**C7. Place search** [S, implied by C4/C6: destinations must be found]
- Typeahead search and reverse geocoding through the VM proxy (KC-099).
  Results are styled in the skin's typography.

**C8. Routing** [S]
- MVP: a route *preview* (line, distance, ETA) for walk, cycle and drive.
- After the MVP: turn-by-turn voice navigation. This was the reference
  app's most fragile area (KC-035). It is deferred so the MVP stays
  "correctly working" (KC-069).

**C9. Offline maps (F9)**
- Download a named area (city-sized packs within provider limits,
  KC-087). Packs work with every skin. Manage and delete packs. Search
  and routing are online-only. Alarms and saved places work offline
  (KC-104).

**C10. Accessibility (F10)** [S, specifics]
- TalkBack labels on all controls and markers; system font scaling;
  markers distinguishable by shape and label, not colour alone; a
  reduced-motion mode (no animated skin transitions); one high-contrast
  skin variant.

**C11. Accounts and sync (F11)**
- Guest by default. Optional sign-in syncs saved places, parking, alarms
  and explored cells. In-app account deletion plus a web deletion page
  (KC-093).

**C12. Journal (F12, F13)** [S]
- Visited places and explored-cell count per city. Raw location is logged
  only within F13's scope, kept on-device by default, and synced only
  when signed in.
- After the MVP: achievements and streaks.

**C13. Compliance and credit surfaces**
- A location prominent-disclosure screen; an About screen with OSM,
  OpenFreeMap, Open-Meteo and other attributions and "Concept inspired by
  MapShifter"; a privacy policy link.

**C14. Settings**
- Units (km default), default skin, reveal toggle, alarm defaults
  (radius, sound, vibration), offline packs, account.

### Out of scope (stated or deferred)

Wear OS and Android Auto (later, KC-060); localisation (KC-076);
turn-by-turn voice (after the MVP); widgets [S: after the MVP]; weather
chip [S: after the MVP, Open-Meteo]; achievements [S: after the MVP].

### MVP slice [S]

C1, C2 (4 skins), C3, C4, C5, C6, C7, C8 (preview only), C9, C10, C11,
C12 (basic), C13, C14.

## Hidden Assumptions

- Hillshade and terrain need a free elevation-tile source. If none fits
  zero spend, Frontier drops hillshade and Courier is excluded under F3.
- Straight-line distance is a good enough proxy for "about to reach"
  (KC-095). On winding mountain roads, 10 km straight-line can be a much
  longer drive, which errs early, the safe side for waking up.
- Hexagonal cells of around 150 m reveal enough to feel like exploring
  without amounting to a precise location history.

## Strengths

- One headline utility with a clear, testable rule (C4), which matches
  the success definition (KC-069).
- Skins as data (C1), so new skins after launch need no code changes.
- Core features (C4, C5, C6, C9) work offline, as KC-104 requires.

## Weaknesses

- C4 is the hardest feature to get right on Android (Doze, OEM battery
  killers, alarm reliability) and the one most exposed to Play policy.
- 3D skins on low-end phones may miss KC-070's smoothness target. A
  2D fallback per skin may be needed.
- A large MVP for one developer, especially C11 sync.

## Questions

1. *(→ `technical-architecture`)* Which free elevation source, if any,
   supports hillshade and 3D terrain at zero spend?
2. *(→ `hidden-business-rules`)* Should an alarm still ring if the user
   is already inside the radius when they set it?
3. Should C11 sync be in the MVP, or could the MVP ship guest-only with
   sync in release 1.1, which would shrink the MVP considerably?

## Recommendations

- **Keep:** route-agnostic destination-radius alarms (C4), skins as data
  (C1), reveal as an opt-in global toggle (C3).
- **Improve:** checkpoint alarms fire on *any of* a set (C4), closing the
  owner's C-vs-D loophole for the cases where an intermediate point is
  genuinely wanted.
- **Remove:** Subnautica-inspired 3D (unless a spike proves otherwise);
  turn-by-turn from the MVP.

## Confidence Level

Owner-stated capabilities: **Fact (95–100)**. The skin feasibility table
and C-level design suggestions: **Inference (65)**, capped by KC-084
(rendering feasibility is unverified until the M3 spike).
