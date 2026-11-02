---
id: DISC-FEATURE-INVENTORY
title: "Discovery: Feature Inventory"
version: 1.0.1
status: Frozen
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
Lifecycle: **Frozen** v1.0.0 (2026-11-02). Draft v0.1.0, then owner-proxy
rounds 1–3 (ADR-0001), then Architecture Review, then Freeze (KC-109).

The project is greenfield (KC-007), so this investigation records the
capabilities the project *will* have: what the owner stated, plus the
agent's suggestions, marked **[S]**. The reference app's inventory
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

**Obligations that follow from these capabilities, and so are features
too** (fact/inference, 75–90):

- account deletion, in-app and web (KC-093);
- a prominent location disclosure before the permission prompt (KC-092);
- map-data attribution: OpenStreetMap ODbL and OpenFreeMap (KC-064,
  KC-087);
- a privacy policy, the Data safety form, a content rating and Play's
  target-API rules (KC-064, KC-097);
- credit to MapShifter (KC-003);
- the alarm's platform permissions and declarations (see C4).

## Interpretation

### Capability catalogue

**C1. Skinned map (F1, F3)**
- A vector map with pan, zoom, rotate and tilt, compass and locate-me.
  [S] Renderer candidate: MapLibre. The choice belongs to
  `technical-architecture`; KC-038 describes the reference app, not this
  project.
- [S] Skin switching through a skin picker sheet, plus a horizontal swipe
  on a skin strip. The switch keeps camera, alarms and route intact.
- [S] Each skin is a bundle: map style, fonts, POI icon set, location
  marker, UI accent colours, and a 3D flag. This makes skins mostly data.
  A new 2D skin needs no new code, though it still ships in an app release
  unless remote delivery is added later. 3D skins may need code.
- **Requirement:** every 3D skin has a 2D fallback, used automatically on
  devices that cannot hold KC-070's smoothness target.

**C2. Skin collection (F2, F3, KC-078)**
- **Rule:** codenames, skin art, textures and fonts are original or
  openly licensed. Nothing is taken from game assets, and no codename
  echoes a franchise or a MapShifter skin name (KC-057, KC-065, KC-043).
  Public skin names are identity, decided under ADR-0001 rule 4.
- [S] Each inspiration (internal design reference only) is assessed
  against F3:

| Inspiration | Trait | Codename | Rendering | Feasibility |
| --- | --- | --- | --- | --- |
| RDR2 | natural geography | `terrain-sepia` | 2D, sepia, terrain hillshade | Feasible; hillshade needs a free DEM (Q1) |
| Cyberpunk 2077 | vertical urban | `vertical-night` | **3D** building extrusion, night palette | Feasible (KC-084) |
| Zelda: BOTW | exploration geography | `painterly-explore` | 2D painterly; pairs with F4 | Feasible |
| GTA San Andreas | regional diversity | `regional-bold` | 2D, bold land-use colour regions | Feasible |
| Death Stranding | terrain and infrastructure | `topo-mono` | **3D** terrain plus contours, monochrome roads | Only if Q1 finds a free DEM; else dropped (F3) |
| Subnautica | 3D underwater | (none) | needs bathymetry | **Excluded** under F3 unless a spike finds free data (KC-084) |
| AC Odyssey | archipelago | `parchment-sea` | 2D, water-dominant parchment | Feasible |
| Minecraft | procedural world | `cube-3d` | **3D** blocky extrusion, original pixel textures | Feasible (KC-084) |
| Hollow Knight | underground topology | `ink-underground` | 2D ink map; tunnels and metro emphasised | Feasible as 2D (its map is 2D, so F3 does not apply) |
| PUBG | tactical geography | `tactical-grid` | 2D grid overlay, tactical palette | Feasible |

- [S] **MVP ships 4 skins:** `terrain-sepia` (2D classic),
  `vertical-night` (3D night), `cube-3d` (3D playful) and `tactical-grid`
  (tactical). They prove both rendering modes and are visibly varied. The
  rest come after launch.

