---
id: DISC-USER-JOURNEYS
title: "Discovery: User Journeys"
version: 0.2.0
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
  - DISC-HIDDEN-BUSINESS-RULES
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
log](evidence/README.md). Lifecycle: **Draft** v0.2.0, the revision after
owner-proxy round 1 (ADR-0001). Suggestions are marked **[S]**.

The reference app's journeys cannot be observed (KC-061), and this
project designs its own UI and flows (KC-057). So these journeys are
**designed, not discovered** (KC-062). Rule references (R-n) point to
DISC-HIDDEN-BUSINESS-RULES; capability references (C-n) point to
DISC-FEATURE-INVENTORY (both Frozen).

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
- Parking and offline features must work without the backend (R16).
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
   (foreground, precise requested).
3. On denial, the app stays fully usable for browsing, skins, search,
   saved places and the demo trip (J12), with a quiet "enable location"
   chip.

**J2. Switch skin (C1)**
1. Open the skin picker sheet, or swipe the skin strip.
2. The new skin applies live. Camera, armed alarms and route preview are
   kept. Reduced motion means no transition animation (C10).

**J3. Arrival alarm, the headline journey (C4; R1–R4, R17, R22, R24)**
1. Choose a destination: a search result, a long-pressed map point or a
   saved place. Tap **"Wake me"**.
2. Set the travel mode (R22) and the radius. The radius is suggested from
   the trip's straight-line length and is required.
3. Optionally add more alarms (e.g. 10 km, 2 km).
4. **Pre-flight checks**, re-checked silently at **every** "Start trip".
   An explanation appears only when a check fails, because Android can
   revoke permissions and settings at any time:
   - the location disclosure has been shown and permission granted (R6);
   - **precise** location is granted. If only approximate location is
     granted (about 1–3 km on Android 12+), the app asks for precise. If
     the user refuses, radii below the reported accuracy are blocked, with
     an explanation;
   - location services are on system-wide;
   - `POST_NOTIFICATIONS` is granted;
   - the alarm-stream volume is above 0;
   - full-screen intent is available, or the user is told about the
     heads-up fallback;
   - one-time hint: battery optimisation, for OEMs known to kill
     services.
5. If the user is already inside a radius, show the R4 dialog.
6. **Start trip.** The foreground-service notification shows the live
   distance and the next alarm. The quest marker appears on the map.
7. While the trip runs, the user can edit it: add or remove an alarm, or
   change a radius (pre-flight checks rerun), or end the trip.
8. Alarm rings: full-screen or heads-up, alarm stream, strong vibration.
   The user dismisses or snoozes. Remaining alarms stay armed (R2).
9. The trip ends when the last alarm is dismissed or the user taps "End
   trip".
- **Edge: location lost.** A distinct alert (R3). Dismissing it keeps the
  trip running.
- **Edge: stale trip.** A notification prompt after 3 hours without
  progress (R24).
- **Edge: phone restart.** A boot receiver needs no location permission.
  If a trip was armed, it raises a loud **"Trip alarm interrupted: phone
  restarted"** alert on the alarm stream (full-screen or heads-up),
  distinct from the arrival ring. Tapping it opens the app, and the user
  resumes the trip in the foreground. There is no automatic restart, as
  R5 requires.
- **Edge: service killed** (an OEM battery killer, or location revoked
  mid-trip). While a trip is armed, an inexact system alarm (no
  exact-alarm permission) acts as a watchdog and checks the session is
  alive. If it is not, the watchdog raises the same "interrupted" alert.
  Mechanism to be confirmed by an M3 spike.
- **Accessibility:** all of J3, including dismissing an alarm, can be
  completed with TalkBack (C10).

**J4. Save parking (C5)**
1. Tap "Park here" and add an optional note.
2. A Garage marker appears.
3. Later, "Return to vehicle" opens a walking route preview when online.
   Offline, it shows a compass bearing and the distance (R16).

