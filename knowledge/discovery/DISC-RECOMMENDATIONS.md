---
id: DISC-RECOMMENDATIONS
title: "Discovery: Recommendations"
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
  - DISC-REPO-STRUCTURE
  - DISC-HIDDEN-BUSINESS-RULES
  - DISC-USER-JOURNEYS
  - DISC-DATA-MODEL
  - DISC-DOMAIN-DISCOVERY
  - DISC-TECHNICAL-ARCHITECTURE
  - DISC-TECHNICAL-DEBT
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - discovery
  - recommendations
risk: High
complexity: Low
---

# DISC-RECOMMENDATIONS: Recommendations Roll-up

The tenth M1 investigation and the roll-up of the other nine (`OV-001`,
Frozen). Lifecycle: **Draft**, under owner-proxy review (ADR-0001). It
adds no new design. It consolidates what the frozen investigations
decided, and hands M2–M7 a clean list.

## Purpose

Say what the project should preserve, improve and discard, and give M2
(the Constitution) and M3 (Architecture) their exact inputs and open
dependencies.

## Observed Facts

- Nine investigations are Frozen or awaiting freeze. Their own
  Recommendations sections are the source of this roll-up.
- Escalations pending with the owner: KC-116 (the reference images and
  the project licence) and KC-117 (map boundaries and DPDP). Owner input
  pending: KC-128 (the API domain) and Q-08 (the app name and package).
- Implementation is gated on the owner's explicit approval (KC-107).

## Interpretation

### Keep (preserve into M2–M8)

- Concept-only scope, credited; portfolio-first; zero spend until 300+
  downloads; Play distribution (DISC-BUSINESS-VISION).
- The route-agnostic destination-radius alarm. One trip session holds
  all alarms. An uncertain alert is preferred over silence, and an
  interrupted trip always raises a loud alert (R1–R4, R17, R22–R26).
- Guest-first entry, with optional accounts; location collected only
  while the app is in use or a trip runs; no background location (R5–R9).
- Skins as data, with original assets and neutral codenames; 3D only
  where feasible, always with a 2D fallback (C1, C2).
- Device-authoritative storage. Minimal before-unlock data
  (TripBootRecord). Explored-cell sync through epoch filtering then union
  (DISC-DATA-MODEL).
- BC1 as the UI-independent core with an AlertPort; BC2 as the only
  location gateway; BC10 as the provider anticorruption layer
  (DISC-DOMAIN-DISCOVERY).

### Improve (work owed to later milestones)

- Turn the KC-070 smoothness target into numbers (M4).
- Run spikes S1–S5 before M3 freezes (DISC-TECHNICAL-ARCHITECTURE).
- Add abuse protection to the gateway (M3).
- Ship public-repo hygiene with the first code commit (DISC-REPO-STRUCTURE).
- Track TD1–TD10 with repayment triggers (DISC-TECHNICAL-DEBT).

### Remove (out of the MVP, or never)

- Never: franchise or MapShifter names and marks in the project's
  identity; tracking or ad SDKs; background location.
- Out of the MVP: turn-by-turn navigation, offline routing (a recorded
  gap), checkpoint alarms, the parking meter timer, widgets, weather,
  achievements, Wear OS and Android Auto (BC11 is reserved).
- Excluded skins: Subnautica-inspired (no bathymetry) and `topo-mono`
  (no 3D terrain on Android yet, KC-123), both under F3.

### Candidate Laws for the M2 Constitution

[S] These are non-negotiable, and violating any of them silently would be
expensive (`OV-002`'s promotion criterion). The final wording and
selection belong to M2.

1. A silent failure of an armed alarm is never acceptable. Every failure
   path raises an alert (R3, R25, R26).
2. No background location. Location is collected only while the app is
   in use or a user-started trip runs (R5).
3. Guest-first: no feature except sync requires an account (R7).
4. Alarms, the journal and raw location never leave the device in the
   MVP (R8).
5. Account and local erasure are complete and verifiable (R9, R19, R21).
6. Zero spend until the budget review. Any paid service needs the
   owner's approval (KC-068, ADR-0001 rule 2).
7. No franchise or MapShifter identity. MapShifter is credited for the
   concept (R14).
8. Comply with Play policy and every provider's policy (R10–R12).
9. The core works offline: alarm, parking, saved places, downloaded maps
   (R16).
10. *(Pending KC-117)* DPDP consent and erasure duties, and the boundary
    rule.

### Inputs for M3

- The ten bounded contexts plus reserved BC11.
- The event catalogue.
- The candidate stack.
- Spikes S1–S5.
- The constraints: gateway abuse protection, SDK levels, backups.
- Open: KC-117 (tile source), KC-128 (domain).

## Hidden Assumptions

- No investigation's recommendations conflict with another's. The
  Architecture Reviews checked them pairwise as each one froze.

## Strengths

- Every recommendation traces to a Frozen investigation. Nothing new is
  introduced here.

## Weaknesses

- The M2 Constitution cannot freeze until the owner answers KC-117, and
  its freeze is itself an escalation (ADR-0001 rule 3).

## Questions

1. Should the owner's pending items (KC-116, KC-117, KC-128, Q-08) go to
   the owner together, now, as one batch? [S] Yes. KC-117 already blocks
   M2.

## OV-000 exit criteria and M1 Definition of Done

- Every one of the ten investigation areas has fact- or inference-tier
  claims, or an explicitly surfaced gap. **Met.**
- Gaps that would change conclusions were surfaced. **Met**: KC-116 and
  KC-117 escalated.
- All ten investigations are drafted, discussed (by the owner proxy) and
  Frozen. **Met once this one and its two siblings freeze.**
- Recommendations are captured for M2 and M3. **This document.**
- A worked example under Oriveda's `examples/`. **Not applicable**: that
  item belongs to the framework's own Definition of Done (Oriveda ADR-0002).
  This project's DISC- documents are the per-project output.

## Recommendations

- **Keep:** the list above.
- **Improve:** send the owner batch now.
- **Remove:** nothing.

## Confidence Level

**Overall: Inference (65).** The roll-up inherits the weakest
non-assumption cap among the frozen investigations. The open assumptions
(KC-117, KC-118) are carried as named dependencies, not as load-bearing
claims.
