---
id: ADR-0003
title: "Map Rendering and Map Data"
version: 0.1.0
status: Draft
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
(zero spend) and the KC-151 preferences. Lifecycle: **Draft**.

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
