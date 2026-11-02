# Evidence Log

This project's evidence log, per Oriveda `OV-000` (Frozen). Entries follow
`.oriveda-framework/.oriveda/schemas/evidence.schema.yaml`. The log is
append-only: new evidence takes the next `EVD-NNN`, new claims the next
`KC-NNN`. Nothing is renumbered or deleted. A revised claim is marked
`superseded_by`.

**Subject of this project.** The owner is building an original Android app
based on the *concept* of an existing iOS app, *MapShifter: Stylized Game
Map* by Filipp Bazun (the "reference app"), which MapShifter is credited
for. The concept is real-world maps reskinned in video-game-inspired
styles. The owner first framed this as a replica (EVD-001) and narrowed it
to concept-only in EVD-010. Most evidence below observes the reference app
directly. A claim about what the reference app does can be a `fact`. Those
claims are concept and market evidence for this project, not its
requirements (KC-063).

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
    superseded_by: KC-056
  - id: KC-002
    statement: >
      The project must not cost money ("without spending any money").
    status: fact
    confidence: 100
    evidence_ids: [EVD-001]
    investigation: business-vision
    notes: >
      Q-03 asked whether this rules out the one-time Google Play
      registration fee. Resolved by KC-058: the fee is already paid.
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
    superseded_by: KC-057
    notes: >
      Q-01 asked how far "as much as possible" extends to visual assets,
      names and logos. Resolved by KC-057: concept only.
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
    superseded_by: KC-121
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
    superseded_by: KC-121
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
    superseded_by: KC-126
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
    superseded_by: KC-126
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
    notes: >
      Surfaced as Q-01. The owner accepted it and went further, to concept
      only (KC-057).
```

---

## EVD-010

```yaml
id: EVD-010
type: conversation
source: Owner answers to Q-01–Q-03 in the Oriveda setup session, 2026-10-30
received: 2026-10-30
summary: >
  The owner narrows the project from replicating MapShifter to building an
  original app on its concept alone. Initial scope is the phone app,
  extendable later to Wear OS and Android Auto. Distribution is through
  the owner's existing, already-paid Google Play developer account.
pipeline: vision
processed: true
claims:
  - id: KC-056
    statement: >
      The project builds an original Android app on MapShifter's concept
      (real-world maps reskinned in video-game-inspired styles). It is not
      a replica of MapShifter.
    status: fact
    confidence: 100
    evidence_ids: [EVD-010]
    investigation: business-vision
  - id: KC-057
    statement: >
      Everything beyond the concept is the project's own: the app name,
      skins, art, UI layout, features and flows. MapShifter is credited as
      the inspiration for the concept.
    status: fact
    confidence: 100
    evidence_ids: [EVD-010, EVD-001]
    investigation: business-vision
  - id: KC-058
    statement: >
      The app is published on Google Play through the owner's existing
      developer account. The one-time registration fee is already paid, so
      Play distribution adds no cost under KC-002.
    status: fact
    confidence: 100
    evidence_ids: [EVD-010]
    investigation: business-vision
  - id: KC-059
    statement: >
      The initial scope is the Android phone app only.
    status: fact
    confidence: 100
    evidence_ids: [EVD-010]
    investigation: business-vision
  - id: KC-060
    statement: >
      The design must stay extendable to Wear OS and Android Auto, which
      may be planned later but are not built now.
    status: fact
    confidence: 100
    evidence_ids: [EVD-010]
    investigation: technical-architecture
  - id: KC-061
    statement: >
      No preview video or iOS screen recording of the reference app can be
      obtained. Its user journeys will remain unobserved.
    status: fact
    confidence: 100
    evidence_ids: [EVD-010]
    investigation: user-journeys
  - id: KC-062
    statement: >
      Because the project's UI and flows are its own (KC-057), user
      journeys are designed, not discovered. They will be authored in this
      project's Domain and Feature specifications (M5/M6). The unobserved
      reference journeys (KC-061) therefore no longer block Discovery.
    status: inference
    confidence: 85
    evidence_ids: [EVD-010]
    investigation: user-journeys
  - id: KC-063
    statement: >
      Claims about the reference app (EVD-003–EVD-009) now serve as concept
      and market evidence, not requirements. They show what the concept
      looks like in a shipped product, that it can be built on a free open
      stack (KC-038), and which areas proved fragile (KC-035). None of them
      binds this project's design unless adopted explicitly.
    status: inference
    confidence: 85
    evidence_ids: [EVD-010, EVD-008, EVD-009]
    investigation: recommendations
  - id: KC-064
    statement: >
      Publishing on Google Play brings obligations that a sideloaded
      portfolio build would not. These include a privacy policy and Data
      safety declaration, a content rating, Play's target-API-level rules,
      map-data attribution in the app (e.g. OpenStreetMap's ODbL credit),
      and a store listing that does not use MapShifter's name or
      game-franchise trademarks in a way that implies affiliation.
    status: inference
    confidence: 75
    evidence_ids: [EVD-010, EVD-009]
    investigation: business-vision
    notes: >
      Rests on general knowledge of Play and OSM policy, not on logged
      evidence. To verify against current policy text in M3/M4.
  - id: KC-065
    statement: >
      MapShifter has announced its own Android version (KC-045). This app
      will therefore be published on Play in a category where the concept's
      originator is expected to arrive. That makes a distinct name,
      identity and listing, plus clear credit, more important.
    status: inference
    confidence: 80
    evidence_ids: [EVD-010, EVD-009]
    investigation: recommendations
```

---

## EVD-011

```yaml
id: EVD-011
type: conversation
source: Owner's Discussion reply on DISC-BUSINESS-VISION v0.1.0, 2026-11-02
received: 2026-11-02
summary: >
  The owner confirms the investigation's interpretations, then sets the
  budget horizon, success measure, product posture, NFRs, skin rules,
  personal-data storage options and the server, and introduces the app's
  utility feature: a location-based arrival alarm.
pipeline: vision
processed: true
claims:
  - id: KC-066
    statement: >
      The primary audience is people reviewing the owner's portfolio. Play
      Store users are secondary. The owner confirms the other
      interpretations in DISC-BUSINESS-VISION v0.1.0 as correct.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: business-vision
  - id: KC-067
    statement: >
      After the MVP, the app is maintained, publicly available and usable.
      It is not a frozen demo.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: business-vision
  - id: KC-068
    statement: >
      The app stays free of cost to run until it passes 300 downloads.
      Tech stack and methodology choices must defer any spending until
      then. The budget is revisited after that, and no budget is presumed
      before it.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: business-vision
    notes: Refines KC-002 by adding a horizon to the zero-spend rule.
  - id: KC-069
    statement: >
      Success means a published app that works correctly, complies with
      Play's rules and provides real utility.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: business-vision
  - id: KC-070
    statement: >
      Skinned maps must feel as smooth as native Google Maps or Apple Maps.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: business-vision
    notes: >
      This is a non-functional requirement with no number attached. It
      needs measurable targets in Standards (M4), such as frame rate while
      panning and zooming, and time to first render.
  - id: KC-071
    statement: >
      A game inspiration whose map is 3D is used only if 3D rendering is
      feasible for it. Otherwise that inspiration is dropped.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: feature-inventory
  - id: KC-072
    statement: >
      Explore-to-reveal is an opt-in toggle available on every skin. When
      it is on, parts of the map are revealed as the user explores them.
      When it is off, the whole map is visible.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: feature-inventory
  - id: KC-073
    statement: >
      The record of visited and explored places may be stored locally as
      GeoJSON or in a server database. The choice is open.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: data-model
  - id: KC-074
    statement: >
      Wanted features: accessibility options; offline support comparable to
      Google Maps; saved parking spots; and marked locations named in
      gaming terminology (e.g. waypoint, quest marker), with the terms
      sourced from common gaming usage.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: feature-inventory
  - id: KC-075
    statement: >
      Product posture: free, no ads, user accounts, a backend of the
      project's own, and location logging.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: hidden-business-rules
    notes: >
      This differs from the reference app on accounts, backend and
      location logging (KC-027, KC-042). Allowed by KC-063.
  - id: KC-076
    statement: >
      Offline maps are a goal. The app is English-only.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: business-vision
  - id: KC-077
    statement: >
      Accessibility options are in scope, even though the answer to the
      NFR question named only offline maps. The owner listed accessibility
      options earlier in the same message.
    status: inference
    confidence: 75
    evidence_ids: [EVD-011]
    investigation: business-vision
    superseded_by: KC-103
    notes: To confirm with the owner.
  - id: KC-078
    statement: >
      No franchise-referencing names or marks appear in the project's
      identity: the app name, skin names, logos, store listing or in-app
      text. The games listed in EVD-012 are design references only.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011, EVD-012]
    investigation: business-vision
  - id: KC-079
    statement: >
      Utility feature, the arrival alarm: it alerts the user by location as
      they approach a destination, instead of at an estimated time. Its
      purpose is to let the user sleep through overnight travel despite
      delays. The user sets the trigger distance manually (the example
      given is 10 km). Multiple alarms are possible.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: feature-inventory
  - id: KC-080
    statement: >
      The alarm must not depend on the route actually taken. The owner's
      example: on a trip from A to B via either C or D, an alarm set at C
      never fires if the vehicle goes via D. The alarm must account for
      both location and distance.
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: hidden-business-rules
  - id: KC-081
    statement: >
      The backend runs on the owner's current VM (see EVD-013).
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: technical-architecture
  - id: KC-082
    statement: >
      Research of free data providers is delegated to the agent (Q-04).
    status: fact
    confidence: 100
    evidence_ids: [EVD-011]
    investigation: technical-architecture
