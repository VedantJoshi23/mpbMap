---
id: DISC-BUSINESS-VISION
title: "Discovery: Business Vision"
version: 0.2.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Vedant Joshi
created: 2026-10-30
updated: 2026-11-02
milestone: M1
category: Discovery
priority: Critical
depends_on: []
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions: []
tags:
  - discovery
  - business-vision
risk: Medium
complexity: Low
---

# DISC-BUSINESS-VISION: Business Vision

One of the ten M1 investigations, run under Oriveda `OV-001` (Frozen) over
the claims in [the evidence log](evidence/README.md). Claim IDs (`KC-NNN`)
point into that log, which holds the full statements and sources.

Lifecycle: **Draft**. v0.1.0 went through Discussion (EVD-011). This is
the Revision. Next come Architecture Review and then Freeze, once the
owner approves.

## Purpose

Establish what this product is, who it is for, why it exists, and which
constraints bound it, before the Constitution (M2) turns any of it into
Law.

## Observed Facts

All owner-stated, at confidence 100.

- **What it is.** An original Android app on one borrowed concept: real
  streets and real GPS, reskinned in video-game-inspired styles (KC-056).
  MapShifter is credited for the concept (KC-003, KC-057).
- **What makes it useful.** The arrival alarm wakes the user by distance
  to the destination, not by estimated time, so overnight travellers can
  sleep through delays (KC-079, KC-080).
- **Who it is for.** First, people reviewing the owner's portfolio. Second,
  Play Store users (KC-066).
- **Why it exists.** It is a portfolio project that stays maintained,
  public and usable after the MVP (KC-001, KC-067).
- **Success.** A published app that works correctly, complies with Play's
  rules and provides real utility (KC-069).
- **Distribution.** Google Play, on the owner's already-paid account
  (KC-058).
- **Budget.** Zero spend until 300+ downloads, with stack and methodology
  chosen to defer cost. The budget is revisited after that (KC-068).
- **Scope.** Phone app first, extendable later to Wear OS and Android Auto
  (KC-059, KC-060). The server is the owner's existing VM (KC-081).
- **Posture.** Free, no ads, user accounts, a backend of its own, and
  location logging (KC-075).
- **Identity.** No franchise-referencing names or marks anywhere in the
  app's identity. Games are design references only (KC-078, KC-083).
- **Non-functional requirements** (the `OV-001` cross-cutting check):
  - maps that feel as smooth as Google or Apple Maps (KC-070)
  - offline maps (KC-076)
  - English only, with no localisation (KC-076)
  - accessibility options, inferred at 75 and awaiting confirmation
    (KC-077)
  - no availability target is stated

## Interpretation

These are interpretations, confirmed in Discussion (KC-066) unless marked
new.

- **Engineering and design craft is the product's main output.** The
  portfolio audience judges code quality, architecture and polish. Play
  users mainly test that the app is real and works.
- **The skins and the arrival alarm are what make this the owner's work.**
  The concept is MapShifter's, and MapShifter is coming to Android
  (KC-065). Original skin art plus a real utility separate this project
  from a port.
- **Being on Play makes compliance a product feature, not polish.**
  KC-069 makes "rules compliant" part of success itself. Accounts and
  location logging raise the bar further (KC-093, KC-096, KC-097).
- **Zero spend shapes the architecture.** Every map service must be free
  at about 300 downloads. A free provider set exists (KC-099, inference 75).
- *(New)* **The arrival alarm sets the app's location-permission strategy.**
  Play accepts one declared background-location feature (KC-096). The
  alarm is the natural one, and it may not need background permission at
  all if it runs as a user-started foreground service. This is also why
  location logging should be limited to sessions the user is actively
  running (Q-10, owned by `hidden-business-rules`).

## Hidden Assumptions

- About 300 downloads will stay within free-provider fair-use limits
  (KC-087–KC-091). If usage grows faster than the budget review, the
  zero-spend rule and provider policies collide. The VM proxy (KC-099)
  is the control point.
- The shared VM keeps enough headroom for the backend: about 2.4 GB of RAM
  is free today (KC-085, KC-086).
- Indian data-protection and map-boundary rules may apply (KC-098). This
  is assumption-tier and must be checked before launch.

## Strengths

- A proven, appealing concept (KC-034, KC-036), plus a utility drawn from
  the owner's own real need (KC-079).
- A clear, measurable success definition (KC-069) and an explicit budget
  horizon (KC-068).
- Tight scope with named extension points (KC-059, KC-060).
- Identity and credit rules were settled before any code (KC-057, KC-078).

## Weaknesses

- The smoothness target "feels like Google/Apple Maps" (KC-070) has no
  numbers yet. Standards (M4) must make it measurable.
- The posture (accounts, backend, location logging) carries the heaviest
  compliance load of any option considered. This is not a reason to
  change it, but it must be designed in from the first slice
  (`ADR-0005`, High tier).
- No availability target is stated for the backend. On a shared VM, a
  realistic target should be written down, even if modest.

## Questions

1. *(For `hidden-business-rules`, Q-10)* Limit location logging to while
   the app is open or an alarm or navigation session is running (KC-096)?
2. *(For `user-journeys`, Q-11)* Make accounts optional, with a guest mode
   that syncs on sign-in (KC-100)?
3. *(For the Constitution, Q-12)* Is accessibility in scope (KC-077)?
4. *(For Standards)* Is there a backend availability target, e.g. "best
   effort; the app's core features work offline when the server is down"?

## Recommendations

- **Keep:** concept-only scope with credit; portfolio-first audience;
  zero spend to 300 downloads; Play distribution; phone-first scope; the
  arrival alarm as the headline utility.
- **Keep:** free, no ads, accounts, own backend, location logging (KC-075),
  scoped as in Q-10 and Q-11.
- **Improve:** turn KC-070 into numeric performance targets in Standards.
- **Improve:** design core features so they work when the backend is
  unavailable. This covers the alarm, offline maps and saved places, and
  it fits both a shared VM and the zero-spend rule.
- **Remove:** franchise names and marks from the project's identity
  (KC-078). This is done by rule: no identity exists yet to strip.

## Confidence Level

Vision, audience, scope, budget, posture and identity: **Fact (100)**,
owner-stated in EVD-010 and EVD-011. Interpretations confirmed in
Discussion: **Inference (85)**. Statements that rest on Play-policy and
provider claims: **Inference (75)**, capped by KC-099, the weakest
load-bearing claim (`OV-001` §4).
