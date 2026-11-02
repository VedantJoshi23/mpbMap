---
id: STD-SKINS
title: "Skin Authoring"
version: 1.0.0
status: Frozen
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
  - ADR-0003
  - ADR-0009
  - DISC-FEATURE-INVENTORY
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
CONST-CORE Law (Frozen). **Tier: Medium** (Oriveda ADR-0005). Lifecycle:
**Frozen** v1.0.0 (2026-11-02), approved by the owner proxy after rounds
1–2 (KC-170, ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Skin bundles under `skins/<codename>/`: style JSON, fonts, icons,
textures and metadata.

## Rules

1. **Assets are original or openly licensed** (OFL fonts; CC0 or CC-BY
   textures). Each bundle has a `LICENSES.md`. Nothing comes from games.
   No codename or public name echoes a franchise or a MapShifter skin
   (Law 11, C2).
2. **The style never references the `boundary` source layer** (Law 12),
   enforced by the CI style lint. BC3's runtime boundary guard is defence
   in depth (ADR-0003).
3. Each skin's `meta.json` declares:
   - codename;
   - version;
   - `is3D`;
   - `fallback2D`;
   - font set;
   - icon set;
   - accents;
   - attribution text.
4. **The attribution control** is enabled in every style. The lint fails
   any skin that hides or removes it (R11; ADR-0003).
5. **Performance:** meets STD-PERFORMANCE. 3D skins name a 2D fallback.
6. **Accessibility:**
   - POI icons differ by shape;
   - label halos keep text at 4.5:1 or better;
   - one high-contrast variant is maintained.
7. **Bundling** (ADR-0003, ADR-0009):
   - styles are **bundled and CI-linted, never fetched whole**;
   - glyphs and sprites are bundled as `asset://` (Law 9, offline
     labels);
   - at runtime the bootstrap file may substitute **only source
     endpoints**.

## Examples

- Compliant: `skins/terrain-sepia/`, with its fonts listed under OFL in
  `LICENSES.md`.
- Non-compliant: a style that sets the attribution control off.

## Exceptions

None for rules 1, 2 and 4.

## Enforcement

CI style lint (boundary layer, attribution control, `meta.json` schema,
`LICENSES.md` present); human art review per skin.

## Open question (for ADR-0003's next revision; not resolved here)

ADR-0003 makes glyph endpoints remote-configurable, yet bundles glyphs as
`asset://`. Suggested reading: `asset://` is the default, and a remote
glyph endpoint is an optional override.

## Architecture Review (2026-11-02)

Checked against CONST-CORE Laws 1–14, ARCH-SYSTEM, and ADR-0002..0009 as
amended: no contradiction. Result: passes.
