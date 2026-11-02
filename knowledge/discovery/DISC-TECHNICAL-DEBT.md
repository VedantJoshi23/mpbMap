---
id: DISC-TECHNICAL-DEBT
title: "Discovery: Technical Debt"
version: 1.1.0
status: Frozen
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
log](evidence/README.md). Lifecycle: **Frozen** v1.0.0 (2026-11-02, KC-129). Draft v0.1.0, then
owner-proxy rounds 1–2 (ADR-0001), then Architecture Review, then Freeze.

There is no code, so there is no debt *in code* (KC-007). This
investigation records **debt taken on by design**: deliberate shortcuts
and external dependencies. Per Oriveda Principle 7, each one carries its
rationale, a repayment trigger and a migration path.

## Purpose

List the known, deliberate debts and fragile areas, with repayment
triggers and migration paths, so that M4 Standards and M8 can track them.

## Observed Facts

- The reference app's most fragile area was navigation (KC-035).
- Every candidate provider is free with no SLA. Some are non-commercial
  only (KC-087–KC-091, KC-122).
- MapLibre Android has no 3D terrain (KC-123).
- The VM is shared, with about 2.4 GB of RAM free (KC-085, KC-086).
- Force-stop and OEM "deep clean" can disable the alarm, its watchdog and
  its boot receiver until the app is reopened (DISC-TECHNICAL-ARCHITECTURE
  S3; DISC-USER-JOURNEYS Weaknesses).

## Interpretation

| # | Debt | Why it was taken | Risk | Repayment trigger | Migration path |
| --- | --- | --- | --- | --- | --- |
| TD1 | Free, volunteer-run tile, search and routing providers with no SLA | Zero spend (KC-068) | An outage or policy change breaks search and routing | Budget review at 300+ downloads; any provider policy change | Swap providers behind BC10. Self-hosting costs money, so it is an **ADR-0001 rule-2 escalation** |
| TD2 | OSRM demo's "non-commercial, reasonable use" clause | Only free routing candidate | Blocked if usage looks commercial | As TD1; **any move toward monetisation** | Self-hosted OSRM or Valhalla for the region (rule 2) |
| TD3 | No offline routing (C9 gap against KC-074) | MVP scope | Falls short of "comparable to Google Maps" | **First post-MVP release checkpoint** | On-device routing or routing data in packs. Making routing permanently online-only would contradict KC-074 (a rule-1 escalation) |
| TD4 | Straight-line arrival distance (R1) | Route-agnostic, works offline | Early alarms on winding roads (the safe direction) | User reports of early alarms | A route-aware distance as an *optional* refinement. Never replaces R1 |
| TD5 | Last-write-wins sync for the Codex | Simplicity (C11) | Concurrent edits lost silently | Multi-device use becomes common | Per-field merge, or a CRDT for CodexEntry |
| TD6 | The alarm depends on OEM behaviour. **Residual silent mode:** a force-stop or OEM deep clean cancels the watchdog and blocks the boot receiver until the app is reopened. Also, the watchdog's detection bound is a known limit (≤10 min target, S3) | The platform is outside the app's control | **A silent failure is possible after a force-stop.** Every other path is loud (R25) | S3 results; per-OEM user reports | Per-OEM guidance in the app (the J3 battery hint); reassess exact alarms if Play policy allows; record measured bounds |
| TD7 | `topo-mono` excluded; hillshade only (KC-123) | No 3D terrain in MapLibre Android | A smaller skin roster | MapLibre Android ships 3D terrain | Add `topo-mono` as a 3D skin, with a 2D fallback |
| TD8 | Backend on a shared VM with about 2.4 GB of free RAM | Zero spend (KC-068); the VM's figures (KC-085, KC-086) | Contention with other workloads | A memory alert; the budget review | A dedicated host after the budget review (rule 2) |
| TD9 | Policy and legal assumptions: R18 (KC-118), DPDP and boundaries (KC-117) | Not verifiable yet | Play rejection or legal exposure | Play review outcome; the owner's KC-117 answer | Drop R18 (reveal in use only), which is an escalation to the owner under ADR-0001 rule 1 if the Play review forces it; apply the KC-117 rules |
| TD10 | Turn-by-turn navigation deferred | The reference app's fragility (KC-035) | A feature gap | First post-MVP release checkpoint | Its own Feature spec, with reroute and arrival rules |
| TD11 | AWS Terrain Tiles and Open-Meteo have no SLA, and Open-Meteo is non-commercial (KC-122, KC-091) | Free | Hillshade or weather outage; a terms breach if monetised | Outage; any move toward monetisation | Bundle elevation tiles in packs (S4); a paid weather API is a rule-2 escalation |
| TD12 | Gateway app identity: Play Integrity quotas, or a weaker extractable token | Zero spend | Quota exhaustion, or abuse past the token | Quota alerts; abuse seen in gateway logs | Tighter rate limits; a paid tier is a rule-2 escalation |
| TD13 | A single VM with no redundancy, and an untested backup restore | Zero spend; KC-104 best effort | Data loss on VM failure | Before the first public release | A scripted restore drill; off-VM backups (flagged free tier) |
| TD14 | Play's yearly targetSdk bump | Play policy (R12) | Forced updates; behaviour changes to FGS and alarms | Each Play deadline | Recurring maintenance task; rerun S3 on each bump |
\1| TD16 | The API hostname is a free DDNS subdomain (KC-136) | No domain, zero spend | Provider shutdown; **hostname hijack** through takeover of the DDNS account (an attacker could get a valid certificate for our name) | The budget review; any DDNS incident | The bootstrap file on GitHub Pages redirects without a release. Mitigations: 2FA on the DDNS account; the update token kept on the VM, never in the repo; optional public-key pinning with a reused key and a backup pin. Repayment: a purchased domain (rule 2) |