```

---

## EVD-012

```yaml
id: EVD-012
type: other
source: reference/image-5.png (625x413, owner-supplied table of game inspirations)
received: 2026-11-02
summary: >
  The owner's list of ten games that inspire the map skins, each paired
  with its most distinctive geographic feature.
pipeline: vision
processed: true
claims:
  - id: KC-083
    statement: >
      Skin inspirations and their distinctive features: Red Dead Redemption
      2 (natural geography); Cyberpunk 2077 (vertical urban design); Zelda:
      BOTW (exploration-driven geography); GTA San Andreas (regional
      diversity); Death Stranding (terrain and infrastructure); Subnautica
      (3D underwater geography); Assassin's Creed Odyssey (archipelago
      geography); Minecraft (procedural world generation); Hollow Knight
      (interconnected underground topology); PUBG (large-scale tactical
      geography).
    status: fact
    confidence: 100
    evidence_ids: [EVD-012]
    investigation: feature-inventory
  - id: KC-084
    statement: >
      Some inspirations rest on data that is weak or missing in
      OpenStreetMap-derived vector tiles. Subnautica's underwater 3D needs
      bathymetry. Hollow Knight's underground topology has only 2D
      tunnel/subway flags. Under KC-071, these two are the likeliest to
      drop or to be reinterpreted in 2D. 3D buildings (Cyberpunk 2077,
      Minecraft) and terrain (Death Stranding, RDR2) look feasible.
    status: inference
    confidence: 65
    evidence_ids: [EVD-012, EVD-011, EVD-014]
    investigation: technical-architecture
    notes: >
      Rests on general knowledge of the OpenMapTiles schema and MapLibre's
      building-extrusion and terrain features. To be verified by a
      rendering spike in M3.
```

---

## EVD-013

```yaml
id: EVD-013
type: other
source: Inspection of the owner's VM (uname, free, df, OCI instance metadata, listening ports), 2026-11-02
received: 2026-11-02
summary: >
  Resources and existing services on the VM that KC-081 names as the
  server.
pipeline: repository
processed: true
claims:
  - id: KC-085
    statement: >
      The VM is an Oracle Cloud VM.Standard.A1.Flex (ARM64, aarch64) in
      ap-mumbai-1, with 2 OCPUs and 12 GB RAM. About 9.2 GB of the RAM is
      in use, and about 2.4 GB is available. Its 96 GB disk has 31 GB
      free. It runs Ubuntu 24.04, and Docker, PostgreSQL (localhost:5432)
      and nginx (ports 80/443) are already running alongside other
      services.
    status: fact
    confidence: 95
    evidence_ids: [EVD-013]
    investigation: technical-architecture
  - id: KC-086
    statement: >
      The VM is shared with other workloads. It can host a small API, a
      database and static region map files. It cannot host a world-scale
      tile set (the planet build is about 120 GB, KC-094), a global
      geocoder, or a global routing engine within about 2.4 GB of free RAM.
    status: inference
    confidence: 80
    evidence_ids: [EVD-013, EVD-021]
    investigation: technical-architecture
```

---

## EVD-014 to EVD-021: free data-provider research (Q-04)

All were read on 2026-11-02 with the fetch tool (model-extracted digest),
so claims are capped at 90. Each policy can change and must be re-checked
before launch.

```yaml
- id: EVD-014
  type: other
  source: https://openfreemap.org/
  pipeline: other
  claims:
    - id: KC-087
      statement: >
        OpenFreeMap: free vector tiles with no API key and no stated
        request limits. Commercial use is allowed. The schema is
        OpenMapTiles. Offline app downloads are allowed up to 3,000 tiles
        or 50 MB per download, with at most 4 parallel requests and a
        unique User-Agent. Attribution is required. There is no SLA. It
        can be self-hosted.
      status: fact
      confidence: 90
      evidence_ids: [EVD-014]
      investigation: technical-architecture
- id: EVD-015
  type: other
  source: https://operations.osmfoundation.org/policies/nominatim/
  pipeline: other
  claims:
    - id: KC-088
      statement: >
        Nominatim public API: at most 1 request per second, counted across
        all of an app's users combined. Client-side autocomplete is
        forbidden. An identifying User-Agent is required, results must be
        cached, and a caching proxy is recommended.
      status: fact
      confidence: 90
      evidence_ids: [EVD-015]
      investigation: technical-architecture
- id: EVD-016
  type: other
  source: https://github.com/Project-OSRM/osrm-backend/wiki/Demo-server
  pipeline: other
  claims:
    - id: KC-089
      statement: >
        OSRM demo server: reasonable, non-commercial use only, at most 1
        request per second. It offers car, foot and bike profiles, with no
        uptime or update guarantees.
      status: fact
      confidence: 90
      evidence_ids: [EVD-016]
      investigation: technical-architecture
- id: EVD-017
  type: other
  source: https://photon.komoot.io/
  pipeline: other
  claims:
    - id: KC-090
      statement: >
        Photon public API: supports search-as-you-type. It is fair use,
        with heavy use throttled, no availability guarantee, and terms that
        may change. It is open source and can be self-hosted.
      status: fact
      confidence: 90
      evidence_ids: [EVD-017]
      investigation: technical-architecture
- id: EVD-018
  type: other
  source: https://open-meteo.com/en/terms
  pipeline: other
  claims:
    - id: KC-091
      statement: >
        Open-Meteo free API: non-commercial use only, at 10,000 calls a
        day, 5,000 an hour and 600 a minute, under a CC-BY 4.0
        attribution licence. Apps without ads or subscriptions count as
        non-commercial.
      status: fact
      confidence: 90
      evidence_ids: [EVD-018]
      investigation: technical-architecture