**J5. Mark a place, the Codex (C6)**
1. Long-press the map or a search result, then "Mark as…".
2. Pick a category, with an optional name.
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
2. The download runs as a deferrable background job, so it can finish
   after the app is backgrounded. The mechanism, either a job scheduler
   or a data-sync foreground service (which needs its own Play
   declaration and has a time cap on Android 15), is **decided in M3**.
3. Name the pack, then manage or delete it later.

**J8. Explore-to-reveal (C3, R15, R18)**
1. Turn on the reveal toggle. Fog covers unexplored cells.
2. Cells clear as the user moves while the app is in use.
3. Optional: turn on "Reveal map during trips" (R18, off by default).

**J9. Sign in, sync and sign out (C11, R8)**
1. Profile → "Sign in to sync" (optional), using a method decided in M3.
2. Guest data merges into the account, using last-write-wins.
3. Saved places, parking and explored cells sync. Alarms, the journal and
   raw logs stay on the device.
4. **Sign out:** the user chooses to keep local data as guest data (the
   default) or to clear it from this device. Server data is unaffected.

**J10. Delete account (R9)**
1. In-app: Profile → Delete account → confirm. The server data is purged
   (R19), and the user chooses whether to keep local data.
2. Web: a public deletion-request page (KC-093) for users without the app.

**J11. Clear local data (R21)**
1. Settings → Privacy → clear the journal and explored cells.

**J12. Portfolio reviewer (KC-066)**
1. Install, then within about 30 seconds: see the map in a skin, switch
   skins, try the alarm.
2. **Demo trip** (MVP, as the proxy answered Q1):
   - **What it is:** a scripted replay of 30–60 seconds, labelled "Demo",
     that shows an alarm firing on a synthetic route. It is an in-app
     animation.
   - **What it does not use:** no location, no foreground service and no
     mock-location provider. It writes nothing to explored cells or the
     journal.
   - **When it runs:** never alongside a real trip. It works with location
     denied and with no network, using a small bundled synthetic area or
     tile set. It also makes the Play declaration videos easy to record.
3. About links to the public repo and specs (KC-112).

No admin journeys: there is no admin UI in the MVP. Operator tasks (VM
deploys, skin releases through app updates) belong to M3 and M4.

## Hidden Assumptions

- The boot receiver and the inexact watchdog alarm (J3) can be relied on
  across mainstream OEMs. This is to be confirmed by the M3 spike; some
  OEMs delay boot broadcasts.
- The demo trip adds no Play exposure, because it uses no location
  services.

## Strengths

- The headline journey re-validates every silent-failure cause at arming
  time, and covers reboot and service death with a loud alert, never
  silence.
- Guest-first entry (J1) and the offline, permission-free demo (J12) serve
  the primary audience.

## Weaknesses

- J3's pre-flight checks add steps, though they are silent when
  everything passes.
- OEM battery killers stay partly outside the app's control. The watchdog
  turns a silent failure into a loud one; it cannot prevent the failure.

## Questions

1. *(Resolved by the proxy)* The demo trip is in the MVP, bounded as in
   J12.
2. *(Resolved by the proxy)* Reboot and service death raise a loud
   interrupted-trip alert, with no automatic restart.
3. *(→ M3)* Background download mechanism (J7); watchdog and boot-receiver
   spike (J3).

New rules introduced here (the interrupted-trip alert, the watchdog and
re-checking at every start) are added to DISC-HIDDEN-BUSINESS-RULES as a
versioned revision (v1.1.0, R25–R26). They are not left implicit.

## Recommendations

- **Keep:** guest-first entry; re-checked pre-flight; the demo trip.
- **Improve:** a loud interrupted-trip alert for reboot and service death;
  an offline bearing for parking.
- **Remove:** any onboarding wall; silent failure modes.

## Confidence Level

**Overall: Inference (65)**, capped by the reboot/watchdog handling and
the demo trip (`OV-001` §4). The journeys themselves are designs (KC-062)
built on Frozen rules and capabilities.
