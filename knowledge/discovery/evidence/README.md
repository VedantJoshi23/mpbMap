# Evidence Log

This project's evidence log, per Oriveda `OV-000` (Frozen). Entries follow
`.oriveda-framework/.oriveda/schemas/evidence.schema.yaml`. The log is
append-only: new evidence takes the next `EVD-NNN`, new claims the next
`KC-NNN`. Nothing is renumbered or deleted. A revised claim is marked
`superseded_by`.

**Subject of this project.** The owner wants to build an Android app that
replicates the behaviour of an existing iOS app, *MapShifter: Stylized Game
Map* by Filipp Bazun (the "reference app"). Most evidence below observes
the reference app directly. A claim about what the reference app does can
be a `fact`. A claim about what *this* project should do follows from it
only by inference, unless the owner stated it.

**Source of web evidence.** `EVD-008` and `EVD-009` were read on
2026-10-30 with a fetch tool. That tool returns a model-extracted digest of
the page, not the raw HTML. Claims from these two items are therefore
capped at 90.

---

## EVD-001

```yaml
id: EVD-001
type: conversation
source: Owner message in the Oriveda setup session, 2026-10-30
received: 2026-10-30
summary: >
  The owner's statement of intent: replicate the reference app's iOS
  behaviour on Android as a personal portfolio project, with no spending,
  crediting the original throughout. Also identifies reference/ as the
  evidence set and describes what each item is.
pipeline: vision
processed: true
claims:
  - id: KC-001
    statement: >
      The project's goal is an Android app that replicates the iOS behaviour
      of MapShifter, built as a personal portfolio project.
    status: fact
    confidence: 100
    evidence_ids: [EVD-001]
    investigation: business-vision
  - id: KC-002
    statement: >
      The project must not cost money ("without spending any money").
    status: fact
    confidence: 100
    evidence_ids: [EVD-001]
    investigation: business-vision
    notes: >
      Not yet stated: whether this rules out the one-time Google Play
      developer registration fee, i.e. whether the app is distributed
      through Play at all. Open question Q-03.
  - id: KC-003
    statement: >
      MapShifter's work is to be credited and acknowledged wherever
      necessary, throughout the process.
    status: fact
    confidence: 100
    evidence_ids: [EVD-001]
    investigation: business-vision
  - id: KC-004
    statement: >
      The aim is to replicate the reference app "as much as possible".
    status: fact
    confidence: 100
    evidence_ids: [EVD-001]
    investigation: business-vision
    notes: >
      How far "as much as possible" extends to visual assets, names and
      logos, as opposed to behaviour, is open. See KC-055 and Q-01.
  - id: KC-005
    statement: >
      The reference app is iOS-only today, with an Android version
      announced as coming soon.
    status: fact
    confidence: 95
    evidence_ids: [EVD-001, EVD-009]
    investigation: business-vision
  - id: KC-006
    statement: >
      The owner labels image-1.png as "red dead redemption", i.e. the owner
      reads the Old Wild Frontier II skin as evoking that game.
    status: fact
    confidence: 100
    evidence_ids: [EVD-001]
    investigation: hidden-business-rules
    notes: Source is reference/info.md, the owner's index of the evidence.
```

---

## EVD-002

```yaml
id: EVD-002
type: repository
source: This repository (github.com/VedantJoshi23/mpbMap), as of 2026-10-30
received: 2026-10-30
summary: >
  The project's own repository. It holds only the Oriveda submodule, the
  CLAUDE.md bootstrap, an empty knowledge/ layout and the reference/
  evidence folder. There is no application code.
pipeline: repository
processed: true
claims:
  - id: KC-007
    statement: >
      The project is greenfield: no application code, build files or
      platform scaffolding exist yet.
    status: fact
    confidence: 100
    evidence_ids: [EVD-002]
    investigation: repo-structure
  - id: KC-008
    statement: >
      No source code of the reference app is available. Every behaviour
      claim about it comes from screenshots, the store listing and the
      marketing site.
    status: fact
    confidence: 100
    evidence_ids: [EVD-002, EVD-001]
    investigation: repo-structure
```

---

## EVD-003