- id: EVD-019
  type: other
  source: https://support.google.com/googleplay/android-developer/answer/9799150
  pipeline: other
  claims:
    - id: KC-092
      statement: >
        Play background-location policy: background location is allowed
        only for core functionality. An app declares exactly one
        location-based feature, and declaring several leads to rejection.
        The declaration needs a video of 30 seconds or less and an in-app
        prominent disclosure shown before the permission prompt. Foreground
        use is preferred; a foreground service must start from a user
        action and end right after it.
      status: fact
      confidence: 90
      evidence_ids: [EVD-019]
      investigation: hidden-business-rules
- id: EVD-020
  type: other
  source: https://support.google.com/googleplay/android-developer/answer/13327111
  pipeline: other
  claims:
    - id: KC-093
      statement: >
        Play account-deletion policy: an app that lets users create
        accounts must offer an in-app path to delete the account and its
        data, plus a web link where deletion can be requested without the
        app. The link goes in the Data safety form.
      status: fact
      confidence: 90
      evidence_ids: [EVD-020]
      investigation: hidden-business-rules
- id: EVD-021
  type: other
  source: https://docs.protomaps.com/basemaps/downloads
  pipeline: other
  claims:
    - id: KC-094
      statement: >
        Protomaps: a planet PMTiles basemap of about 120 GB (zoom 0–15)
        under ODbL. `pmtiles extract` cuts a region, and each extra zoom
        level roughly doubles the file size. Hotlinking the download URLs
        is discouraged.
      status: fact
      confidence: 85
      evidence_ids: [EVD-021]
      investigation: technical-architecture
```

## Synthesis claims, round 2 (2026-11-02)

```yaml
claims:
  - id: KC-095
    statement: >
      The arrival alarm is route-agnostic if it fires on the device's
      distance to the destination (a radius around B), not on passing a
      point along the way (C). Straight-line distance needs no routing and
      no network, so it keeps working through low-coverage stretches at
      night. An alarm on an intermediate point (C) is inherently
      route-dependent. If offered at all, it should be labelled that way,
      or allow "any of these points".
    status: inference
    confidence: 85
    evidence_ids: [EVD-011]
    investigation: hidden-business-rules
  - id: KC-096
    statement: >
      Play allows one declared background-location feature (KC-092). The
      arrival alarm is this app's strongest candidate for core
      functionality. If it runs as a foreground service that the user
      starts (persistent notification, ending on arrival), it may not need
      background location at all. Location logging or explore-to-reveal
      while the app is closed would then have no declared feature to rest
      on. So "location logging" (KC-075) should record only while the app
      is in use or an alarm or navigation session is active.
    status: inference
    confidence: 80
    evidence_ids: [EVD-019, EVD-011]
    investigation: hidden-business-rules
    superseded_by: KC-101
    notes: >
      This is High-tier under ADR-0005, because it is security- and
      policy-bearing. It needs the owner's agreement and a Constitution Law.
  - id: KC-097
    statement: >
      Accounts plus a backend plus location logging (KC-075) add these
      obligations: in-app and web account deletion (KC-093), Data safety
      disclosure of location collection, a privacy policy URL, and TLS on
      the API. Free hosting exists for the static pages (e.g. GitHub
      Pages).
    status: inference
    confidence: 80
    evidence_ids: [EVD-011, EVD-019, EVD-020]
    investigation: hidden-business-rules
  - id: KC-098
    statement: >
      Because the publisher and VM are in India, India's Digital Personal
      Data Protection Act 2023 likely applies to stored location data. It
      requires consent, purpose limitation and erasure on request. Its
      rules on map boundaries may also constrain how the app depicts
      India's borders.
    status: assumption
    confidence: 50
    evidence_ids: [EVD-013, EVD-011]
    investigation: hidden-business-rules
    notes: General knowledge only, not legal advice. To check before launch.
  - id: KC-099
    statement: >
      Zero-spend provider set that fits KC-068 at the ~300-download scale:
      OpenFreeMap tiles for online maps and city-sized offline packs
      (KC-087); Photon for typeahead search (KC-090), with Nominatim for
      reverse geocoding at ≤1 req/s (KC-088); the OSRM demo server for
      routing (KC-089); Open-Meteo for weather (KC-091). Search, routing
      and weather calls pass through a caching, rate-limiting proxy on the
      VM. That keeps aggregate traffic within each policy and lets a
      provider be swapped without an app release. The arrival alarm needs
      none of these services at run time (KC-095).
    status: inference
    confidence: 75
    evidence_ids: [EVD-014, EVD-015, EVD-016, EVD-017, EVD-018, EVD-013]
    investigation: technical-architecture
    notes: >
      This is a recommendation for M3/OV-004, not a decision. The weakest
      links are the OSRM demo's "non-commercial, reasonable" clause and
      Photon's throttling. The fallback once a budget exists (KC-068) is
      self-hosting region extracts on a larger machine. Offline packs
      larger than 50 MB (e.g. a whole region) need a self-hosted region
      PMTiles file on the VM (KC-094, about 31 GB free).
  - id: KC-100
    statement: >
      A mandatory sign-up wall would cost the primary audience (KC-066):
      reviewers who install to try the app. Accounts should be optional,
      with a guest mode that keeps data on-device and syncs once the user
      signs in.
    status: inference
    confidence: 70
    evidence_ids: [EVD-011]
    investigation: user-journeys
    superseded_by: KC-102
    notes: To confirm with the owner.
```

---

## EVD-022

```yaml
id: EVD-022
type: conversation
source: Owner decisions on Q-10–Q-12, the availability target and the freeze, 2026-11-02
received: 2026-11-02
summary: >
  The owner approves freezing DISC-BUSINESS-VISION and tells the agent to
  proceed with its recommendations on the open questions. The owner also
  accepts the proposed backend availability target.
pipeline: vision
processed: true
claims:
  - id: KC-101
    statement: >
      Location logging is limited to while the app is in use or a
      user-started arrival-alarm or navigation session is running. Nothing
      is logged while the app is closed and idle.
    status: fact
    confidence: 95
    evidence_ids: [EVD-022, EVD-019]
    investigation: hidden-business-rules
    notes: >
      The owner adopted the KC-096 recommendation in general terms ("proceed
      with your recommendations"), not word for word, hence 95.
  - id: KC-102
    statement: >
      Accounts are optional. A guest mode keeps data on-device, and signing
      in syncs it to the backend.
    status: fact
    confidence: 95
    evidence_ids: [EVD-022]
    investigation: user-journeys
    notes: Adopts KC-100 through the owner's general approval.
  - id: KC-103
    statement: >
      Accessibility options are in scope.
    status: fact
    confidence: 95
    evidence_ids: [EVD-022, EVD-011]
    investigation: business-vision
    notes: >
      Adopts KC-077 through the owner's general approval. KC-077 was the
      only position the agent had put forward on this question.
  - id: KC-104
    statement: >
      Backend availability target: best effort. The arrival alarm, offline
      maps and saved places must all work while the backend is
      unreachable.
    status: fact
    confidence: 100
    evidence_ids: [EVD-022]
    investigation: technical-architecture
  - id: KC-105
    statement: >
      The owner approved freezing DISC-BUSINESS-VISION at v0.2.0 content,
      released as v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-022]
    investigation: business-vision
```

---

## EVD-023

```yaml
id: EVD-023
type: conversation
source: Owner instruction on the review workflow, 2026-11-02
received: 2026-11-02
summary: >
  The owner delegates review and approval of Oriveda artifacts to an owner
  proxy agent. The proxy runs on Opus 5.5, argues with the drafting agent
  over multiple rounds until they agree, and pings the owner only for
  critical matters. Implementation is paused until the owner explicitly
  approves it. A comprehensive summary artifact is due once the Oriveda
  steps are done.
