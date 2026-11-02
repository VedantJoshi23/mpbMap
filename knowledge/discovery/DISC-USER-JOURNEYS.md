---
id: DISC-USER-JOURNEYS
title: "Discovery: User Journeys"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M1
category: Discovery
priority: High
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
  - user-journeys
risk: Medium
complexity: Medium
---

# DISC-USER-JOURNEYS: User Journeys

One of the ten M1 investigations (`OV-001`, Frozen), over [the evidence
log](evidence/README.md). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Suggestions are marked **[S]**.

The reference app's journeys cannot be observed (KC-061), and this
project designs its own UI and flows (KC-057). So these journeys are
**designed, not discovered** (KC-062). Rule references (R-n) point to
DISC-HIDDEN-BUSINESS-RULES; capability references (C-n) point to
DISC-FEATURE-INVENTORY (Frozen).

## Purpose

Fix the main paths users take through the MVP, at the level of steps and
decision points. This is the input to M5 domains and M6 feature specs.
Screen design stays in M6.

## Observed Facts

- Two audiences: portfolio reviewers first, Play users second (KC-066).
- No sign-up wall: guest is the default, and accounts are optional
  (KC-102, R7).
- Location permission comes only after a prominent disclosure (KC-092,
  R6), and never as background location (R5).
- The reference app opens straight onto a full-bleed map with controls
  floating over it (KC-016). This is context only (KC-063).

## Interpretation

All journeys are [S] designs unless they restate a Frozen rule.

**J1. First launch (both audiences)**
1. The app opens straight onto the map, in the default skin, as a guest.
   There is no onboarding wall. Before a location fix, the map is centred
   on a neutral default view.
2. The first location-dependent tap (locate-me or "Wake me") shows the
   prominent disclosure (R6), then the system permission prompt
   (foreground, precise).
3. On denial, the app stays fully usable for browsing, skins, search and
   saved places, with a quiet "enable location" chip.

**J2. Switch skin (C1)**
1. Open the skin picker sheet, or swipe the skin strip.
2. The new skin applies live. Camera, armed alarms and route preview are
   kept. Reduced motion means no transition animation (C10).

**J3. Arrival alarm, the headline journey (C4; R1–R4, R17, R22)**
1. Choose a destination: a search result, a long-pressed map point or a
   saved place. Tap **"Wake me"**.
2. Set the travel mode (R22) and the radius. The radius is suggested from
   the trip's straight-line length and is required.
3. Optionally add more alarms (e.g. 10 km, 2 km).
4. Pre-flight checks, run once each:
   - location disclosure and permission (R6);
   - `POST_NOTIFICATIONS`;
   - full-screen-intent availability, with a fallback note;
   - [S] a battery-optimisation hint for OEMs known to kill services.
5. If the user is already inside a radius, show the R4 dialog.
6. **Start trip.** The foreground-service notification shows the live
   distance and the next alarm. The quest marker appears on the map.
7. Alarm rings: full-screen or heads-up, alarm stream, strong vibration.
   The user dismisses or snoozes. Remaining alarms stay armed (R2).
8. The trip ends when the last alarm is dismissed or the user taps "End
   trip".
   - Edge: the location-lost alert (R3) is distinct, and dismissing it
     keeps the trip running.
   - Edge: on phone restart, the trip is lost (no background restart, R5).
     [S] The next app open shows "Your trip was interrupted. Resume?"

**J4. Save parking (C5)**
1. Tap "Park here" and add an optional note.
2. A Garage marker appears.
3. Later: "Return to vehicle" opens a walking route preview.

**J5. Mark a place, the Codex (C6)**
1. Long-press the map or a search result, then "Mark as…".
2. Pick a category (Waypoint, Quest Marker, Safehouse and so on), with an
   optional name.
3. The place appears in the Codex list, filterable by category.

**J6. Search and route preview (C7, C8)**
1. Type in the search field: online typeahead, or offline pack search when
   offline.
2. Pick a result to see a place card.
3. "Route" shows the line, distance and ETA for walk, cycle or drive
   (online only).
4. "Wake me" from the place card leads into J3.

**J7. Download an offline area (C9)**
1. Settings → Offline maps → "Download area". Frame a city on the map,
   with a live size estimate that is blocked above the pack limit (R10).
2. Download in the background (in-use only), name the pack, and manage or
   delete it later.

**J8. Explore-to-reveal (C3, R15, R18)**
1. Turn on the reveal toggle. Fog covers unexplored cells.
2. Cells clear as the user moves while the app is in use.
3. Optional: turn on "Reveal map during trips" (R18, off by default).

**J9. Sign in and sync (C11, R8)**
1. Profile → "Sign in to sync" (optional), using a method decided in M3.
2. Guest data merges into the account, using last-write-wins.
3. Saved places, parking and explored cells sync. Alarms, the journal and
   raw logs stay on the device.

**J10. Delete account (R9)**
1. In-app: Profile → Delete account → confirm. The server data is purged
   (R19), and the user chooses whether to keep local data.
2. Web: a public deletion-request page (KC-093) for users without the app.

**J11. Clear local data (R21)**
1. Settings → Privacy → clear the journal and explored cells.

**J12. Portfolio reviewer (KC-066)** [S]
1. Install, then within about 30 seconds: see the map in a skin, switch
   skins, try the alarm.
2. [S] **Demo trip:** a clearly labelled simulated trip replays a short
   synthetic route in the app (not system mock location), so a reviewer
   can see an alarm fire without travelling. It is isolated from real
   trips, cannot run alongside them, and is hidden from the Play-declared
   foreground-service flow.
3. About links to the public repo and specs (KC-112).

No admin journeys: there is no admin UI in the MVP. Operator tasks (VM
deploys, skin releases through app updates) belong to M3 and M4.

## Hidden Assumptions

- A demo trip that never touches real location adds no Play exposure. It
  is UI simulation only.
- Users accept losing an armed trip on reboot, given the resume prompt.

## Strengths

- The headline journey (J3) shows every safety check before the user
  falls asleep. Failures are caught at arming time, not at 3 a.m.
- Guest-first (J1) and the demo trip (J12) serve the primary audience.

## Weaknesses

- J3's pre-flight step adds friction to the most important flow.
- OEM battery killers remain partly outside the app's control.

## Questions

1. Is the demo trip (J12) acceptable in the MVP? It directly serves
   KC-066.
2. Should the reboot case (J3, step 8) do more than a resume prompt? Any
   automatic restart would need background location, which R5 forbids.

## Recommendations

- **Keep:** guest-first entry; J3's pre-flight checks.
- **Improve:** add the demo trip for reviewers.
- **Remove:** any onboarding wall.

## Confidence Level

**Overall: Inference (70).** The journeys are designs (KC-062) built on
Frozen rules and capabilities. The demo trip and the reboot handling are
Inference (65–70).
