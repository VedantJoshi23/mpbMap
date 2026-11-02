---
id: STD-COMPLIANCE
title: "Privacy and Play Compliance"
version: 0.2.0
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
  - ADR-0005
  - ADR-0008
  - ADR-0009
  - DISC-HIDDEN-BUSINESS-RULES
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
CONST-CORE Law (Frozen). **Tier: High** (Oriveda ADR-0005). Lifecycle:
**Draft** v0.2.0, the revision after owner-proxy round 1 (ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

The checkable obligations behind Laws 5, 6, 10 and 12. This is the
release checklist.

## Rules

1. **Play declarations:**
   - foreground-service type `location`. The **declaration video records
     a real trip session with the real foreground service**; the J12 demo
     is **never** used as FGS evidence (Law 10);
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
   - crash data (allowlisted fields);
   - **device-integrity data sent to Google** by Play Integrity and by
     Credential Manager sign-in (ADR-0008);
   - deletion is offered (R9).
4. **Privacy policy** (GitHub Pages) states **facts only**: the data
   collected, the purpose per category (Law 12), the retention windows
   and the rights offered.
   - **Every** retention window that touches user-linked or device data:
     - LocationFix: 30 days;
     - tombstones: 90 days;
     - crash reports: 90 days;
     - account data: **at most 37 days** after deletion (30-day purge
       plus 7-day backups);
     - safety debug log: 7 days.
   - It makes **no claim of legal compliance** (e.g. "DPDP-compliant").
     Any such claim goes to the owner (ADR-0001 rule 5; Law 12).
5. **Consent records:** every consent kind, with withdrawal (Law 12).
6. **Attribution:**
   - OSM ODbL and OpenFreeMap on the map view;
   - Terrain Tiles and providers on the About screen;
   - "Concept inspired by MapShifter" on the About screen (R11, Law 11).
7. **No boundary lines** on any map surface or store image (Law 12).
8. **Store listing:** **all store listing copy goes to the owner before
   publication** (ADR-0001 rule 4). Copy mentioning MapShifter also
   follows R14 (concept credit only; no names or marks otherwise).

## Examples

- Compliant: the release checklist has every box ticked and links the
  real-trip declaration video.
- Non-compliant: a privacy policy that says "fully DPDP compliant".

## Exceptions

None.

## Enforcement

The release checklist in the CI release workflow (manual sign-off
items), plus automated checks where possible (style lint; screenshot
review).