pipeline: vision
processed: true
claims:
  - id: KC-106
    statement: >
      An owner proxy agent (Opus 5.5) reviews and approves specifications
      on the owner's behalf, in rounds with the drafting agent until they
      agree. It escalates to the owner only for critical matters. The
      mechanism and escalation rules are set out in ADR-0001.
    status: fact
    confidence: 100
    evidence_ids: [EVD-023]
    investigation: recommendations
  - id: KC-107
    statement: >
      Implementation (M8) does not begin without the owner's explicit
      approval.
    status: fact
    confidence: 100
    evidence_ids: [EVD-023]
    investigation: recommendations
  - id: KC-108
    statement: >
      Once all Oriveda steps before implementation are done, the owner
      receives one comprehensive artifact summarising everything.
    status: fact
    confidence: 100
    evidence_ids: [EVD-023]
    investigation: recommendations
```

---

## EVD-024

```yaml
id: EVD-024
type: conversation
source: Owner-proxy review of DISC-FEATURE-INVENTORY, rounds 1–3, 2026-11-02 (ADR-0001)
received: 2026-11-02
summary: >
  The owner proxy reviewed DISC-FEATURE-INVENTORY over three rounds. It
  requested 6 + 2 MUST changes and 17 SHOULD changes, all applied, and
  approved v0.3.0, frozen as v1.0.0. There were no escalations. Main
  changes it drove: checkpoint alarms removed from the MVP (they
  reintroduce route dependence); neutral codenames; offline search;
  alarm Play/platform compliance; a distinct "location lost" alert in
  place of a false arrival; and the one-trip-session rule for multiple
  alarms.
pipeline: vision
processed: true
claims:
  - id: KC-109
    statement: >
      DISC-FEATURE-INVENTORY v1.0.0 is approved by the owner proxy on the
      owner's behalf (ADR-0001), with no escalations.
    status: fact
    confidence: 100
    evidence_ids: [EVD-024]
    investigation: feature-inventory
  - id: KC-110
    statement: >
      Open for hidden-business-rules: (1) the arrival distance used when
      an alarm is armed inside its radius; (2) whether location collected
      by the trip-session foreground service may also grow explored cells
      and the journal (secondary use under Play's foreground-service and
      prominent-disclosure rules).
    status: fact
    confidence: 100
    evidence_ids: [EVD-024]
    investigation: hidden-business-rules
  - id: KC-111
    statement: >
      Open for technical-architecture: a free elevation (DEM) source
      without a billing account for hillshade and 3D terrain. If none
      exists, terrain-sepia ships without hillshade and topo-mono is
      dropped.
    status: fact
    confidence: 100
    evidence_ids: [EVD-024]
    investigation: technical-architecture
```

---

## EVD-025

```yaml
id: EVD-025
type: repository
source: This repository and the GitHub API (repos/VedantJoshi23/mpbMap), observed 2026-11-02
received: 2026-11-02
summary: >
  A second observation of the repository after M1 work began. knowledge/
  is no longer empty; this supersedes that part of EVD-002's summary
  (EVD-002 itself is not edited).
pipeline: repository
processed: true
claims:
  - id: KC-112
    statement: >
      The GitHub repository VedantJoshi23/mpbMap is public.
    status: fact
    confidence: 100
    evidence_ids: [EVD-025]
    investigation: repo-structure
  - id: KC-113
    statement: >
      The Oriveda submodule at .oriveda-framework/ is pinned at commit
      a62b657.
    status: fact
    confidence: 100
    evidence_ids: [EVD-025]
    investigation: repo-structure
  - id: KC-114
    statement: >
      knowledge/ holds the evidence log, the frozen DISC- investigations,
      ADR-0001 and manifest.yaml. Oriveda's validator passes in target
      mode (--root) with 0 errors.
    status: fact
    confidence: 100
    evidence_ids: [EVD-025]
    investigation: repo-structure
  - id: KC-115
    statement: >
      reference/ holds five MapShifter store and in-app images (copyright
      Filipp Bazun, KC-025), the owner's inspiration table and info.md. As
      of 2026-11-02 it also has a credit and ownership notice
      (reference/README.md). The repository has no LICENSE file.
    status: fact
    confidence: 100
    evidence_ids: [EVD-025]
    investigation: repo-structure
```

---

## EVD-026

```yaml
id: EVD-026
type: conversation
source: Owner-proxy reviews, 2026-11-02 (ADR-0001). DISC-REPO-STRUCTURE rounds 1-2; DISC-HIDDEN-BUSINESS-RULES round 1
received: 2026-11-02
summary: >
  The proxy approved DISC-REPO-STRUCTURE for freezing, with its Q1 (the
  public third-party images and the project licence) escalated to the
  owner. In round 1 of DISC-HIDDEN-BUSINESS-RULES it escalated the map
  boundaries and the applicability of DPDP right away, because they
  constrain the M3 tile source and the M2 Laws. It answered that
  hidden-business-rules Q2 keeps R18 as an opt-in.
pipeline: vision
processed: true
claims:
  - id: KC-116
    statement: >
      DISC-REPO-STRUCTURE v1.0.0 is approved by the owner proxy (ADR-0001).
      Q1a (third-party images in the public reference/) and Q1b (the
      project licence) are escalated to the owner and pending.
    status: fact
    confidence: 100
    evidence_ids: [EVD-026]
    investigation: repo-structure
  - id: KC-117
    statement: >
      Escalated to the owner and pending: (a) how map boundaries are drawn
      (OSM as-is, an India-compliant layer, or no country boundary lines
      at all; the proxy recommends none); (b) whether the app is designed
      as if India's DPDP Act applies (the proxy recommends yes).
    status: fact
    confidence: 100
    evidence_ids: [EVD-026]
    investigation: hidden-business-rules
  - id: KC-118
    statement: >
      A disclosed, double opt-in secondary use of trip-session location for
      reveal is acceptable under Play's foreground-service and
      prominent-disclosure rules.
    status: assumption
    confidence: 55
    evidence_ids: [EVD-019, EVD-026]
    investigation: hidden-business-rules
    notes: >
      Play's text (KC-092) does not address secondary use of
      foreground-service location directly. Fallback if Play objects:
      reveal grows only while the app is in use.
```

---

## EVD-027

```yaml
id: EVD-027
type: conversation
source: Owner-proxy review of DISC-HIDDEN-BUSINESS-RULES round 2, 2026-11-02 (ADR-0001)
received: 2026-11-02
summary: >
  The proxy approved DISC-HIDDEN-BUSINESS-RULES for freezing with its Q1
  (KC-117) open, on condition that the downstream dependency is recorded
  and DISC-FEATURE-INVENTORY C12 is revised to match R18. It added a
  non-blocking stale-trip safeguard (R24).
pipeline: vision
processed: true
claims:
  - id: KC-119
    statement: >
      DISC-HIDDEN-BUSINESS-RULES v1.0.0 and DISC-FEATURE-INVENTORY v1.0.1
      (C12 narrowed: the journal uses in-use location only) are approved
      by the owner proxy (ADR-0001). KC-117 blocks the freeze of the M2
      Constitution and the M3 tile-provider decision.
    status: fact
    confidence: 100
    evidence_ids: [EVD-027]
    investigation: hidden-business-rules
```

---

## EVD-028

```yaml
id: EVD-028
type: conversation
source: Owner-proxy reviews, 2026-11-02 (ADR-0001). DISC-USER-JOURNEYS rounds 1-3; DISC-HIDDEN-BUSINESS-RULES v1.1.0; DISC-DATA-MODEL round 1
received: 2026-11-02
summary: >
  The proxy approved DISC-USER-JOURNEYS (freeze v1.0.0) and the
  DISC-HIDDEN-BUSINESS-RULES v1.1.0 revision. Both required a
  direct-boot-aware interrupted-trip alert, because an overnight restart
  leaves the phone locked. v1.1.0 also extends R21 erasure to the raw
  location log. Process note: a revision to a Frozen spec is reviewed as a
  draft before it is marked Frozen. The proxy answered the data model's
  Q1: a 30-day, on-device, clearable raw location log.
