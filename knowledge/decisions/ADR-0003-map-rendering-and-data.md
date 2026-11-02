---
id: ADR-0003
title: "Map Rendering and Map Data"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M3
category: Decision
priority: Critical
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
risk: High
complexity: High
---

# ADR-0003: Map Rendering and Map Data

Technology decision under Oriveda `OV-004`, with **Hybrid** stack freedom
(KC-151): options are compared, one is recommended, and the owner proxy
decides (ADR-0001). Constraints: CONST-CORE (Frozen), especially Law 7
(zero spend) and the KC-151 preferences. Lifecycle: **Frozen** v1.0.0 (2026-11-02), decided by the owner proxy
under KC-151 (EVD-045). Freezing does not authorise spikes or code
(KC-107).

## Context

Vector skins with live switching, 3D building extrusion, hillshade, a 2D
fallback (C1, C2), offline packs (C9), no boundary lines (Law 12), visible
attribution (R11), zero spend (Law 7) and smoothness (KC-070). Evidence:
KC-087 (OpenFreeMap), KC-094 (Protomaps), KC-122 (Terrain Tiles), KC-123
(MapLibre Android capabilities), KC-139 (boundary-layer fields).

## Options Considered

**Renderer**
- **MapLibre Native Android.** Open source (BSD), vector styles, runtime
  style switching, fill-extrusion, hillshade, offline regions and an
  attribution control. No 3D terrain on Android yet (KC-123). Version 13
  defaults to Vulkan, with an OpenGL ES variant.
- **Mapbox Maps SDK.** Mature, with 3D terrain. But it needs a token,
  metering and billing past a free tier: lock-in, and against Law 7.
- **osmdroid.** Raster only: no vector skins, no extrusion. Fails C1.

**Tiles**
- **OpenFreeMap.** Free, no key, OpenMapTiles schema, offline packs of
  50 MB / 3,000 tiles, no SLA (TD1).
- **Self-hosted Protomaps PMTiles on the VM.** Free software, but a
  regional extract consumes VM disk and bandwidth. Kept as the TD1
  fallback and for region packs after the MVP.
- **MapTiler free tier.** Keyed, quota-limited and commercial: lock-in
  risk.

**Elevation:** AWS Terrain Tiles (no account) versus a keyed commercial
DEM.

## Decision

- **MapLibre Native Android.** Spike S1 picks Vulkan or the OpenGL ES
  variant.
- **OpenFreeMap** for online tiles and city packs.
- **AWS Terrain Tiles** for hillshade, bundled into packs (S4).
- Skins are MapLibre style JSON over the OpenMapTiles schema, in
  `skins/`. **Every skin omits the boundary layer** (Law 12, KC-149). A
  style lint check in CI fails any skin that references the `boundary`
  layer.
- 3D terrain stays excluded until MapLibre Android supports it (TD7).

### Amendments from owner-proxy round 1

- **Remote config scope (Law 12):** remote config (the bootstrap file)
  may change only **tile, glyph and DEM source endpoints**. Skin styles
  are **bundled and CI-linted**, never fetched whole.
- **Runtime boundary guard:** BC3 removes any layer whose `source-layer`
  is `boundary` before rendering. This is defence in depth against
  mistakes and upstream schema changes.
- **Glyphs and sprites** are bundled as app assets (`asset://`), so labels
  work offline (Law 9) and nothing needs hosting.
- **The CI style lint also fails** any skin that hides or removes the
  attribution control (R11).
- **Spike clause:** confirmed by S1 (Vulkan or GL ES; the 3D roster) and
  S4 (packs plus DEM). A failure picks the named fallback through a draft
  revision.

## Consequences

- Zero spend, no keys, and an open stack end to end.
- Skins are portable style files.
- Fragility is TD1 and TD11. The bootstrap file and the gateway allow a
  tile-source switch without an app release only if the style URL is also
  remote-configurable. This ADR requires that.

## Revisit Criteria

OpenFreeMap's policy or availability changes (TD1); MapLibre Android
ships 3D terrain (TD7); S1 misses the smoothness targets on target
devices.

## Architecture Review (2026-11-02)

Checked against CONST-CORE and ARCH-SYSTEM (both Frozen), with no conflict. Laws 7, 9, 11 and 12 are satisfied (bundled linted styles plus the runtime guard).
Divergences: none (no code). Worked example under Oriveda `examples/`: not
applicable (Oriveda ADR-0002). Result: passes.
