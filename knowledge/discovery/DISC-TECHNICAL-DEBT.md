---
id: DISC-TECHNICAL-DEBT
title: "Discovery: Technical Debt"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M1
category: Discovery
priority: Medium
depends_on:
  - DISC-TECHNICAL-ARCHITECTURE
  - DISC-HIDDEN-BUSINESS-RULES
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - discovery
  - technical-debt
risk: Medium
complexity: Low
---

# DISC-TECHNICAL-DEBT: Technical Debt

One of the ten M1 investigations (`OV-001`, Frozen), over [the evidence
log](evidence/README.md). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001).

There is no code, so there is no debt *in code* (KC-007). What this
investigation records is **debt taken on by design**: deliberate
shortcuts and external dependencies that will bite a future maintainer if
nobody tracks them. Each item names its trigger for repayment, in the
spirit of Oriveda Principle 7 (every shortcut is intentional and dated).

## Purpose

List the known, deliberate debts and fragile areas, with repayment
triggers, so that the M2 Constitution and M4 Standards can guard them and
M8 can watch them.

## Observed Facts

- The reference app's most fragile area was navigation: arrival
  detection, turn banners, rerouting and offline-pack persistence
  (KC-035).
- Every provider in the candidate set is free, with no SLA, and some are
  restricted to non-commercial use (KC-087–KC-091).
- 3D terrain is not available in MapLibre Android (KC-123).
- The VM is shared and has little free RAM (KC-085, KC-086).

## Interpretation

Each item is a design debt.

| # | Debt | Why it was taken | Risk | Repayment trigger |
| --- | --- | --- | --- | --- |
| TD1 | Reliance on free, volunteer-run providers (tiles, search, routing) with no SLA | Zero spend (KC-068) | Outage or policy change breaks search and routing | Budget review at 300+ downloads, or any provider policy change. Then self-host region extracts |
| TD2 | The OSRM demo's "non-commercial, reasonable use" clause | Only free routing candidate | Being blocked if usage looks commercial | Same as TD1. The gateway (BC10) makes the swap cheap |
| TD3 | No offline routing (the C9 gap against KC-074) | MVP scope | Falls short of "comparable to Google Maps" | After the MVP, on the roadmap |
| TD4 | Straight-line arrival distance (R1) | Route-agnostic and offline | An early alarm on winding roads (the safe direction) | Only if users report early alarms as a problem |
| TD5 | Last-write-wins sync for the Codex | Simplicity (C11) | Concurrent edits are lost silently | Multi-device use becomes common |
| TD6 | The alarm depends on OEM behaviour (battery killers, delayed boot broadcasts) | The platform is outside the app's control | Loud failures (R25), not silent ones | Spike S3 results, and user reports per OEM |
| TD7 | `topo-mono` excluded; hillshade only (KC-123) | MapLibre Android has no terrain yet | Smaller skin roster | MapLibre Android ships 3D terrain |
| TD8 | A backend on a shared VM with about 2.4 GB of free RAM | Zero spend (KC-081) | Resource contention with other workloads | A memory alert, or the budget review |
| TD9 | Policy and legal assumptions: R18 (KC-118), DPDP and boundaries (KC-117) | Not verifiable yet | Play rejection or legal exposure | Play review outcome; the owner's KC-117 answer |
| TD10 | Turn-by-turn navigation deferred | The reference app's fragility (KC-035) | A feature gap against expectations | After the MVP, with its own spec |

- [S] **Tracking:** each TD item becomes a dated entry in the project's
  shortcut register at M8, and every release checkpoint reviews them
  (Oriveda ADR-0005 rule 5).

## Hidden Assumptions

- 300 downloads stay within free-provider fair use (DISC-BUSINESS-VISION).
- Debt that turns failures loud (TD6) is acceptable where silent failure
  is not.

## Strengths

- Every debt is deliberate, has a reason, and has a trigger.
- The gateway (BC10) and the AlertPort (BC1) cap the cost of repaying TD1,
  TD2 and TD6.

## Weaknesses

- TD1 and TD2 can arrive suddenly, through a policy change, not
  gradually.

## Questions

1. Should the M2 Constitution make "every design debt has a dated
   repayment trigger" a Law, or leave it as a Standard?

## Recommendations

- **Keep:** TD4 (the safe direction) and TD5 (simple) until their
  triggers fire.
- **Improve:** monitor TD1 and TD2 through gateway error rates from the
  first release.
- **Remove:** nothing yet.

## Confidence Level

**Overall: Inference (65).** The debts are derived from Frozen specs and
fact-tier provider claims. The risk ratings are judgements.