pipeline: vision
processed: true
claims:
  - id: KC-120
    statement: >
      DISC-USER-JOURNEYS v1.0.0 and DISC-HIDDEN-BUSINESS-RULES v1.1.0
      (R25 with direct boot, R26, R21 extended to raw location) are
      approved by the owner proxy (ADR-0001).
    status: fact
    confidence: 100
    evidence_ids: [EVD-028]
    investigation: user-journeys
  - id: KC-121
    statement: >
      The project's own data model (DISC-DATA-MODEL) replaces the
      reference-app entity inferences: Trip and Alarm live on the device
      only, in device-protected storage; the raw LocationFix log is a
      30-day on-device rolling log; Codex and explored cells sync; the
      server holds only the synced entities and Account.
    status: inference
    confidence: 70
    evidence_ids: [EVD-028, EVD-024, EVD-027]
    investigation: data-model
```

---

## EVD-029 to EVD-031: elevation, renderer and TLS research (KC-111)

```yaml
- id: EVD-029
  type: other
  source: https://registry.opendata.aws/terrain-tiles/ (read 2026-11-02, fetch digest)
  pipeline: other
  claims:
    - id: KC-122
      statement: >
        AWS Terrain Tiles (Mapzen/Tilezen) is a global bare-earth elevation
        tile set in the public S3 bucket elevation-tiles-prod, accessible
        with no AWS account. Attribution follows the tilezen/joerd
        attribution document.
      status: fact
      confidence: 85
      evidence_ids: [EVD-029]
      investigation: technical-architecture
      notes: >
        The tile encodings (e.g. terrarium) and the URL pattern were not
        shown on the fetched page. To be confirmed in the M3 spike.
