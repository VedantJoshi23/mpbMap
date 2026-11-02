---
id: STD-INDEX
title: "Standards Applicability Index"
version: 0.1.0
status: Draft
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
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Applicability (all ten candidate categories, plus project additions)

| Category | Applies? | Why | Standard |
| --- | --- | --- | --- |
| Code | Yes (always) | Every project | STD-CODE |
| Testing | Yes (always) | Every project; Production-grade (CONST §2) | STD-TESTING |
| Security | Yes (always) | Accounts, gateway, public repo | STD-SECURITY |
| API | Yes | BC9 and BC10 expose HTTP (ARCH-SYSTEM §1) | STD-API |
| Database | Yes | ADR-0006 and ADR-0007 record datastores | STD-DATABASE |
| CI/CD | Yes | Ships to Play (ADR-0009) | STD-CICD |
| Observability | Yes | Production-grade includes it (OV-005) | STD-OBSERVABILITY |
| Performance | Yes | KC-070 names a performance NFR | STD-PERFORMANCE |
| Accessibility | Yes | User-facing UI; KC-103; Law 14 | STD-ACCESSIBILITY |
| SEO | **No** | The only public web surface is GitHub Pages: legal and deletion pages plus a bootstrap file. They are not a discoverable product surface, and the Play listing is ASO, not SEO. Revisit if a marketing site is added | none |
| *Project: Privacy & Play compliance* | Yes | Law 10, Law 12; R10–R12 | STD-COMPLIANCE |
| *Project: Skin authoring* | Yes | C1, C2; Laws 11 and 12 | STD-SKINS |

## Rules

Each Standard above uses the OV-005 template. This index is the single
place where applicability is stated.

## Enforcement

Review checklist for PRM-REVIEW: every change names the Standards it
touches.