```yaml
id: EVD-003
type: screenshot
source: reference/image.png (464x612, App Store / marketing image, iPad)
received: 2026-10-30
summary: >
  An iPad marketing image. Headline copy sits above an iPad showing a city
  map in the Wild Frontier skin, with floating skin chips.
pipeline: screenshots
processed: true
claims:
  - id: KC-009
    statement: >
      The marketing copy reads "Your city, but reskinned by video games." /
      "A full-screen canvas. Twelve worlds to walk through. Same streets
      underneath.", with a "Runs on iPad" badge and the footer
      "MapShifter · Built for iPad".
    status: fact
    confidence: 95
    evidence_ids: [EVD-003]
    investigation: business-vision
  - id: KC-010
    statement: >
      The Wild Frontier skin renders land in sepia/beige, water in dark
      navy, buildings in tan/brown blocks and POIs as small dark round
      icons, with a search field across the top of the map.
    status: fact
    confidence: 90
    evidence_ids: [EVD-003]
    investigation: feature-inventory
  - id: KC-011
    statement: >
      Skins are shown as pill-shaped chips holding a round icon and an
      uppercase name ("WILD FRONTIER", "NEON CITY").
    status: fact
    confidence: 90
    evidence_ids: [EVD-003]
    investigation: feature-inventory
```

---

## EVD-004

```yaml
id: EVD-004
type: screenshot
source: reference/image-1.png (248x537, in-app iPhone screenshot, Manhattan)
received: 2026-10-30
summary: >
  The main map screen on iPhone, showing Manhattan in the Old Wild Frontier
  II skin with the full control chrome visible.
pipeline: screenshots
processed: true
claims:
  - id: KC-012
    statement: >
      Main-screen chrome observed: top-left, a skin selector showing the
      current skin's logo ("Old Wild Frontier II"), a dropdown chevron and
      a small orange badge; top-right, a speech-bubble button and a
      character-portrait avatar button; below them, a "Search places..."
      field and a weather chip ("8°" / "Weather"); on the right edge, a
      vertical stack of compass, zoom +, zoom −, locate-me and settings
      (gear); bottom-right, an "i" info button.
    status: fact
    confidence: 90
    evidence_ids: [EVD-004]
    investigation: feature-inventory
  - id: KC-013
    statement: >
      POIs are drawn as white round markers with dark category glyphs
      (e.g. food/drink, pharmacy, landmark), each carrying a small coloured
      corner badge (orange, yellow, purple).
    status: fact
    confidence: 85
    evidence_ids: [EVD-004]
    investigation: feature-inventory
    notes: What the badge colours mean is unknown (Q-06).
  - id: KC-014
    statement: >
      The map shows a blue round marker with a house glyph (a saved "home"
      place) and a separate, stylised location marker for the user.
    status: inference
    confidence: 65
    evidence_ids: [EVD-004]
    investigation: feature-inventory
  - id: KC-015
    statement: >
      Area labels use a skin-specific spaced uppercase serif face (e.g.
      "MANHATTAN ISLAND", "ROOSEVELT ISLAND"). Some label text may be
      reworded in-theme, not just restyled.
    status: assumption
    confidence: 45
    evidence_ids: [EVD-004, EVD-009]
    investigation: hidden-business-rules
    notes: >
      The restyled typography is observed. The rewording is not confirmed:
      EVD-009 says labels are "relabeled in each skin's typography".
  - id: KC-016
    statement: >
      The map is full-bleed under the status bar, with the controls
      floating over it. The reference app has no tab bar or bottom
      navigation on the main screen.
    status: fact
    confidence: 90
    evidence_ids: [EVD-004]
    investigation: user-journeys
```

---

## EVD-005

```yaml
id: EVD-005
type: screenshot
source: reference/image-2.png (230x498, App Store screenshot "Pick a world")
received: 2026-10-30
summary: >
  A store screenshot titled "The Skin Collection", with two phones: a dark
  neon skin and a grey 3D city with trees.
pipeline: screenshots
processed: true
claims:
  - id: KC-017
    statement: >
      The copy reads "THE SKIN COLLECTION" / "Pick a world. Walk inside it."
      / "Four hand-crafted styles. More dropping monthly."
    status: fact
    confidence: 95
    evidence_ids: [EVD-005]
    investigation: business-vision
  - id: KC-018
    statement: >
      One skin renders a pitched (tilted) camera with 3D-extruded grey
      buildings and green tree clusters ("MANHATTAN").
    status: fact
    confidence: 90
    evidence_ids: [EVD-005]
    investigation: feature-inventory
  - id: KC-019
    statement: >
      One skin uses a dark navy/indigo palette with bright accents and a
      white arrow-style location marker, consistent with the "Cybershock
      2099" (neon/grid) skin.
    status: inference
    confidence: 65
    evidence_ids: [EVD-005, EVD-009]
    investigation: feature-inventory
```