- id: EVD-030
  type: other
  source: MapLibre style spec terrain page; MapLibre newsletters Dec 2025 and Feb 2026 (web search 2026-11-02)
  pipeline: other
  claims:
    - id: KC-123
      statement: >
        3D terrain is listed as unsupported on MapLibre Native Android and
        is still in progress (roadmap issue #252, December 2025). Hillshade
        from raster-dem sources is supported, with extra hillshade
        algorithms merged by February 2026. MapLibre Android 13 defaults to
        a Vulkan renderer, with an OpenGL ES variant available.
      status: fact
      confidence: 75
      evidence_ids: [EVD-030]
      investigation: technical-architecture
      notes: Search digest. The current release must be checked in M3.
- id: EVD-031
  type: other
  source: Read-only inspection of the VM's nginx configuration, 2026-11-02
  pipeline: repository
  claims:
    - id: KC-124
      statement: >
        nginx on the VM already terminates TLS with Let's Encrypt
        certificates for several of the owner's existing domains and
        subdomains. The hostnames are deliberately not recorded in this
        public repository (DISC-REPO-STRUCTURE hygiene rule).
      status: fact
      confidence: 95
      evidence_ids: [EVD-031]
      investigation: technical-architecture
```

---

## EVD-032

```yaml
id: EVD-032
type: conversation
source: Owner-proxy reviews, 2026-11-02 (ADR-0001). DISC-DATA-MODEL round 2; DISC-DOMAIN-DISCOVERY round 1
received: 2026-11-02
summary: >
  The proxy approved DISC-DATA-MODEL, which is frozen with a minimal DP
  record (TripBootRecord, no coordinates). It requested changes to
  DISC-DOMAIN-DISCOVERY: the demo trip runs in a sandbox, BC1 owns
  alerting, erasure crosses into BC2, five events were missing, a
  Platform Surfaces context is reserved, and the reference-app context
  claims are superseded.
pipeline: vision
processed: true
claims:
  - id: KC-125
    statement: >
      DISC-DATA-MODEL v1.0.0 is approved by the owner proxy (ADR-0001).
    status: fact
    confidence: 100
    evidence_ids: [EVD-032]
    investigation: data-model
  - id: KC-126
    statement: >
      The project's own bounded contexts and domain events
      (DISC-DOMAIN-DISCOVERY) replace the reference-app inferences:
      BC1 Trip & Alarms (core, UI-independent, owns alerting) through
      BC10 Provider Gateway, with a reserved Platform Surfaces context.
    status: inference
    confidence: 65
    evidence_ids: [EVD-032, EVD-028]
    investigation: domain-discovery
```

---

## EVD-033

```yaml
id: EVD-033
type: conversation
source: Owner-proxy reviews, 2026-11-02 (ADR-0001). DISC-DOMAIN-DISCOVERY round 2; DISC-TECHNICAL-ARCHITECTURE round 1
received: 2026-11-02
summary: >
  The proxy approved DISC-DOMAIN-DISCOVERY for freeze. On
  DISC-TECHNICAL-ARCHITECTURE it required: the device-store row to match
  the Frozen data model, and abuse protection for the gateway, with Play
  Integrity flagged as a commercial free tier. It reframed the domain
  question as owner input batched with Q-08, not a rule-4 escalation.
pipeline: vision
processed: true
claims:
  - id: KC-127
    statement: >
      DISC-DOMAIN-DISCOVERY v1.0.0 is approved by the owner proxy
      (ADR-0001).
    status: fact
    confidence: 100
    evidence_ids: [EVD-033]
    investigation: domain-discovery
  - id: KC-128
    statement: >
      Pending owner input, to be asked together with Q-08 (the app name
      and package): which of the owner's existing domains hosts the API
      subdomain. Interim default: GitHub Pages for the static pages.
    status: fact
    confidence: 100
    evidence_ids: [EVD-033]
    investigation: technical-architecture
```

---

## EVD-034

```yaml
id: EVD-034
type: conversation
source: Owner-proxy (successor #2) reviews, 2026-11-02 (ADR-0001); the agent's owner batch sent the same day
received: 2026-11-02
summary: >
  The proxy approved DISC-TECHNICAL-ARCHITECTURE, DISC-TECHNICAL-DEBT and
  DISC-RECOMMENDATIONS, which completes the ten M1 investigations. Its key
  finding: a force-stop or OEM deep clean is a residual silent-failure
  path, recorded honestly as a known limit (TD6), with a late-detection
  safeguard proposed for M3.
pipeline: vision
processed: true
claims:
  - id: KC-129
    statement: >
      DISC-TECHNICAL-ARCHITECTURE v1.0.0, DISC-TECHNICAL-DEBT v1.0.0 and
      DISC-RECOMMENDATIONS v1.0.0 are approved by the owner proxy
      (ADR-0001). All ten M1 investigations are Frozen.
    status: fact
    confidence: 100
    evidence_ids: [EVD-034]
    investigation: recommendations
  - id: KC-130
    statement: >
      On 2026-11-02 the agent sent the owner one batch of pending decisions:
      KC-116 (the reference images and the licence), KC-117 (boundaries and
      DPDP), Q-08 (the app name and Play package), KC-128 (the API domain)
      and OV-002's three owner-only Constitution choices (rigor level, AI
      collaboration mode, architecture ambition), each with a
      recommendation.
    status: fact
    confidence: 100
    evidence_ids: [EVD-034]
    investigation: recommendations
```

---

## EVD-035

```yaml
id: EVD-035
type: conversation
source: Owner-proxy (successor #2) review of CONST-CORE rounds 1-2, 2026-11-02 (ADR-0001)
received: 2026-11-02
summary: >
  The proxy approved CONST-CORE v0.2.0 in substance: the philosophy and
  Laws 1-14. Changes it drove: Law 5 narrowed to raw location logs, with
  the transient provider-query clause; route-aware refinement may only
  fire an alarm earlier; new Law 13 (core boundaries) and Law 14 (the
  alarm is accessible); Law 12 reduced to whatever the owner's KC-117
  answer adds.
pipeline: vision
processed: true
claims:
  - id: KC-131
    statement: >
      CONST-CORE Laws 1-14 are approved in substance by the owner proxy
      (ADR-0001). The freeze is pending: the owner's answers to the
      sections 2-4 choices (KC-130), KC-116 and KC-117, and the owner's
      own sign-off (ADR-0001 rule 3).
    status: fact
    confidence: 100
    evidence_ids: [EVD-035]
    investigation: recommendations
```

---

## EVD-036

```yaml
id: EVD-036
type: conversation
source: Owner answers to the KC-130 decision batch, 2026-11-02
received: 2026-11-02
summary: >
  The owner settles the reference images, the licence, DPDP, rigor level,
  AI collaboration mode and architecture ambition. The owner leans toward
  OSM's own borders but asks why the agent recommended none before
  confirming. The owner proposes the name "Orimap". The owner has no
  domain, and asks whether the package name refers to the Play account
  name.
pipeline: vision
processed: true
claims:
  - id: KC-132
    statement: >
      The MapShifter images stay in the public reference/, with the credit
      notice, because they show the concept inspiration (KC-116 Q1a: option
      (a)).
    status: fact
    confidence: 100
    evidence_ids: [EVD-036]
    investigation: repo-structure
  - id: KC-133
    statement: >
      The project's own code and art are licensed under Apache-2.0
      (KC-116 Q1b). The reference images are excluded through NOTICE.
    status: fact
    confidence: 100
    evidence_ids: [EVD-036]
    investigation: repo-structure
  - id: KC-134
    statement: >
      Design as if India's DPDP Act 2023 applies (KC-117 b: yes).
    status: fact
    confidence: 100
    evidence_ids: [EVD-036]
    investigation: hidden-business-rules
  - id: KC-135
    statement: >
      Constitution owner choices (OV-002): rigor level Production-grade; AI
      collaboration mode Principal Engineer; architecture ambition Modular
      Monolith.
    status: fact
    confidence: 100
    evidence_ids: [EVD-036]
    investigation: recommendations
  - id: KC-136
    statement: >
      The owner has no domain (KC-128). The API host must be domain-free
      or use a free subdomain service.
    status: fact
    confidence: 100
    evidence_ids: [EVD-036]
    investigation: technical-architecture
  - id: KC-137
    statement: >
      Pending owner confirmation: map borders (the owner leans toward OSM
      as-is, KC-117 a); the app name "Orimap" (Q-08); the Play package
      name.
    status: fact
    confidence: 100
    evidence_ids: [EVD-036]
    investigation: business-vision
```

---

## EVD-037 to EVD-038: naming and boundary-data checks (2026-11-02)

```yaml
- id: EVD-037
  type: other
  source: Web search for "Orimap" app (2026-11-02)
  pipeline: competitor
  claims:
    - id: KC-138
      statement: >
        An existing iOS/iPad App Store app is named "OriMap: Your Map
        Diary": a map-based memory journal, free with in-app purchases,
        whose policies are hosted at lineash.github.io. The search did not
        cover Play Store availability.
      status: fact
      confidence: 80
      evidence_ids: [EVD-037]
      investigation: business-vision
- id: EVD-038
  type: other
  source: https://openmaptiles.org/schema/ boundary layer (2026-11-02, fetch digest)
  pipeline: other
  claims:
    - id: KC-139
      statement: >
        The OpenMapTiles boundary layer carries `disputed` (0/1),
        `disputed_name` (e.g. IndianClaim-North,
        IndianClaimwesternKashmir, PakistaniClaim, ChineseClaim, Demchok)
        and `claimed_by` (the ISO2 code of the country that wants to see
        the line) for country boundaries (admin_level 2). A map style can
        therefore hide disputed lines, or show a specific country's
        claimed view, at no extra cost.
      status: fact
      confidence: 85
      evidence_ids: [EVD-038]
      investigation: technical-architecture
      notes: >
        Whether OpenFreeMap's tiles carry these fields unchanged (KC-087
        says "unmodified OpenMapTiles") is to be verified in M3 spike S2
        or S4.
```

---

## EVD-039

```yaml
id: EVD-039
type: conversation
source: Owner-proxy (successor #2) review, 2026-11-02 (ADR-0001). CONST-CORE v0.4.0; draft revisions to Frozen DISC-DATA-MODEL, DISC-TECHNICAL-ARCHITECTURE and DISC-TECHNICAL-DEBT
received: 2026-11-02
summary: >
  The proxy approved CONST-CORE v0.4.0. The freeze still waits on the
  owner's boundary answer and sign-off. It also approved, as drafts before
  application, the v1.1.0 revisions of DISC-DATA-MODEL (wider consent),
  DISC-TECHNICAL-ARCHITECTURE (no-domain hosting with a GitHub Pages
  bootstrap file) and DISC-TECHNICAL-DEBT (TD16, DDNS hostname risk).
pipeline: vision
processed: true
claims:
  - id: KC-140
    statement: >
      CONST-CORE v0.4.0 is approved by the owner proxy. The freeze is
      pending the owner's boundary answer (KC-137), then a short proxy
      confirmation and the owner's sign-off (ADR-0001 rule 3).
    status: fact
    confidence: 100
    evidence_ids: [EVD-039]
    investigation: recommendations
  - id: KC-141
    statement: >
      DISC-DATA-MODEL v1.1.0, DISC-TECHNICAL-ARCHITECTURE v1.1.0 and
      DISC-TECHNICAL-DEBT v1.1.0 are approved by the owner proxy as
      reviewed draft revisions of Frozen specs (ADR-0001).
    status: fact
    confidence: 100
    evidence_ids: [EVD-039]
    investigation: technical-architecture
```

---

## EVD-040

```yaml
id: EVD-040
type: conversation
source: Owner answers on borders, name and package, 2026-11-02
received: 2026-11-02
summary: >
  The owner chooses no border drawing. The owner asks for name suggestions
  rooted in a historic phenomenon, person or ritual, and will pick one.
  The package is io.github.vedantjoshi23.<name>, once the name is
  confirmed.
pipeline: vision
processed: true
claims:
  - id: KC-142
    statement: >
      Map boundaries: no boundary lines are drawn (KC-117 a: option iii).
      The agent applies it to administrative boundaries at every level,
      because disputed areas also affect sub-national lines. Coastlines and
      land use are unaffected.
    status: fact
    confidence: 95
    evidence_ids: [EVD-040]
    investigation: hidden-business-rules
    superseded_by: KC-144
    notes: >
      The owner said "no borders drawing". Applying it to every admin level
      is the agent's reading and is surfaced for confirmation.
  - id: KC-143
    statement: >
      The app name will be chosen by the owner from suggestions rooted in a
      historic phenomenon, person or ritual. The Play package is
      io.github.vedantjoshi23.<name>. "Orimap" is dropped because of the
      KC-138 collision.
    status: fact
    confidence: 95
    evidence_ids: [EVD-040]
    investigation: business-vision
```

---

## EVD-041

```yaml
id: EVD-041
type: conversation
source: Owner-proxy (successor #2) Law 12 confirmation round, 2026-11-02 (ADR-0001)
received: 2026-11-02
summary: >
  The proxy split KC-142 into the owner's fact and the agent's reading,
  widened the boundary clause to every map surface, and found no
  remaining substantive blocker to freezing CONST-CORE apart from the
  owner's sign-off, which must include confirming the every-level reading.
pipeline: vision
processed: true
claims:
  - id: KC-144
    statement: >
      The owner chose "no borders drawing" (KC-117 a: option iii).
    status: fact
    confidence: 100
    evidence_ids: [EVD-040]
    investigation: hidden-business-rules
  - id: KC-145
    statement: >
      KC-144 applies to administrative boundaries at every level, maritime
      included, on every map the app renders and every store-listing
      image, because disputed areas also affect sub-national lines.
    status: inference
    confidence: 90
    evidence_ids: [EVD-040, EVD-041, EVD-038]
    investigation: hidden-business-rules
    superseded_by: KC-149
    notes: Confirmed by the owner's rule-3 sign-off on CONST-CORE (pending).
  - id: KC-146
    statement: >
      CONST-CORE v0.5.0 (Laws 1-14, Architecture Review) is approved by the
      owner proxy. The only remaining freeze condition is the owner's own
      sign-off, including confirmation of KC-145 (ADR-0001 rule 3).
    status: fact
    confidence: 100
    evidence_ids: [EVD-041]
    investigation: recommendations
```

---

## EVD-042

```yaml
id: EVD-042
type: conversation
source: Owner decision and Constitution sign-off, 2026-11-02
received: 2026-11-02
summary: >
  The owner names the app Etak and instructs the agent to freeze the
  Constitution (ADR-0001 rule 3 sign-off).
pipeline: vision
processed: true
claims:
  - id: KC-147
    statement: >
      The app is named "Etak", after the Micronesian navigation technique
      (and the 1985 Etak Navigator). The Play package is
      io.github.vedantjoshi23.etak, which is permanent once published.
    status: fact
    confidence: 100
    evidence_ids: [EVD-042]
    investigation: business-vision
    superseded_by: KC-167
    notes: >
      Flagged to the owner before the choice: the historic Etak company's
      assets passed into the TomTom group, so a trademark may still be
      live. No Android app named Etak was found (search only).
  - id: KC-148
    statement: >
      The owner signs off CONST-CORE and instructs it to be frozen (v1.0.0).
    status: fact
    confidence: 100
    evidence_ids: [EVD-042]
    investigation: recommendations
  - id: KC-149
    statement: >
      No administrative boundary lines, at any level, maritime included,
      are drawn on any map the app renders or the store listing shows.
      Accepted by the owner through sign-off of CONST-CORE's Law 12 text,
      which states this scope.
    status: fact
    confidence: 95
    evidence_ids: [EVD-042, EVD-041]
    investigation: hidden-business-rules
    notes: >
      The owner did not restate the scope separately; it was accepted as
      part of the Law 12 text they signed off. It can be revisited by an
      amendment ADR.
```

---

## EVD-043

```yaml
id: EVD-043
type: conversation
source: Owner-proxy (successor #2) review of ARCH-SYSTEM rounds 1-2, 2026-11-02 (ADR-0001)
received: 2026-11-02
summary: >
  The proxy approved ARCH-SYSTEM. Changes it drove: single ownership of
  ConsentRecord (BC2); a direct-boot constraint so a cold process cannot
  crash before the R25 alert; feature api/ boundaries checkable in CI;
  per-context UI; one backend deployable; the L1-L14 conformance table.
pipeline: vision
processed: true
claims:
  - id: KC-150
    statement: >
      ARCH-SYSTEM v1.0.0 is approved by the owner proxy (ADR-0001). All
      sections are Frozen. Implementation still needs the owner's explicit
      approval (KC-107).
    status: fact
    confidence: 100
    evidence_ids: [EVD-043]
    investigation: technical-architecture
```

---

## EVD-044

```yaml
id: EVD-044
type: conversation
source: Owner answer to the OV-004 stack-freedom question, 2026-11-02
received: 2026-11-02
summary: >
  The owner chooses Hybrid stack freedom and delegates each per-layer
  approval to the owner proxy "after careful considerations". The owner
  accepts the constraint preferences the agent listed.
pipeline: vision
processed: true
claims:
  - id: KC-151
    statement: >
      OV-004 stack freedom: Hybrid. For each layer the agent compares 2-3
      production-grade options and recommends one, and the owner proxy
      approves it on the owner's behalf after careful review (ADR-0001).
      Constraint preferences: free and open-source first, well maintained,
      industry-standard with active communities, avoid vendor lock-in
      where practical, prefer self-hostable when mature, zero spend until
      300+ downloads (KC-068).
    status: fact
    confidence: 100
    evidence_ids: [EVD-044]
    investigation: technical-architecture
```

---

## EVD-045

```yaml
id: EVD-045
type: conversation
source: Owner-proxy (successor #2) technology decisions, ADR-0002 to ADR-0009, 2026-11-02 (ADR-0001, KC-151)
received: 2026-11-02
summary: >
  The proxy decided all eight technology ADRs and froze them now, with
  spike-confirmation clauses (a spike can only choose among named
  fallbacks). Key changes: insistent looping alarm audio (the FGS on the
  alarm stream, or FLAG_INSISTENT); a heartbeat-based watchdog; remote
  config limited to source endpoints, with bundled linted styles and a
  runtime boundary guard; persisted idempotent erasure; Keystore-wrapped
  refresh tokens. All choices are zero-spend. Flagged free tiers: Play
  Integrity, Oracle object storage, DuckDNS.
pipeline: vision
processed: true
claims:
  - id: KC-152
    statement: >
      ADR-0002 is decided and approved by the owner proxy under KC-151
      (Hybrid stack freedom) and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-045]
    investigation: technical-architecture
  - id: KC-153
    statement: >
      ADR-0003 is decided and approved by the owner proxy under KC-151
      (Hybrid stack freedom) and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-045]
    investigation: technical-architecture
  - id: KC-154
    statement: >
      ADR-0004 is decided and approved by the owner proxy under KC-151
      (Hybrid stack freedom) and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-045]
    investigation: technical-architecture
  - id: KC-155
    statement: >
      ADR-0005 is decided and approved by the owner proxy under KC-151
      (Hybrid stack freedom) and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-045]
    investigation: technical-architecture
  - id: KC-156
    statement: >
      ADR-0006 is decided and approved by the owner proxy under KC-151
      (Hybrid stack freedom) and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-045]
    investigation: technical-architecture
  - id: KC-157
    statement: >
      ADR-0007 is decided and approved by the owner proxy under KC-151
      (Hybrid stack freedom) and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-045]
    investigation: technical-architecture
  - id: KC-158
    statement: >
      ADR-0008 is decided and approved by the owner proxy under KC-151
      (Hybrid stack freedom) and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-045]
    investigation: technical-architecture
  - id: KC-159
    statement: >
      ADR-0009 is decided and approved by the owner proxy under KC-151
      (Hybrid stack freedom) and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-045]
    investigation: technical-architecture