- [S] **Tracking:** each TD item becomes a dated entry in the project's
  shortcut register (Oriveda Principle 7). Release checkpoints check that
  no deferred spec hides an untracked debt (Oriveda ADR-0005 rule 5).

## Hidden Assumptions

- 300 downloads stay within free-provider fair use (DISC-BUSINESS-VISION).
- Force-stop is rare among the target users once the J3 battery hint has
  been shown. To be confirmed by S3 and by user reports.

## Strengths

- Every debt has a rationale, a trigger and a migration path.
- BC10 and the AlertPort cap the cost of repaying TD1, TD2, TD6 and TD11.

## Weaknesses

- TD1, TD2 and TD11 can arrive suddenly, through a policy change.
- TD6 leaves one residual silent-failure mode (force-stop) that no app
  can fully prevent.

## Questions

1. *(Resolved by the proxy)* No Constitution Law for debt tracking. The
   duty already binds through Oriveda Principle 7 and ADR-0005 rule 5, and
   restating it would break "point, don't copy". M4 Standards set the
   register format.

## Recommendations

- **Keep:** TD4 (the safe direction) and TD5 (simple) until their
  triggers fire.
- **Improve:** monitor TD1, TD2 and TD11 through gateway error rates from
  the first release; run the TD13 restore drill before launch.
- **Remove:** nothing yet.

## Architecture Review (2026-11-02)

- **Against the Frozen DISC- investigations:**
  - Every TD item traces to a Frozen decision or a fact-tier provider
    claim.
  - TD6 states the force-stop limit honestly, consistent with R25 and
    DISC-TECHNICAL-ARCHITECTURE S3.
  - Repayments that cost money are flagged as rule 2.
  - Consistent.
- **Divergences:** none (no code).
- **Result:** passes.

## Revision History

- **v1.1.0 (2026-11-02):** adds TD16 (the DDNS API hostname), after
  DISC-TECHNICAL-ARCHITECTURE v1.1.0. Approved by the owner proxy
  (KC-141).

\1

**Overall: Inference (65).** The debts derive from Frozen specs and
fact-tier provider claims. The risk ratings are judgements.
