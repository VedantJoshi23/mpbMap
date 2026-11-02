---
id: CONST-CORE
title: Engineering Constitution
version: 0.5.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
  - Vedant Joshi (freeze sign-off, ADR-0001 rule 3)
created: 2026-11-02
updated: 2026-11-02
milestone: M2
category: Constitution
priority: Critical
depends_on:
  - DISC-RECOMMENDATIONS
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-BUSINESS-VISION
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - constitution
risk: High
complexity: Medium
---

# Engineering Constitution

Authored under Oriveda `OV-002` (Frozen) through `PRM-CONSTITUTION`. The
Laws are derived from Frozen Discovery: DISC-RECOMMENDATIONS and
DISC-HIDDEN-BUSINESS-RULES, among others. Lifecycle: **Draft**. Owner-proxy
review comes first (ADR-0001). **Freezing requires the owner's sign-off**
(ADR-0001 rule 3).

**Draft-completeness status:** §2–§4 are answered by the owner (KC-135).
Law 11 reflects KC-132 and KC-133. Law 12's boundary clause reflects
KC-142.

Amendment: see §6.

## 1. Engineering Philosophy

This app exists to show, and to deliver, one thing done properly: a map
that is a joy to look at and an alarm that does not let a sleeping
traveller miss their stop. "Good" means correct before clever. The
headline utility never fails without telling the user. Privacy and
platform rules are designed in, not bolted on. Everything else (skins,
reveal, the Codex) is delight built on that dependable base. It is a
portfolio piece, so the code, the specs and the decision trail are part
of the product, and they are kept honest and readable.

## 2. Rigor Level

**Chosen:** Production-grade (owner, KC-135).

This means:
- **Includes:** automated tests for every core rule (BC1 alarm logic
  above all); CI on every push; spec-first changes for High-tier areas;
  the divergence log kept; release checkpoints (Oriveda ADR-0005 rule 5);
  secret scanning.
- **Excludes:** an RFC per change, formal threat-model documents, multiple
  approvers, SLAs.

## 3. AI Collaboration Contract

**Chosen:** Principal Engineer, the technical partner (owner, KC-135).

The agent commits to:
- challenge requests that weaken a Law or a Frozen spec, and propose the
  smallest compliant alternative;
- preserve context boundaries (BC1–BC11) and say when a change crosses
  them;
- log every disagreement with a Draft spec as a `DIV-` entry, never
  resolving it silently;
- escalate under ADR-0001's rules, and never start implementation without
  the owner's explicit approval (KC-107).

## 4. Architecture Ambition

**Chosen:** Modular Monolith (owner, KC-135).

That is one Android app split into modules along the Frozen bounded
contexts (DISC-DOMAIN-DISCOVERY), plus one small backend service, with
BC11 (Wear OS, Auto) as later modules.

## 5. Laws

Each Law cites its source. A Law is never violated knowingly. A found
violation is a defect to fix, and a deliberate exception requires an
amendment (§6).

### Law 1: No silent alarm failure

> An armed trip alarm never fails silently on any path the app can
> detect. Every detectable failure (location loss, restart, service
> death, a failed pre-flight check) raises a loud, distinct alert.
> Undetectable paths (force-stop and any others the spikes find) are
> recorded known limits with mitigations. They are not exceptions to this
> Law.

**Source:** DISC-RECOMMENDATIONS candidate 1; R3, R25, R26
(DISC-HIDDEN-BUSINESS-RULES); TD6; KC-079, KC-069.

### Law 2: Route-agnostic arrival

> Arrival alarms fire on straight-line distance to the destination and
> never depend on the route taken. A route-aware refinement may only make
> an alarm fire earlier: never later, and never not at all. One trip
> session holds every armed alarm until each one has fired and been
> dismissed, or the user ends the trip.

**Source:** R1, R2, R17; KC-080 (owner fact); TD4 (DISC-TECHNICAL-DEBT).

### Law 3: No background location

> The app never requests `ACCESS_BACKGROUND_LOCATION`. Location is
> collected only while the app is in use or a user-started trip session
> runs, and only after the prominent disclosure.

**Source:** DISC-RECOMMENDATIONS candidate 2; R5, R6; KC-101 (owner
decision); DISC-DOMAIN-DISCOVERY BC2 rule.

### Law 4: Guest first