**C3. Explore-to-reveal (F4, F12)**
- Off by default. The toggle is global and works on every skin.
- [S] Explored area is a set of fixed-size cells (about 150 m), drawn as
  a fog layer above the skin. Cells can be stored either as GeoJSON or in
  a server database, so the storage choice stays with `data-model`
  (KC-073). Cells are still location history: they are declared in Data
  safety and counted under KC-098.

**C4. Arrival alarm (F5, F6). The headline utility.**
- **Trigger.** An alarm is a destination plus a trigger radius. It fires
  on straight-line distance from the device to the destination, so it
  does not depend on the route (it closes C-vs-D, KC-080, KC-095) and it
  works offline.
- **Targets.** Any map point, search result or saved place can be a
  target. The user does not have to save it first.
- **Multiple alarms.** For example, "10 km: wake up" and "2 km: get
  ready".
- **Radius.** The user sets it. [S] The radius is required at setup, with
  a suggestion scaled to trip length (10 km is the owner's example,
  KC-079, and is wrong for walking).
- **Setting inside the radius.** If the user is already inside the
  radius, the app warns them and makes them choose in the dialog: shrink
  the radius, or arm anyway. Arming anyway fires on arrival, and the
  arrival distance is shown at setup. It never arms silently. (The proxy
  answered Q2; the rule goes to `hidden-business-rules`.)
- **Behaviour.** Alarm-clock style: the alarm audio stream, strong
  vibration, snooze and dismiss.
- **Rule: one trip session.** A single **user-started foreground
  service** holds every armed alarm for the trip, with a persistent
  notification showing live distance. Dismissing one alarm disarms only
  that alarm. The service runs until the last alarm has fired and been
  dismissed, the user ends the trip, or the user arrives. It must never
  stop while an alarm is still armed. Without background location, the
  service cannot be restarted from the background, so stopping early
  would silently lose later alarms.
- **Platform and Play compliance** (KC-069, KC-092, DISC-BUSINESS-VISION):
  - no `ACCESS_BACKGROUND_LOCATION`, by design: the foreground service is
    started while the app is in use;
  - `FOREGROUND_SERVICE_LOCATION`, plus the foreground-service-type
    declaration in Play Console;
  - the `POST_NOTIFICATIONS` runtime permission (Android 13+), requested
    before the first alarm is armed;
  - a full-screen ring needs `USE_FULL_SCREEN_INTENT`, which Play limits
    to alarm or calling apps and which needs a declaration. Fallback: a
    high-priority notification on the alarm stream if it is not granted.
- [S] Battery-adaptive GPS sampling: sparse when far from the
  destination, dense near the radius.
- [S] **Location-loss safety net:**
  1. When GPS is lost, fall back first to network or cell location, which
     often works without mobile data.
  2. If no fix of any kind arrives, estimate the distance as the last
     fix's distance minus speed × elapsed time. Speed is capped by travel
     mode: walk, cycle, road or rail.
  3. If the estimate crosses the radius, raise a separate **"location
     lost near destination"** alert. It looks and sounds distinct from the
     arrival ring, so the user knows the position is uncertain. It is
     never presented as an arrival. A real fix inside the radius still
     fires the arrival alarm normally.
- [S] Game terminology: an active alarm shows as a "Quest" with an
  objective marker.
- **Extensibility:** the alarm engine is independent of the UI, so Wear OS
  and Android Auto can be added later (KC-060). Bridged alarm
  notifications reach a paired watch at no extra cost.

**C5. Saved parking (F7)**
- One tap saves "parked here" with the time and an optional note. The spot
  gets a distinct marker and a "Return to vehicle" action.
- A meter timer is deferred until after the MVP. It needs exact-alarm
  permissions, which Play restricts.

**C6. Marked locations, the "Codex" (F8)**
- [S] Generic gaming terms, none franchise-owned. Users can rename
  individual places; the categories are fixed.