```

---

## EVD-046

```yaml
id: EVD-046
type: conversation
source: Owner-proxy (successor #3) rounds 1-2, 2026-11-02 (ADR-0001). DISC-DATA-MODEL v1.2.0; the repair of commit b482fe8; M4 Standards
received: 2026-11-02
summary: >
  The proxy approved DISC-DATA-MODEL v1.2.0 (TripBootRecord heartbeat with
  a fail-safe reading). It found that the agent's revision script in
  commit b482fe8 had written a literal "\\1" over five lines of three
  Frozen files, deleting the TD15 row among them, and required a verbatim
  restoration as non-semantic patches. It also approved all twelve M4
  Standards after two rounds.
pipeline: vision
processed: true
claims:
  - id: KC-160
    statement: >
      DISC-DATA-MODEL v1.2.0 is approved by the owner proxy. The watchdog
      receiver lives in feature/trip/platform/boot; ARCH-SYSTEM §2 cites
      v1.1.0 and will be updated at its next revision.
    status: fact
    confidence: 100
    evidence_ids: [EVD-046]
    investigation: data-model
  - id: KC-161
    statement: >
      Lines lost in commit b482fe8 (a regex callback that inserted a
      literal backreference) were restored verbatim from b482fe8^:
      DISC-DATA-MODEL (2 lines, inside v1.2.0), DISC-TECHNICAL-ARCHITECTURE
      v1.1.1 (1 heading) and DISC-TECHNICAL-DEBT v1.1.1 (the TD15 row and
      1 heading). A repository-wide grep for backreferences is clean. The
      one-off script lived only in the scratchpad and is not reused.
    status: fact
    confidence: 100
    evidence_ids: [EVD-046]
    investigation: technical-debt
  - id: KC-162
    statement: >
      All twelve M4 Standards (STD-INDEX and STD-CODE through STD-SKINS)
      are approved by the owner proxy and Frozen at v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-046]
    investigation: recommendations