---

## EVD-006

```yaml
id: EVD-006
type: screenshot
source: reference/image-3.png (230x498, App Store screenshot "One swipe")
received: 2026-10-30
summary: >
  A store screenshot showing the same street in four horizontal bands, one
  skin per band, each labelled.
pipeline: screenshots
processed: true
claims:
  - id: KC-020
    statement: >
      The copy reads "SAME STREET, FOUR REALITIES." / "One swipe. Whole new
      world." The four bands are labelled WILD FRONTIER, BLOCKY WORLD,
      WASTELAND and STREET LIFE.
    status: fact
    confidence: 95
    evidence_ids: [EVD-006]
    investigation: feature-inventory
  - id: KC-021
    statement: >
      Observed skin palettes: Wild Frontier is sepia; Blocky World is
      saturated green with pixel texture; Wasteland is monochrome green on
      black with glow; Street Life is light grey and white with blue water
      and bold black label plates.
    status: fact
    confidence: 85
    evidence_ids: [EVD-006]
    investigation: feature-inventory
  - id: KC-022
    statement: >
      Skins can be switched with a swipe gesture on the map, as well as
      through the skin selector (KC-012).
    status: inference
    confidence: 60
    evidence_ids: [EVD-006, EVD-004]
    investigation: user-journeys
    notes: >
      "One swipe" may be marketing language for switching quickly, not a
      literal gesture. Unconfirmed (Q-05).
  - id: KC-023
    statement: >
      Skin names change over time. Marketing names (Wild Frontier,
      Wasteland, Blocky World, Street Life, Neon City) differ from current
      in-app and listing names (Old Wild Frontier II, Wastefield 4, Voxel
      Realm, Great Street Empire..., Cybershock 2099).
    status: inference
    confidence: 75
    evidence_ids: [EVD-003, EVD-004, EVD-006, EVD-008, EVD-009]
    investigation: hidden-business-rules
```

---

## EVD-007

```yaml
id: EVD-007
type: screenshot
source: reference/image-4.png (1047x673, App Store page, Accessibility and Information sections)
received: 2026-10-30
summary: >
  A capture of the App Store listing's Accessibility and Information
  panels.
pipeline: screenshots
processed: true
claims:
  - id: KC-024
    statement: >
      The reference app declares these accessibility features: VoiceOver,
      Larger Text, Dark Interface, Differentiate Without Color Alone,
      Sufficient Contrast, Reduced Motion.
    status: fact
    confidence: 100
    evidence_ids: [EVD-007, EVD-008]
    investigation: business-vision
    notes: >
      This is a non-functional requirement. It also matters for
      technical-architecture, because skins must stay legible and
      colour-independent.
  - id: KC-025
    statement: >
      Store facts: seller Filipp Bazun; category Navigation; size 121.9 MB;
      requires iOS 18.0 or later; English plus 9 more languages; age rating
      4+; © 2026 Filipp Bazun.
    status: fact
    confidence: 100
    evidence_ids: [EVD-007]
    investigation: business-vision
```

---

## EVD-008