| Term | Meaning | Usage basis |
| --- | --- | --- |
| Waypoint | a generic pin | the standard term for a user-placed map marker across open-world games |
| Quest Marker | a planned destination | the objective marker in quest-driven games |
| Safehouse | home or work | the player's secure base in open-world and stealth games |
| Fast Travel Point | a favourite with one-tap access | a discovered location the player can jump to |
| Merchant | a shop | the trader NPC or shop in RPGs |
| Inn | a stay | the rest location in RPGs |
| Garage | parking (C5's marker) | where vehicles are stored in driving games |
| Point of Interest | anything else | the generic undiscovered marker |

**C7. Place search** [S, implied by C4/C6: destinations must be found]
- Online: typeahead and reverse geocoding through the VM proxy (KC-099).
- Offline: search over saved places plus the place and POI names inside
  downloaded packs.
- Results are styled in the skin's typography.

**C8. Routing** [S]
- MVP: an online route *preview* (line, distance, ETA) for walk, cycle and
  drive.
- After the MVP: turn-by-turn voice navigation. This was the reference
  app's most fragile area (KC-035). It is deferred so the MVP stays
  "correctly working" (KC-069).

**C9. Offline maps (F9: comparable to Google Maps)**
- MVP:
  - Download a named area, then manage and delete packs.
  - Packs work with every skin.
  - Offline search works as in C7.
  - Alarms, parking and saved places work offline (KC-104).
- Pack size: **the MVP ships city-sized packs only**, within OpenFreeMap's
  per-download limit of 50 MB / 3,000 tiles (KC-087). Region-sized packs
  served from self-hosted PMTiles on the VM come after the MVP (KC-094,
  KC-099).
- **Known gap against KC-074:** offline routing is not in the MVP. It is
  planned for after the MVP (needs a routing engine on the device or
  routing data in the packs). This is a recorded gap, not a permanent
  limit.

**C10. Accessibility (F10)** [S, specifics]
- TalkBack labels on all controls and markers.
- System font scaling.
- Markers distinguishable by shape and label, not colour alone.
- Dark UI (KC-024 lists it for the reference app).
- Reduced-motion mode: no animated skin transitions.
- One high-contrast skin variant.
- Alarm accessibility:
  - perceivable without sound: a strong vibration pattern and a
    full-screen or heads-up visual;
  - dismissable through TalkBack.

**C11. Accounts and sync (F11). Stays in the MVP** (KC-075, KC-066)
- Guest by default; signing in is optional.
- MVP sync covers saved places, parking and explored cells, with
  last-write-wins. Alarms, which last one trip, do not sync. Raw location
  logs stay on the device in the MVP.
- In-app account deletion plus a web deletion page (KC-093).

**C12. Journal (F12, F13)** [S]
- Visited places and explored-cell count per city, stored on the device.
  The journal grows from in-use location only (DISC-HIDDEN-BUSINESS-RULES
  R18). *(v1.0.1)*
- After the MVP: achievements and streaks.
- The secondary use of trip-session location is settled by
  DISC-HIDDEN-BUSINESS-RULES R18. Explored cells may use it under a double
  opt-in; the journal never does.

**C13. Compliance and credit surfaces**
- Map attribution on the map view itself, through the renderer's
  attribution control: OpenStreetMap ODbL and OpenFreeMap (KC-064,
  KC-087).
- An About screen with full attributions and "Concept inspired by
  MapShifter". Weather attribution appears only once weather ships.
- The location prominent-disclosure screen.
- A privacy policy link.

**C14. Settings**
- Units (km by default), default skin, reveal toggle, alarm defaults
  (sound, vibration), offline packs, account.

### Out of scope, stated or deferred until after the MVP

- Wear OS and Android Auto (later, KC-060).
- Localisation (KC-076).
- Turn-by-turn voice navigation.
- Offline routing (the recorded gap in C9).
- Checkpoint alarms on intermediate points. They are route-dependent and
  would be labelled as such (KC-095).
- Parking meter timer.
- [S] Widgets.
- [S] Weather chip (Open-Meteo).
- [S] Achievements.

### MVP slice [S]

C1, C2 (4 skins), C3, C4, C5, C6, C7, C8 (online preview only), C9 (with
offline search, without offline routing), C10, C11 (reduced sync), C12
(basic), C13, C14.

## Hidden Assumptions

- Hillshade and terrain need a free elevation-tile source with no billing
  account. If none exists, `terrain-sepia` ships without hillshade and
  `topo-mono` is dropped (F3).
- Straight-line distance is a good enough stand-in for "about to reach"
  (KC-095). On winding roads, 10 km straight-line can be a much longer
  drive. That errs early, which is the safe side for waking up.
- Cells of about 150 m feel like exploring. They still count as location
  history (C3).
- The safety net trades a false wake-up against a missed stop. Ringing
  the distinct "location lost" alert risks waking the user during a long
  halt in a dead zone. Staying silent risks sleeping past the stop. The
  design picks the distinct, uncertain alert. A user woken by it can
  check and go back to sleep, which costs less than missing a stop at
  night.

## Strengths

- One headline utility with a clear, testable, route-agnostic rule (C4),
  which matches KC-069 and KC-080.
- Skins are mostly data (C1), which keeps adding a skin cheap.
- The core features (C4, C5, C6, C7 offline, C9) work offline, as KC-104
  requires.

## Weaknesses

- C4 is the hardest feature to get right on Android (Doze, OEM battery
  killers, full-screen-intent limits) and the one most exposed to Play
  policy.
- 3D skins on low-end phones depend on the C1 2D-fallback requirement to
  meet KC-070.
- A large MVP for one developer. C11 is reduced, but it is still a
  backend plus sync.
- Offline routing falls short of "comparable to Google Maps" until after
  the MVP (C9).

## Questions

1. *(→ `technical-architecture`)* Which free elevation source supports
   hillshade and 3D terrain at zero spend, without a billing account?
   Candidates are open DEM tiles (e.g. Terrarium) or a regional extract on
   the VM. A commercial free tier with a key must be flagged, and any paid
   plan escalates under ADR-0001 rule 2. *(Proxy-answered framing.)*
2. *(→ `hidden-business-rules`)* The rule for arming an alarm inside its
   radius (answered in C4), and the secondary use of foreground-service
   location (C12).
3. *(Resolved)* C11 stays in the MVP with reduced sync. Deferring it
   would contradict KC-075.

## Recommendations

- **Keep:** the destination-radius alarm, which closes C-vs-D (KC-080,
  KC-095); alarms on any point; reveal as an opt-in global toggle (C3);
  accounts in the MVP with reduced sync (C11).
- **Improve:** 2D fallbacks for 3D skins; offline search inside packs;
  alarm accessibility without sound.
- **Remove:** Subnautica-inspired 3D (unless a spike proves otherwise);
  turn-by-turn, checkpoint alarms and the parking meter timer from the
  MVP; franchise and MapShifter echoes in codenames.

## Architecture Review (2026-11-02)

- **Against DISC-BUSINESS-VISION (Frozen):**
  - Location use stays within KC-101's scope: in use or an active trip
    session. Raw logs stay on the device in the MVP.
  - Accounts are optional and stay in the MVP (KC-102, KC-075).
  - The alarm, parking, saved places, offline search and city packs all
    work without the backend (KC-104).
  - Zero spend holds (KC-068). Every provider is free; the DEM source is
    open, and any paid source would escalate.
  - Identity rules hold: neutral codenames and original assets (KC-078,
    KC-057).
  - Consistent.
- **Evidence citations:** every one is current. No superseded claim is
  used as support, and KC-096 has been replaced by KC-092 and the frozen
  DISC-BUSINESS-VISION.
- **Internal consistency:** the MVP slice matches C1–C14. Deferred items
  appear only in "Out of scope", and C9's offline-routing gap is recorded
  there too.
- **Divergences:** none (no code).
- **Result:** passes.

## Revision History

- **v1.0.1 (2026-11-02):** narrows C12 so the journal grows from in-use
  location only, per DISC-HIDDEN-BUSINESS-RULES R18 (KC-119). This is a
  narrowing permitted by KC-101, not a contradiction of it. There is no
  code, so it is not a divergence.

## Confidence Level

**Overall: Inference (65)**, capped by KC-084 (rendering feasibility is
unverified until the M3 spike), per `OV-001` §4. Breakdown: owner-stated
capabilities are Fact (95–100); compliance obligations are Fact/Inference
(75–90); the skin table and design suggestions are Inference (65).
