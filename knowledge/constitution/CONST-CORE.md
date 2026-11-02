---
id: CONST-CORE
title: Engineering Constitution
version: 0.1.0
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

**Draft-completeness status:** §2–§4 are the three owner-only choices. They
are asked (KC-130) and **not yet answered**. `OV-002` forbids defaulting
them silently, so each shows the agent's recommendation, clearly marked as
pending. Laws L11 and L12 depend on KC-116 and KC-117.

Amending a Frozen version of this document requires an ADR and an
explicit remediation statement (`OV-002` Lifecycle).

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

**Chosen:** *pending owner (KC-130). Recommended: Production-grade.*

If chosen, Production-grade means:
- **Includes:** automated tests for every core rule (BC1 alarm logic
  above all); CI on every push; spec-first changes for High-tier areas;
  the divergence log kept; release checkpoints (Oriveda ADR-0005 rule 5);
  secret scanning.
- **Excludes:** an RFC per change, formal threat-model documents, multiple
  approvers, SLAs.

## 3. AI Collaboration Contract

**Chosen:** *pending owner (KC-130). Recommended: Principal Engineer
(technical partner).*

If chosen, the agent commits to:
- challenge requests that weaken a Law or a Frozen spec, and propose the
  smallest compliant alternative;
- preserve context boundaries (BC1–BC11) and say when a change crosses
  them;
- log every disagreement with a Draft spec as a `DIV-` entry, never
  resolving it silently;
- escalate under ADR-0001's rules, and never start implementation without
  the owner's explicit approval (KC-107).

## 4. Architecture Ambition

**Chosen:** *pending owner (KC-130). Recommended: Modular Monolith.*

That is one Android app split into modules along the Frozen bounded
contexts (DISC-DOMAIN-DISCOVERY), plus one small backend service, with
BC11 (Wear OS, Auto) as later modules.

## 5. Laws

Each Law cites its source. "Never silently" means that violating it is a
defect, and so is shipping around it.

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
> never depend on the route taken. One trip session holds every armed
> alarm until each one has fired and been dismissed, or the user ends the
> trip.

**Source:** R1, R2, R17; KC-080 (owner fact).

### Law 3: No background location

> The app never requests `ACCESS_BACKGROUND_LOCATION`. Location is
> collected only while the app is in use or a user-started trip session
> runs, and only through the single location gateway (BC2), after the
> prominent disclosure.

**Source:** DISC-RECOMMENDATIONS candidate 2; R5, R6; KC-101 (owner
decision); DISC-DOMAIN-DISCOVERY BC2 rule.

### Law 4: Guest first

> No feature except sync requires an account.

**Source:** R7; KC-102 (owner decision).

### Law 5: Device-only sensitive data

> Alarms, trip state, the journal and raw location never leave the
> device. Only the Codex and explored cells sync. Widening this needs a
> Constitution amendment.

**Source:** R8; DISC-DATA-MODEL; DISC-RECOMMENDATIONS candidate 4.

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

> The app complies with Play policy (declarations, disclosures, Data
> safety, target API, target audience) and with every data provider's
> published policy (rate limits, attribution, offline limits), enforced
> through the provider gateway (BC10).

**Source:** KC-069 (owner fact: rules-compliant is success); R10–R12,
R23.

### Law 11: Original identity, credited concept

> No franchise or MapShifter names, marks or assets appear in the
> project's identity or the shipped app. All assets are original or
> openly licensed. MapShifter is credited for the concept.

**Source:** KC-078, KC-057, KC-003 (owner facts); R14; C2.
*Depends on KC-116 (the reference images and the licence).*

### Law 12: Data-protection and boundary stance

> *Pending KC-117.* If the owner chooses "design as if DPDP applies":
> location collection is consented, purpose-limited and erasable. The
> boundary rule follows the owner's choice (recommended: no country
> boundary lines).

**Source:** KC-117 (escalated to the owner).

## 6. Amendment Process

Once Frozen, this Constitution may be revised only through (1) an ADR
stating the reason and (2) an explicit list of existing work that becomes
non-compliant and needs remediation (`OV-002`). Ordinary version bumps do
not apply. Under ADR-0001 rule 1, any amendment that weakens a Law
sourced from an owner fact or decision is escalated to the owner.

**Not Laws** (by design): debt tracking (Oriveda Principle 7 already
binds; DISC-TECHNICAL-DEBT Q1), smoothness numbers (M4 Standards),
identity names (Q-08, Play listing).
