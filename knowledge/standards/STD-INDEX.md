---
id: STD-INDEX
title: "Standards Applicability Index"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M4
category: Standard
priority: Medium
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - ADR-0002
  - ADR-0003
  - ADR-0004
  - ADR-0005
  - ADR-0006
  - ADR-0007
  - ADR-0008
  - ADR-0009
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions: []
tags:
  - standard
risk: Low
complexity: Low
---

# STD-INDEX: Standards Applicability Index

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). **Tier: Medium** (Oriveda ADR-0005). Lifecycle:
**Frozen** v1.0.0 (2026-11-02), approved by the owner proxy after rounds
1–2 (KC-170, ADR-0001).
This index is exempt from the Standard template shape.

## Applicability (all ten candidates, plus project additions)

| Category | Applies? | Why | Standard | Tier |
| --- | --- | --- | --- | --- |
| Code | Yes (always) | Every project | STD-CODE | Medium |
| Testing | Yes (always) | Every project; Production-grade (KC-135) | STD-TESTING | **High** |
| Security | Yes (always) | Accounts, gateway, public repo | STD-SECURITY | **High** |
| API | Yes | BC9 and BC10 expose HTTP (ARCH-SYSTEM §1) | STD-API | Medium |
| Database | Yes | ADR-0006 and ADR-0007 record datastores | STD-DATABASE | **High** |
| CI/CD | Yes | Ships to Play (ADR-0009) | STD-CICD | Medium |
| Observability | Yes | Production-grade includes it (KC-135; OV-005) | STD-OBSERVABILITY | Medium |
| Performance | Yes | KC-070 names a performance NFR | STD-PERFORMANCE | Medium |
| Accessibility | Yes | User-facing UI; KC-103; Law 14 | STD-ACCESSIBILITY | Medium |
| SEO | **No** | The only public web surface is GitHub Pages (legal and deletion pages plus a bootstrap file). It is not a discoverable product surface, and the Play listing is ASO, not SEO. Revisit if a marketing site is added | none | n/a |
| *Project: Privacy & Play compliance* | Yes | Laws 10, 12; R10–R12 | STD-COMPLIANCE | **High** |
| *Project: Skin authoring* | Yes | C1, C2; Laws 11, 12 | STD-SKINS | Medium |

Tiers follow Oriveda ADR-0005: High for security, data integrity,
compliance and the tests that guard the safety path; Medium for the
rest. STD-CICD rule 3 relies on these tiers.

## Rules

Each Standard lists, in `depends_on`, the ADRs and DISC specs it
implements, so a revision to any of them flags its dependent Standards.
This index is the single place where applicability is stated.

## Enforcement

Review checklist for PRM-REVIEW: every change names the Standards it
touches.

## Architecture Review (2026-11-02)

Checked against CONST-CORE Laws 1–14, ARCH-SYSTEM, and ADR-0002..0009 as
amended: no contradiction. Result: passes.