```yaml
id: EVD-008
type: competitor
source: https://apps.apple.com/us/app/mapshifter-stylized-game-map/id6762314031 (read 2026-10-30)
received: 2026-10-30
summary: >
  The App Store listing: description, feature list, version history
  1.0 (2026-04-23) to 1.0.34 (2026-10), privacy labels, ratings.
pipeline: competitor
processed: true
claims:
  - id: KC-026
    statement: >
      The listing is "MapShifter: Stylized Game Map", subtitle "Aesthetic
      minimap GPS, retro". The app is free, and the listing shows no in-app
      purchases.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008]
    investigation: business-vision
  - id: KC-027
    statement: >
      The app has no accounts ("No accounts. Install, open, go."), no ads,
      and no server of its own that logs user location.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008, EVD-009]
    investigation: hidden-business-rules
  - id: KC-028
    statement: >
      The app states it is not affiliated with, endorsed by, or sponsored
      by any game studio, publisher or franchise referenced.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008, EVD-009]
    investigation: hidden-business-rules
  - id: KC-029
    statement: >
      Listed features: live skin switching without losing the active route;
      turn-by-turn navigation for driving, walking and cycling; place
      search over OpenStreetMap data; offline map downloads; a journey
      tracker (visited countries, history, streaks, achievements); Apple
      Watch companion; CarPlay with themed map, route, search and guidance;
      Home Screen widgets showing a themed mini-map.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008]
    investigation: feature-inventory
  - id: KC-030
    statement: >
      Features added after launch, per version history: voice guidance,
      route choices (alternatives), Fog of War mode, a driving camera with
      map following, "Zoom with Speed" and "Navigation Zoom" settings,
      automatic arrival detection, faster rerouting, an End Route button,
      a miles/feet units setting, Now Playing, iCloud sync, offline map
      persistence and deletion, and trip sharing from CarPlay.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008]
    investigation: feature-inventory
  - id: KC-031
    statement: >
      Skins listed at 1.0.3: Old Wild Frontier II, Great Street Empire: SA,
      Great Street Empire V, Voxel Realm, Cybershock 2099, Wastefield 4.
      Velocity Rush: Neon Heat was added in 1.0.25 and Maze Muncher in
      1.0.31. Great Street Empire V became the default for new installs in
      1.0.30. A skin named "Web-Slinger: Symbiote" appears in the 1.0.2
      notes and not afterwards.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008]
    investigation: feature-inventory
  - id: KC-032
    statement: >
      Privacy labels: usage data (product interaction) for analytics, and
      diagnostics (crash, performance) for app functionality. Neither is
      linked to the user.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008]
    investigation: hidden-business-rules
  - id: KC-033
    statement: >
      The app ships in 10 languages: English, French, German, Italian,
      Japanese, Korean, Portuguese, Russian, Simplified Chinese, Spanish.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008, EVD-007]
    investigation: business-vision
  - id: KC-034
    statement: >
      The listing rates the app 4.9/5 from 15 ratings. Reviews praise the
      free price, CarPlay and map smoothness.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008]
    investigation: business-vision
  - id: KC-035
    statement: >
      The navigation session is the most fragile area of the reference app.
      Release notes repeatedly fix arrival detection, turn banners,
      rerouting, zoom behaviour during navigation, and offline-map deletion
      and persistence.
    status: inference
    confidence: 70
    evidence_ids: [EVD-008]
    investigation: technical-debt
  - id: KC-036
    statement: >
      The reference app ships very frequently (about 34 patch releases in
      six months, 1.0 to 1.0.34), and new skins are a recurring release
      driver.
    status: fact
    confidence: 90
    evidence_ids: [EVD-008]
    investigation: business-vision
```

---

## EVD-009

```yaml
id: EVD-009
type: competitor
source: https://mapshifter.com/ (read 2026-10-30)
received: 2026-10-30
summary: >
  The reference app's marketing site: pitch, skin list with taglines,
  features, privacy stance, tech stack attribution, platform status.
pipeline: competitor
processed: true
claims:
  - id: KC-037
    statement: >
      Pitch: "Your city, but rewritten by video games." / "Same streets.
      Same GPS. Whole different vibe." The skins are described as cosmetic,
      layered on a working maps engine.
    status: fact
    confidence: 90
    evidence_ids: [EVD-009]
    investigation: business-vision
  - id: KC-038
    statement: >
      The reference app is built on OpenStreetMap (data), MapLibre (map
      rendering) and OSRM (routing).
    status: fact
    confidence: 90
    evidence_ids: [EVD-009]
    investigation: technical-architecture
  - id: KC-039
    statement: >
      Skin taglines: Original (default mascot style); Old Wild Frontier II
      "sepia · serif · dust"; Great Street Empire V "neon noir · slick ·
      sun"; Cybershock 2099 "neon · grid · rain"; Voxel Realm "pixel ·
      cubed · lush"; Wastefield 4 "pip · glow · radiation"; Great Street
      Empire: SA "graffiti · bass · 90s".
    status: fact
    confidence: 90
    evidence_ids: [EVD-009]
    investigation: feature-inventory
  - id: KC-040
    statement: >
      Each skin has its own navigation "vehicle" arrow (e.g. horse,
      minecart, sedan), and search results are shown in the skin's
      typography.
    status: fact
    confidence: 90
    evidence_ids: [EVD-009]
    investigation: feature-inventory
  - id: KC-041
    statement: >
      Offline downloads are per city and include the skins, so a skin can
      be switched while offline.
    status: fact
    confidence: 85
    evidence_ids: [EVD-009, EVD-008]
    investigation: hidden-business-rules
  - id: KC-042
    statement: >
      Location stays on-device. It is sent to OSM-based search and routing
      services only when the user uses them. Analytics exclude search text,
      destinations, routes and precise location.
    status: fact
    confidence: 90
    evidence_ids: [EVD-009]
    investigation: hidden-business-rules
  - id: KC-043
    statement: >
      The site states the skins are original art and typography, not copies
      of game maps.
    status: fact
    confidence: 90
    evidence_ids: [EVD-009]
    investigation: hidden-business-rules
  - id: KC-044
    statement: The site gives iOS 17 or later as the minimum OS.
    status: fact
    confidence: 60
    evidence_ids: [EVD-009]
    investigation: business-vision
    superseded_by: KC-025
    notes: >
      Conflicts with the store's "iOS 18.0 or later" (EVD-007). The store
      is authoritative for install requirements. Kept per OV-000.
  - id: KC-045
    statement: >
      The site lists Android as "Android Soon", with no date.
    status: fact
    confidence: 90
    evidence_ids: [EVD-009]
    investigation: business-vision
  - id: KC-046
    statement: >
      The site does not mention the weather chip seen in-app (KC-012).
      Weather's data source is unknown.
    status: fact
    confidence: 85
    evidence_ids: [EVD-009, EVD-004]
    investigation: technical-architecture
```

