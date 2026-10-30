---
id: DISC-BUSINESS-VISION
title: "Discovery: Business Vision"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Vedant Joshi
created: 2026-10-30
updated: 2026-10-30
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

Lifecycle: **Draft**, awaiting the owner's Discussion pass.

## Purpose

Establish what this product is, who it is for, why it exists, and which
constraints bound it, before the Constitution (M2) turns any of it into
Law.

## Observed Facts

Owner-stated facts, at confidence 100:

- **What it is.** An original Android app built on one concept: real
  streets and real GPS, reskinned in video-game-inspired styles (KC-056).
- **What it is not.** It is not a replica of MapShifter. The name, skins,
  art, UI, features and flows are all the project's own (KC-057).
- **Credit.** MapShifter is credited as the source of the concept,
  throughout the project (KC-003, KC-057).
- **Purpose.** A personal portfolio project (KC-001). KC-056 superseded
  KC-001's "replicate" scope but left this purpose unchanged.
- **Cost.** Zero spend (KC-002). The Google Play account is already paid
  for (KC-058).
- **Distribution.** Google Play, through the owner's account (KC-058).
- **Scope.** Phone app first (KC-059). It must stay extendable to Wear OS
  and Android Auto, which are not built now (KC-060).

Context from the reference app. These are facts (confidence ≤ 90) about
MapShifter, not about this project:

- The concept has shipped and been well received. MapShifter launched in
  April 2026, reached 1.0.34 by October, and is rated 4.9/5 from 15
  ratings (KC-034, KC-036).
- MapShifter is free, with no accounts and no ads (KC-026, KC-027).
- Its Android version is announced as "soon", with no date (KC-045).
- It positions itself as "cosmetic" skins over a working maps engine
  (KC-037).

## Interpretation

These are interpretations, not facts.

- **The real audience is people evaluating the owner's work** (recruiters,
  peers, the portfolio audience). Play Store users are a secondary
  audience. Quality of engineering and design craft is the product's main
  output, and download numbers are not. *(This rests on "personal
  portfolio project" in KC-001. Needs confirming.)*
- **The differentiator has to be the owner's own skins and identity.**
  The concept is MapShifter's, and MapShifter is coming to Android
  (KC-065). Original skin art and a distinct identity are what make this
  project the owner's work, not just a port.
- **Going public on Play turns a portfolio piece into a real product with
  obligations** (KC-064, inference 75): a privacy policy, a Data safety
  form, a content rating, target-API compliance and OSM attribution. These
  belong to the Constitution and Standards, not later polish.
- **Zero spend shapes the architecture.** Every map service (tiles,
  search, routing, weather) has to be free at the expected scale
  (KC-054, inference 70).

## Hidden Assumptions

- That "portfolio" means the app should be publicly usable, polished and
  maintained after launch, not a frozen demo.
- That expected usage stays small enough for free tiers and free public
  services. If the app gets popular, zero spend and fair-use limits will
  collide.
- That the owner wants a game-*inspired* tone but no references to
  specific franchises. The reference app evokes franchises it is not
  affiliated with (KC-006, KC-028). Whether this project does the same is
  not yet decided (Q-08).

## Strengths

- A proven, appealing concept with visible demand (KC-034, KC-036).
- A free, open stack is known to be enough for it (KC-038).
- The scope is tightly drawn: phone only, extension points named.
- The owner has already decided how to credit MapShifter and where the
  replica boundary sits, before writing any code.

## Weaknesses

- No non-functional requirements are stated by the owner yet. Discovery's
  cross-cutting check (`OV-001`) found accessibility (KC-024), offline use
  (KC-041), localisation (KC-033) and a privacy posture (KC-027, KC-042),
  but **only as reference-app facts**. None is yet a goal of this project.
  No performance or availability targets exist in any evidence.
- No success measure is stated. "Done" for a portfolio project is
  undefined.
- No identity yet: the app name and skin concepts are open (Q-08).

## Questions

1. **Audience.** Is the primary audience people reviewing your portfolio,
   with Play users secondary? Or are real Play users the primary target?
2. **Success.** What makes this a success? For example: shipped on Play
   with N original skins; a showcase-quality codebase; a target
   download or rating count.
3. **Posture (Q-09).** Do you adopt the reference app's posture as your
   own: free, no ads, no accounts, no backend of your own, no location
   logging? Each part becomes a Constitution Law if adopted.
4. **NFRs.** Which of these are goals for *this* app: accessibility, as
   MapShifter declares (KC-024); offline maps (KC-041); more than one
   language (KC-033)? Are there any performance targets you care about?
5. **Tone (Q-08).** Original game-*inspired* worlds (generic genres like
   western, cyberpunk, post-apocalyptic, voxel), or nods to specific
   games?
6. *(For `technical-architecture`)* Which free tile, search and routing
   providers fit zero spend at Play scale (Q-04)?

## Recommendations

- **Keep:** concept-only scope with credit (KC-056, KC-057); phone-first
  with named extension points (KC-059, KC-060); zero spend (KC-002).
- **Keep:** free, no accounts, no ads, on-device location. These fit zero
  spend (no server to pay for) and a portfolio's credibility. To be
  confirmed in Q-09.
- **Improve:** turn the owner's NFR choices from question 4 into stated
  goals before the Constitution, so they can become Laws.
- **Improve:** define success measures, so release checkpoints
  (`ADR-0005`) have something to check against.
- **Remove:** franchise-referencing names or marks from this project's
  identity, if confirmed in question 5. This lowers Play listing risk
  (KC-064, KC-065).

## Confidence Level

The vision statement, scope and constraints are **Fact (100)**: they are
owner-stated (KC-002, KC-003, KC-056–KC-060). Statements about audience,
differentiator and Play obligations are at most **Inference (75)**, capped
by KC-064, their weakest load-bearing claim (`OV-001` §4).
