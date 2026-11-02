---
id: STD-SKINS
title: "Skin Authoring"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M4
category: Standard
priority: High
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions: []
tags:
  - standard
risk: Medium
complexity: Low
---

# STD-SKINS: Skin Authoring

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Skin bundles under `skins/<codename>/`: style JSON, fonts, icons,
textures and metadata.

## Rules

1. **Assets are original or openly licensed** (OFL fonts, CC0 or CC-BY
   textures). Each bundle has a `LICENSES.md` listing every asset and its
   licence. Nothing is taken from games. No codename or public name
   echoes a franchise or a MapShifter skin (Law 11, C2).
2. **The style never references the `boundary` source layer** (Law 12).
   This is checked by the CI style lint.
3. Each skin's `meta.json` declares:
   - codename;
   - version;
   - `is3D`;
   - `fallback2D`;
   - font set;
   - icon set;
   - accents;
   - attribution text.
4. **Attribution control** is enabled in every style (R11).
5. **Performance:** meets STD-PERFORMANCE. 3D skins must name a 2D
   fallback.
6. **Accessibility:**
   - POI icons differ by shape;
   - label halos keep text at 4.5:1 or better;
   - one high-contrast variant is maintained (STD-ACCESSIBILITY).
7. **Style URL:** styles reference tiles through the remote-configurable
   source from the bootstrap file (ADR-0003). No hard-coded third-party
   keys.

## Examples

- Compliant: `skins/terrain-sepia/` with its fonts listed under OFL in
  `LICENSES.md`.
- Non-compliant: a skin using a font ripped from a game.

## Exceptions

None for rules 1 and 2.

## Enforcement

CI style lint (no boundary layer; `meta.json` schema; LICENSES.md
present); human art review per skin.
