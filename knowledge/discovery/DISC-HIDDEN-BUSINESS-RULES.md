---
id: DISC-HIDDEN-BUSINESS-RULES
title: "Discovery: Hidden Business Rules"
version: 0.1.0
status: Draft
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
  - DISC-FEATURE-INVENTORY
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - discovery
  - hidden-business-rules
risk: High
complexity: Medium
---

# DISC-HIDDEN-BUSINESS-RULES: Hidden Business Rules

One of the ten M1 investigations (`OV-001`, Frozen), over [the evidence
log](evidence/README.md). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Suggestions are marked **[S]**.

The project is greenfield, so no rule is yet hidden *in code*. This
investigation surfaces rules that are implicit in the owner's decisions,
the Frozen investigations and the external policies the app must obey,
so that none is left unstated when the Constitution (M2) chooses its
Laws. Risk is **High**: several rules carry location data, accounts and
Play policy (Oriveda ADR-0005).

## Purpose

State every rule the product must follow, and where it comes from, so
that M2 can promote the non-negotiable ones to Laws and M4 the rest to
Standards.

## Observed Facts

Each rule is listed with its source.

**Alarm rules** (from DISC-FEATURE-INVENTORY C4, Frozen)

- **R1. Route-agnostic trigger.** An arrival alarm fires on straight-line
  distance from the device to the destination. It never depends on the
  route taken (KC-080, KC-095).
- **R2. Trip session.** One user-started foreground service holds every
  armed alarm for a trip. Dismissing one alarm disarms only that alarm.
  The service runs until the last alarm is dismissed, the trip ends or the
  user arrives.
- **R3. Location loss.** Network or cell location is the first fallback.
  Dead reckoning is capped by travel mode. If the estimate crosses the
  radius, the app raises a distinct "location lost near destination"
  alert, never an arrival alarm.
- **R4. Arming inside the radius.** The app never arms silently. A dialog
  makes the user choose: shrink the radius, or arm anyway, which fires on
  arrival with the arrival distance shown.

**Location and privacy rules** (owner posture, Frozen
DISC-BUSINESS-VISION)

- **R5. Collection scope.** Location is collected only while the app is in
  use or a trip session is running (KC-101). No
  `ACCESS_BACKGROUND_LOCATION`.
- **R6. Disclosure first.** An in-app prominent disclosure naming every
  location-using feature comes before the runtime permission prompt
  (KC-092).
- **R7. Guest first.** No feature except sync requires sign-in (KC-102).
  Guest data stays on the device.
- **R8. Sync scope (MVP).** Signing in syncs saved places, parking and
  explored cells, with last-write-wins. Alarms and raw location logs never
  leave the device in the MVP (DISC-FEATURE-INVENTORY C11).
- **R9. Account deletion.** Deletion is available in-app and through a web
  link. Deleting the account deletes its server data (KC-093).

**External-policy rules**

- **R10. Provider fair use.**
  - Nominatim and the OSRM demo are limited to ≤1 request per second
    across all users, through the VM proxy, with an identifying
    User-Agent, caching and no client-side Nominatim autocomplete
    (KC-088, KC-089).
  - Offline downloads are capped at 50 MB / 3,000 tiles per pack, with
    ≤4 parallel requests (KC-087).
- **R11. Attribution always visible.** OSM ODbL and OpenFreeMap credit
  appears on the map view (KC-064, KC-087).
- **R12. Play declarations.**
  - Foreground-service type: location.
  - `POST_NOTIFICATIONS` is requested before the first alarm.
  - The full-screen-intent declaration, with a heads-up fallback.
  - Data safety, content rating and target API (KC-097).

**Product and identity rules**

- **R13. Free, no ads** (KC-075). Zero spend until 300+ downloads (KC-068).
- **R14. Identity.** No franchise or MapShifter names or marks in public
  surfaces. MapShifter is credited for the concept (KC-078, KC-003,
  KC-057).
- **R15. Reveal default.** Explore-to-reveal is off by default. When off,
  the whole map is visible (KC-072).
- **R16. Core works offline.** Alarms, parking, saved places, offline
  search and downloaded maps work without the backend (KC-104).

## Interpretation

These are the agent's proposals, settling the items carried forward in
KC-110.

- [S] **R17. Arrival distance** (KC-110 item 1). "Arrival" means within
  500 m of the destination for road and rail, and 100 m for walking and
  cycling. The value is shown in the arm-anyway dialog. The reason: bus
  and rail stops are rarely at the exact pin, and 500 m still means "now"
  at vehicle speed.
- [S] **R18. Secondary use of trip-session location** (KC-110 item 2).
  Trip-session fixes may update explored cells only when all of these
  hold:
  - (a) the reveal toggle is on;
  - (b) the prominent disclosure (R6) lists the exploration map among the
    features that use location;
  - (c) a separate setting, "Reveal map during trips", is off by default.

  This keeps the foreground service's declared purpose (the alarm)
  primary. It is recorded as **assumption-tier**: Play's text does not
  address secondary use of foreground-service location directly
  (KC-092).
- [S] **R19. Server retention.** The server stores only what R8 syncs.
  Deleted-account data is purged within 30 days and the backup window is
  disclosed (KC-093 allows disclosed retention).
- [S] **R20. No tracking SDKs.** No advertising or third-party analytics
  SDK. Crash reporting, if any, must be free and must not collect
  location. The tool choice goes to M3 (KC-068, KC-075). This keeps the
  Data safety form simple.
- [S] **R21. Local erasure.** The user can clear the journal and explored
  cells on the device at any time, independent of account deletion.

## Hidden Assumptions

- R18 assumes that a disclosed, opt-in secondary use of location from a
  user-started foreground service is acceptable to Play. This is not
  verified. If Play rejects it, R18 drops and reveal grows only while the
  app is in use.
- India's DPDP Act 2023 and its map-boundary rules may apply (KC-098,
  assumption, 50). R6, R9, R19 and R21 cover consent and erasure in
  spirit. How India's borders are depicted is not addressed by any rule
  here.

## Strengths

- Each rule traces to an owner decision, a Frozen investigation or a
  published policy.
- The alarm rules (R1–R4, R17) are concrete enough to test directly.

## Weaknesses

- R18 and the boundary question depend on unverified assumptions.
- R10 depends on third-party policies that can change without notice.

## Questions

1. Should map-boundary depiction (KC-098) be settled before launch? It is
   legal exposure beyond Play's published rules (ADR-0001 rule 5). [S]
   Escalate a single, specific question to the owner before the first
   public release, not now: show OSM's boundaries as-is, or use an
   India-compliant boundary layer?
2. Is R18 acceptable as an opt-in, or should the MVP drop secondary use
   entirely to minimise Play risk?

## Recommendations

- **Keep:** R1–R16 as candidate Laws (M2) or Standards (M4).
- **Improve:** settle R17 and R19–R21 as proposed.
- **Remove:** nothing.

## Confidence Level

**Overall: Assumption (50)**, capped by KC-098 (the legal applicability
of DPDP and the boundary rules). Breakdown: R1–R16 are Fact (90–100);
R17 and R19–R21 are Inference (70); R18 is Assumption (55).
