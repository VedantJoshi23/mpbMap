---
id: DISC-HIDDEN-BUSINESS-RULES
title: "Discovery: Hidden Business Rules"
version: 0.2.0
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
log](evidence/README.md). Lifecycle: **Draft** v0.2.0, the revision after
owner-proxy round 1 (ADR-0001). Suggestions are marked **[S]**.

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

Each rule is listed with its source. R1–R16 are fixed by Frozen specs
(KC-105, KC-109) or owner decisions.

**Alarm rules** (from DISC-FEATURE-INVENTORY C4, Frozen)

- **R1. Route-agnostic trigger.** An arrival alarm fires on straight-line
  distance from the device to the destination. It never depends on the
  route taken (KC-080, KC-095).
- **R2. Trip session.** One user-started foreground service holds every
  armed alarm for a trip. Dismissing one alarm disarms only that alarm.
  The service runs until every armed alarm has fired and been dismissed,
  or the user ends the trip. Reaching the arrival distance (R17) never
  ends the session while any alarm is still armed.
- **R3. Location loss.** Network or cell location is the first fallback.
  Dead reckoning is capped by the trip's travel mode (R22). If the
  estimate crosses the radius, the app raises a distinct "location lost
  near destination" alert, never an arrival alarm.
- **R4. Arming inside the radius.** The app never arms silently. A dialog
  makes the user choose: shrink the radius, or arm anyway, which fires at
  the arrival distance (R17), shown in the dialog.

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
  explored cells, with last-write-wins. Alarms, journal entries and raw
  location logs never leave the device in the MVP (DISC-FEATURE-INVENTORY
  C11).
- **R9. Account deletion.** Deletion is available in-app and through a web
  link. Deleting the account deletes its server data (KC-093).

**External-policy rules**

- **R10. Provider fair use.** Obey every chosen provider's published
  policy. Providers are decided in M3. Current candidates and their
  limits:
  - Nominatim and the OSRM demo: ≤1 request per second across all users,
    through the VM proxy, with an identifying User-Agent and caching, and
    no client-side Nominatim autocomplete (KC-088, KC-089);
  - Photon: fair use, with heavy use throttled (KC-090);
  - OpenFreeMap offline packs: 50 MB / 3,000 tiles each, with ≤4 parallel
    requests (KC-087).
- **R11. Attribution always visible.** OSM ODbL and OpenFreeMap credit
  appears on the map view (KC-064, KC-087).
- **R12. Play declarations.**
  - Foreground-service type: location.
  - `POST_NOTIFICATIONS` is requested before the first alarm.
  - The full-screen-intent declaration, with a heads-up fallback.
  - The Data safety form and privacy policy (KC-097).
  - Content rating and target API (KC-064).

**Product and identity rules**

- **R13. Free, no ads** (KC-075). Zero spend until 300+ downloads (KC-068).
- **R14. Identity.** No franchise or MapShifter names or marks in public
  surfaces, **except the concept credit** to MapShifter (KC-078, KC-003,
  KC-057). Any store-listing copy that mentions MapShifter is escalated
  under ADR-0001 rule 4 (KC-064).
- **R15. Reveal default.** Explore-to-reveal is off by default. When off,
  the whole map is visible (KC-072).
- **R16. Core works offline.** Alarms, parking, saved places, offline
  search and downloaded maps work without the backend (KC-104).

## Interpretation

These are the agent's proposals, settling KC-110.

- [S] **R17. Arrival distance** (KC-110 item 1). The arrival distance is
  the smaller of two values:
  - the mode default: 500 m for road and rail, 100 m for walking and
    cycling;
  - the smallest armed alarm radius.

  It is shown in the arm-anyway dialog (R4). The reason: bus and rail
  stops are rarely at the exact pin, and 500 m still means "now" at
  vehicle speed. Taking the smaller value means no armed alarm can lie
  inside it (consistent with R2).
- [S] **R18. Secondary use of trip-session location** (KC-110 item 2).
  Trip-session fixes may update **explored cells** only when all of these
  hold:
  - (a) the reveal toggle is on;
  - (b) the prominent disclosure (R6) lists the exploration map among the
    features that use location;
  - (c) the user has turned on "Reveal map during trips" (off by
    default).

  **The journal is excluded.** It grows only from in-use location. This
  rests on assumption KC-118 (55). The proxy answered Q2 as the owner
  would: keep the opt-in, because KC-101 puts trip sessions inside the
  scope of location logging.
- [S] **R19. Server retention.** The server stores only what R8 syncs.
  Deleted-account data is purged within 30 days, and any backup window is
  disclosed in the privacy policy.
- [S] **R20. No tracking SDKs.** No advertising or third-party analytics
  SDK. Crash reporting, if any, must be free and must not collect
  location. The tool choice goes to M3 (KC-068, KC-075).
- [S] **R21. Local erasure.** The user can clear the journal and explored
  cells on the device at any time, independent of account deletion.
- [S] **R22. Travel mode.** The user picks the travel mode (walk, cycle,
  road, rail) when arming a trip. It defaults to the route-preview mode
  if one exists. It drives R3's speed cap and R17's arrival distance.
- [S] **R23. Target audience.** The app's Play target audience excludes
  children, so Play's Families policy does not apply. The app has
  accounts, a backend and location logging. This falls within Play's
  published rules, so the proxy adopted it under its own authority.

## Hidden Assumptions

- **KC-118 (55).** Play accepts R18's disclosed, double opt-in secondary
  use. If Play rejects it, R18 drops and reveal grows only while the app
  is in use.
- **KC-098 (50).** India's DPDP Act and its map-boundary rules may apply.
  Both are **escalated to the owner** (KC-117).

## Strengths

- Each rule traces to an owner decision, a Frozen investigation or a
  published policy.
- The alarm rules (R1–R4, R17, R22) are concrete and testable, and
  consistent with one another.

## Weaknesses

- R18 rests on an unverified assumption.
- R10 depends on third-party policies that can change without notice.
- Two legal questions are open with the owner (KC-117).

## Questions

1. **Escalated to the owner (ADR-0001 rule 5, KC-117), now and not at
   release.** The answers constrain M3's tile source and M2's Laws.
   - **(a) Map boundaries.** Choose one: (i) OSM as-is; (ii) an
     India-compliant layer; (iii) no country boundary lines (proxy's
     recommendation).
   - **(b) Design as if DPDP applies?** (proxy's recommendation: yes).
2. *(Resolved by the proxy)* R18 stays as an opt-in.

## Recommendations

- **Keep:** R1–R16 as candidate Laws (M2) or Standards (M4).
- **Improve:** adopt R17–R23 as proposed.
- **Remove:** nothing.

## Confidence Level

**Overall: Assumption (50)**, capped by KC-098. Breakdown: R1–R16 are
Fact (90–100); R17 and R19–R23 are Inference (70); R18 is Assumption (55,
KC-118).