---

## Synthesis claims (inferences across evidence)

These claims are drawn from the evidence items above by reasoning, so none
can be a `fact` (`OV-000` §4). They are attached to the evidence they rest
on. They belong to the evidence items cited, but are grouped here so that
inference stays visibly separate from observation.

```yaml
claims:
  - id: KC-047
    statement: >
      The reference app is client-only, with no backend of its own. It
      relies on third-party OSM-ecosystem services for tiles, search and
      routing, plus an analytics/crash SDK.
    status: inference
    confidence: 75
    evidence_ids: [EVD-008, EVD-009]
    investigation: technical-architecture
    notes: The tile source and the search/geocoding service are not named (Q-04).
  - id: KC-048
    statement: >
      A skin is a bundle of: a vector map style (colours, layers, 3D
      extrusion on/off), fonts, a POI icon set, a location/vehicle marker,
      a logo/chip icon and UI accent colours. Switching skins swaps the
      style without touching map position or navigation state.
    status: inference
    confidence: 70
    evidence_ids: [EVD-004, EVD-005, EVD-006, EVD-008, EVD-009]
    investigation: data-model
  - id: KC-049
    statement: >
      Candidate core entities: Skin; Place (OSM POI / search result);
      SavedPlace (e.g. home); Route (mode, alternatives, steps);
      NavigationSession; OfflineRegion; Trip / journey history;
      VisitedCountry; Achievement / streak; Settings (units, navigation
      zoom, zoom with speed, fog of war, voice).
    status: inference
    confidence: 65
    evidence_ids: [EVD-004, EVD-008, EVD-009]
    investigation: data-model
  - id: KC-050
    statement: >
      Candidate bounded contexts: Map & Skins (rendering, styling);
      Search (places, geocoding); Routing & Navigation (route planning,
      guidance, voice, rerouting, arrival); Offline (region download and
      storage); Journey (trips, visited countries, streaks, achievements,
      fog of war); Platform Surfaces (widget, watch, car).
    status: inference
    confidence: 65
    evidence_ids: [EVD-008, EVD-009]
    investigation: domain-discovery
  - id: KC-051
    statement: >
      Candidate domain events: SkinChanged (consumed by Map, Navigation
      marker, widget); RouteStarted / RerouteRequested /
      ArrivalDetected / RouteEnded (produced by Navigation, consumed by
      Journey); LocationUpdated (consumed by Navigation, Journey, Fog of
      War); OfflineRegionDownloaded / Deleted.
    status: inference
    confidence: 60
    evidence_ids: [EVD-008, EVD-009]
    investigation: domain-discovery
    notes: >
      This is the OV-001 cross-cutting check for events. No event catalogue
      exists in the evidence, so these are inferred from features only.
  - id: KC-052
    statement: >
      "Fog of War" mode obscures map areas the user has not visited, built
      from journey history.
    status: assumption
    confidence: 50
    evidence_ids: [EVD-008]
    investigation: hidden-business-rules
    notes: Inferred from the name alone. Behaviour is unobserved (Q-07).
  - id: KC-053
    statement: >
      Of the reference app's iOS-only surfaces, these map to Android:
      CarPlay to Android Auto, Apple Watch to Wear OS, Home Screen widget
      to an Android app widget, iCloud sync to an Android equivalent.
      Each is a separate scope decision for this project.
    status: inference
    confidence: 80
    evidence_ids: [EVD-008, EVD-001]
    investigation: recommendations
  - id: KC-054
    statement: >
      Free public instances of OSM services are typically governed by
      fair-use policies, so the zero-spend constraint (KC-002) makes the
      choice of tile, search and routing providers a load-bearing
      architecture decision, not an implementation detail.
    status: inference
    confidence: 70
    evidence_ids: [EVD-001, EVD-009]
    investigation: technical-architecture
    notes: >
      The provider-policy part rests on general knowledge, not on logged
      evidence. It needs verifying against each provider's current terms
      in M3 (OV-004).
  - id: KC-055
    statement: >
      Replicating the reference app's behaviour, layout and feature set is
      a different thing from copying its assets. Its skin names and logos
      (e.g. the "Old Wild Frontier II" lockup) are MapShifter's own work,
      and they deliberately evoke third-party game franchises (KC-006,
      KC-028, KC-043). A portfolio replica is safest as a credited,
      clearly separate app: its own name, its own skin names and art, and
      a visible "inspired by MapShifter" attribution. It should not ship
      MapShifter's logos, screenshots, store copy or name.
    status: inference
    confidence: 70
    evidence_ids: [EVD-001, EVD-004, EVD-008, EVD-009]
    investigation: recommendations
    notes: The owner must decide this. Surfaced as Q-01.
```

