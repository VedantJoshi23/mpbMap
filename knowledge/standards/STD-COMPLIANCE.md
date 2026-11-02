---
id: STD-COMPLIANCE
title: "Privacy and Play Compliance"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M4
category: Standard
priority: Critical
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
risk: High
complexity: Low
---

# STD-COMPLIANCE: Privacy and Play Compliance

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

The checkable obligations behind Laws 5, 6, 10 and 12. This is the
release checklist.

## Rules

1. **Play declarations:**
   - foreground-service type `location`, with a demo video (the J12 demo
     helps record it);
   - full-screen intent (alarm core function), with the fallback;
   - `POST_NOTIFICATIONS`;
   - target audience excludes children (R23);
   - content rating;
   - target API.
2. **Prominent disclosure** comes before the location permission. It
   names every location-using feature (R6, R18) and uses the word
   "location".
3. **Data safety form:**
   - location collected (in use and during trips);
   - transient transfer of location to the search and routing providers
     (Law 5);
   - account identifier;
   - crash data;
   - deletion is offered (R9).
4. **Privacy policy** (GitHub Pages) states:
   - the purpose per data category (Law 12);
   - the retention windows: 30-day LocationFix, a 30-day purge plus 7-day
     backups, so **at most 37 days**;
   - DPDP-style consent and erasure.
5. **Consent records:** every consent kind is recorded with withdrawal
   (Law 12, DISC-DATA-MODEL v1.1.0).
6. **Attribution:**
   - OSM ODbL and OpenFreeMap on the map view;
   - Terrain Tiles and providers on the About screen;
   - "Concept inspired by MapShifter" on the About screen (R11, Law 11).
7. **No boundary lines** on any map surface or store image (Law 12).
8. **Store listing:** no MapShifter or franchise names or marks, except
   the concept credit (R14). Listing copy that mentions MapShifter goes to
   the owner (ADR-0001 rule 4).

## Examples

- Compliant: the release checklist has every box ticked and links the
  declaration video.
- Non-compliant: a promo screenshot that shows a country border.

## Exceptions

None.

## Enforcement

A release checklist in the CI release workflow (manual sign-off items),
plus automated checks where possible (style lint; screenshot review).