```

---

## EVD-047

```yaml
id: EVD-047
type: conversation
source: Owner-proxy (successor #3) round 3, 2026-11-02 (ADR-0001). ARCH-SYSTEM v1.1.0 draft; M5 domain specs
received: 2026-11-02
summary: >
  The proxy approved the ARCH-SYSTEM v1.1.0 revision, after reviewing it as
  a draft, and the twelve domain specs, allowing whole-spec freezes with
  spike clauses for TRIP (S3), MAP (S1) and OFFLINE (S4).
pipeline: vision
processed: true
claims:
  - id: KC-163
    statement: >
      ARCH-SYSTEM v1.1.0 is approved by the owner proxy and Frozen: core/config
      in the kernel with a non-user cache; ACRA ingest in BC10; app/ as the
      composition root, holding the port adapters; the erasure mechanism;
      the declared api-level dependencies.
    status: fact
    confidence: 100
    evidence_ids: [EVD-047]
    investigation: technical-architecture
  - id: KC-164
    statement: >
      All twelve M5 domain specifications (DOM-KERNEL through
      DOM-PLATFORM-SURFACES) are approved by the owner proxy and Frozen at
      v1.0.0.
    status: fact
    confidence: 100
    evidence_ids: [EVD-047]
    investigation: domain-discovery
```

---

## EVD-048

```yaml
id: EVD-048
type: conversation
source: Owner-proxy (successor #4) M6 review, rounds 1-2, 2026-11-02 (ADR-0001)
received: 2026-11-02
summary: >
  The proxy approved the twelve M6 features and four draft revisions of
  Frozen specs, after two rounds. Main changes it drove: both spec cards
  converted to full specs; FEAT-LOCAL-ERASURE and FEAT-PROVIDER-GATEWAY
  split out; snooze defined (a fixed 2 minutes, stays armed); the safety
  debug log given an owner; sync applied by the owning contexts;
  epoch-then-union for cells; the demo publishes no BC1 events; slices
  1-4 planned.
pipeline: vision
processed: true
claims:
  - id: KC-165
    statement: >
      Approved by the owner proxy, after review as drafts:
      DOM-TRIP v1.1.0 (snooze, the safety debug log, demo bus-silence);
      DOM-EXPLORATION v1.1.0 (applyRemote, the safety-log erasure part);
      DOM-CODEX v1.1.0 (applyRemote); ARCH-SYSTEM v1.2.0; DISC-DATA-MODEL
      v1.3.0 (SafetyLogEntry); STD-DATABASE v1.0.1 (7-day log retention).
    status: fact
    confidence: 100
    evidence_ids: [EVD-048]
    investigation: technical-architecture
  - id: KC-166
    statement: >
      All twelve M6 feature specifications are approved by the owner proxy
      and Frozen at v1.0.0. M6 Definition of Done: met. The Oriveda
      examples/ worked-example item is not applicable to the target
      project, because it is the framework's own deliverable (Oriveda
      ADR-0002) and the project cannot write into the submodule.
      Implementation, including spikes, still needs the owner's explicit
      M8 approval (KC-107).
    status: fact
    confidence: 100
    evidence_ids: [EVD-048]
    investigation: recommendations
```

---

## EVD-049

```yaml
id: EVD-049
type: conversation
source: Owner instruction, 2026-11-02 ("Let's rename the project to kairos. As Etak trademarks may cause trouble later")
received: 2026-11-02
summary: >
  The owner renames the app from Etak to Kairos because of the Etak
  trademark risk. Recorded as ADR-0010, which also serves as the
  CONST-CORE amendment record.
pipeline: vision
processed: true
claims:
  - id: KC-167
    statement: >
      The app is named "Kairos" (Greek: the opportune moment). The Play
      package is io.github.vedantjoshi23.kairos. Supersedes KC-147.
    status: fact
    confidence: 100
    evidence_ids: [EVD-049]
    investigation: business-vision
    notes: >
      A web search on 2026-11-02 found no Android maps or navigation app
      named Kairos. There is an F-Droid task planner "Kairos"
      (com.skohscripts.kairos) in a different category. A formal
      trademark check is still due before the Play listing.
```

---

## Investigation Coverage (OV-000 exit criteria)

| Investigation | Best claims | Status |
| --- | --- | --- |
| `repo-structure` | KC-007, KC-008, KC-112–115 (fact) | **Frozen** as DISC-REPO-STRUCTURE v1.0.0 (KC-116). Q1 escalated. |
| `business-vision` | KC-002, KC-003, KC-056–059, KC-066–070, KC-076, KC-078, KC-103 (fact) | **Frozen** as DISC-BUSINESS-VISION v1.0.0 (KC-105). |
| `feature-inventory` | KC-071, KC-072, KC-074, KC-079, KC-083 (fact) | **Frozen** as DISC-FEATURE-INVENTORY v1.0.1 (KC-109, KC-119). |
| `user-journeys` | KC-061, KC-102 (fact), KC-062 (inference) | **Frozen** as DISC-USER-JOURNEYS v1.0.0 (KC-120). Designed, not observed (KC-062). |
| `data-model` | KC-073 (fact), KC-048, KC-049 (inference) | **Frozen** as DISC-DATA-MODEL v1.0.0 (KC-125). |
| `domain-discovery` | KC-050, KC-051 (inference) | **Frozen** as DISC-DOMAIN-DISCOVERY v1.0.0 (KC-127). |
| `technical-architecture` | KC-081, KC-085, KC-087–091, KC-104 (fact); KC-086, KC-099 (inference) | **Frozen** as DISC-TECHNICAL-ARCHITECTURE v1.0.0 (KC-129). |
| `hidden-business-rules` | KC-075, KC-080, KC-092, KC-093, KC-101 (fact); KC-095, KC-097 (inference) | **Frozen** as DISC-HIDDEN-BUSINESS-RULES v1.0.0 (KC-119). KC-117 escalated. |
| `technical-debt` | KC-035 (inference) | **Frozen** as DISC-TECHNICAL-DEBT v1.0.0 (KC-129). |
| `recommendations` | KC-106–108, KC-129, KC-130 (fact) | **Frozen** as DISC-RECOMMENDATIONS v1.0.0 (KC-129). **M1 complete.** |

## Open Questions and Gaps (per OV-000 gap detection)

Ranked by how much they block downstream work.

- **Q-01, Q-02, Q-03:** *Resolved* (KC-056–KC-060).
- **Q-09: Product posture.** *Resolved* by KC-075. Follow-ups are Q-10
  and Q-11.
- **Q-10: Scope of location logging.** *Resolved* by KC-101: only while
  the app is in use or an alarm or navigation session is running.
- **Q-11: Optional accounts.** *Resolved* by KC-102: guest mode, with
  sign-in optional.
- **Q-12: Accessibility.** *Resolved* by KC-103: in scope.
- **Q-13: Backend availability target.** *Resolved* by KC-104: best
  effort, with core features working offline.
- **Q-08: Identity.** The app name and its own skin names. Needed before
  the first store listing, not before Discovery.
- **Q-04: Data providers.** A recommendation exists (KC-099). The decision
  is made in M3/OV-004.
- **Q-05–Q-07:** *No longer needed* (KC-062).