> No feature except sync requires an account.

**Source:** R7; KC-102 (owner decision).

### Law 5: Device-only sensitive data

> Alarms, trip state, the journal and raw location logs never leave the
> device. Only the Codex and explored cells sync. Coordinates sent in
> provider queries (search, reverse geocoding, route preview) are
> transient: the gateway (BC10) never stores them linked to a user or
> client, access logs omit coordinates, and cache keys use coarsened
> coordinates. Widening any of this needs an amendment.

**Source:** R8; DISC-DATA-MODEL; DISC-RECOMMENDATIONS candidate 4; KC-088
(caching duty). The transient transfer is disclosed in the Data safety
form (R12).

### Law 6: Complete, verifiable erasure

> Account deletion (in-app and web) and local erasure remove every copy
> they cover, within the disclosed windows. Local erasure includes the raw
> location log.

**Source:** R9, R19, R21; KC-093.

### Law 7: Zero spend until the budget review

> No paid service, plan or tier is adopted before the budget review at
> 300+ downloads without the owner's explicit approval. Commercial free
> tiers are flagged when adopted.

**Source:** KC-068 (owner fact); ADR-0001 rule 2.

### Law 8: Free and ad-free, no tracking

> The app is free and has no ads, advertising SDKs or third-party
> analytics SDKs. Crash reports never contain location.

**Source:** KC-075 (owner decision); R13, R20.

### Law 9: The core works offline

> The trip alarm, parking, saved places, offline search and downloaded
> maps work without the backend or the network.

**Source:** KC-104 (owner decision); R16.

### Law 10: Platform and provider compliance

> The app meets the enumerated platform and provider obligations, R10,
> R11, R12 and R23 in DISC-HIDDEN-BUSINESS-RULES, which are the checkable
> list. Provider limits are enforced through the provider gateway
> (BC10).

**Source:** KC-069 (owner fact: rules-compliant is success); R10–R12,
R23.

### Law 11: Original identity, credited concept

> No franchise or MapShifter names, marks or assets appear in the
> project's identity or the shipped app. The one exception is the
> credited concept-reference images in `reference/`, which are excluded
> from the project licence and never ship in the app (KC-132, KC-133).
> All other assets are original or openly licensed. MapShifter is
> credited for the concept.

**Source:** KC-078, KC-057, KC-003 (owner facts); R14; C2. The project's
own work is Apache-2.0 (KC-133). The MapShifter reference images stay in
`reference/` as credited concept evidence, are excluded from the licence
through NOTICE, and never ship in the app (KC-132).

### Law 12: Data-protection and boundary stance

> The app is designed as if India's DPDP Act 2023 applies. Consent,
> purpose limitation and erasure are covered by Laws 3, 5 and 6. In
> addition, every consent the app requests is recorded on the device
> (ConsentRecord), including its withdrawal: the location disclosure, the
> R18 reveal-during-trips opt-in, and sign-in/sync. The privacy policy
> names the purpose of each data category. No administrative boundary
> lines are drawn at any level in any skin (KC-142). The agent drafts no
> legal interpretation beyond this (ADR-0001 rule 5).

**Source:** KC-134, KC-142 (owner decisions); KC-117; DISC-DATA-MODEL v1.1.0
ConsentRecord.

### Law 13: Core boundaries

> The trip-alarm engine (BC1) is independent of the UI, and every alarm
> alert goes through its AlertPort. Only the location gateway (BC2)
> touches the platform's location APIs.

**Source:** C4; DISC-DOMAIN-DISCOVERY BC1 and BC2 rules; KC-060 (owner
fact). Holds under any §4 choice.

### Law 14: The alarm is accessible

> The core alarm flows (arming a trip, the alarm itself, and dismissing
> or snoozing it) are fully usable with TalkBack and large text, and are
> perceivable without sound.

**Source:** KC-103 (owner decision); C10; J3. Broader accessibility is
set by M4 Standards.

## 6. Amendment Process

Per `OV-002` Lifecycle. In addition, under ADR-0001 rule 1, any amendment
that weakens a Law sourced from an owner fact or decision is escalated to
the owner.

**Not Laws** (by design): debt tracking (Oriveda Principle 7 already
binds; DISC-TECHNICAL-DEBT Q1), smoothness numbers (M4 Standards),
identity names (Q-08, Play listing).