---

## Investigation Coverage (OV-000 exit criteria)

| Investigation | Best claims | Status |
| --- | --- | --- |
| `repo-structure` | KC-007, KC-008 (fact) | Covered. The project is greenfield. |
| `business-vision` | KC-001–005, KC-024–026, KC-033, KC-036–037 (fact) | Covered. Scope boundaries are open (Q-01–Q-03). |
| `feature-inventory` | KC-012, KC-029–031, KC-039–040 (fact) | Covered for *what* exists. Depth per feature comes only from listing copy. |
| `user-journeys` | KC-016 (fact), KC-022 (inference 60) | **Gap.** Only static screens. Onboarding, the skin-switch interaction, search → route → navigate, settings, offline download and the journey screen are unseen. |
| `data-model` | KC-048, KC-049 (inference) | Inference only, which is expected with no source code. |
| `domain-discovery` | KC-050, KC-051 (inference) | Inference only. Events check done (KC-051). |
| `technical-architecture` | KC-038 (fact), KC-047, KC-054 (inference) | Covered. The tile and geocoding source is unknown (Q-04). NFRs: KC-024 (accessibility), KC-041 (offline). |
| `hidden-business-rules` | KC-027, KC-028, KC-032, KC-042, KC-043 (fact) | Covered. KC-015 and KC-052 are assumption-tier and stay open. |
| `technical-debt` | KC-035 (inference) | Not applicable to this project yet (greenfield). Recorded as risk carried over from the reference app. |
| `recommendations` | KC-053, KC-055 (inference) | Waiting on owner decisions Q-01–Q-03. |

## Open Questions and Gaps (per OV-000 gap detection)

Ranked by how much they block downstream work.

- **Q-01: Replica boundary (blocks Constitution).** Should the project
  replicate behaviour and layout but use its own name, skin names and art,
  with MapShifter credited (KC-055)? Or does the owner intend something
  closer to the original?
- **Q-02: Platform scope (blocks Architecture).** Is the scope the phone
  app only, or do Android Auto, Wear OS and the home-screen widget count
  too (KC-053)?
- **Q-03: Distribution (blocks Architecture).** How is the app
  distributed: a GitHub release/APK, or Google Play (which charges a
  one-time registration fee, KC-002)?
- **Q-04: Data providers.** The tile, search and weather sources are
  unknown in the reference app. Settled in M3 against KC-002 and KC-054.
- **Q-05: Skin-switch gesture.** Is "one swipe" a literal gesture? Needs
  user-journey evidence.
- **Q-06: POI badges.** What do the coloured corner badges on POI markers
  mean?
- **Q-07: Fog of War, the speech-bubble button and the avatar button.**
  The behaviour of all three is unobserved.
- **Largest evidence gap: `user-journeys`.** A screen recording of the iOS
  app would close Q-05–Q-07 and most of the journeys gap at once. That
  could be the App Store preview video, or a walkthrough from anyone with
  an iPhone.
