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

## Investigation Coverage (OV-000 exit criteria)

| Investigation | Best claims | Status |
| --- | --- | --- |
| `repo-structure` | KC-007, KC-008 (fact) | Covered. The project is greenfield. |
| `business-vision` | KC-002, KC-003, KC-056–059, KC-066–070, KC-076, KC-078, KC-103 (fact) | **Frozen** as DISC-BUSINESS-VISION v1.0.0 (KC-105). |
| `feature-inventory` | KC-071, KC-072, KC-074, KC-079, KC-083 (fact) | Covered. The project's own feature set is now stated by the owner. |
| `user-journeys` | KC-061, KC-102 (fact), KC-062 (inference) | Visible gap, not blocking. Journeys are designed in M5/M6. |
| `data-model` | KC-073 (fact), KC-048, KC-049 (inference) | Covered. Storage of the explored-area record is open. |
| `domain-discovery` | KC-050, KC-051 (inference) | Inference only. Needs an update for the arrival alarm and accounts. |
| `technical-architecture` | KC-081, KC-085, KC-087–091, KC-104 (fact); KC-086, KC-099 (inference) | Covered. A provider recommendation exists (KC-099). |
| `hidden-business-rules` | KC-075, KC-080, KC-092, KC-093, KC-101 (fact); KC-095, KC-097 (inference) | Covered. KC-098 is assumption-tier. |
| `technical-debt` | KC-035 (inference) | Not applicable yet (greenfield). |
| `recommendations` | KC-053, KC-055, KC-063–065, KC-096, KC-099, KC-100 (inference) | Covered. |

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
