---
id: FEAT-ROUTE-PREVIEW
title: "Etak — Spec Card: Route Preview"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M6
category: Features
priority: Low
depends_on:
  - DOM-ROUTING
required_by: []
related_documents: []
related_domains:
  - DOM-ROUTING
related_features: []
related_decisions: []
tags:
  - feature
  - spec-card
risk: Low
complexity: Low
---

# Spec Card: Route Preview

The Low-tier form of a Feature Specification (Oriveda ADR-0005 rule 2,
`OV-007`). Lifecycle: **Draft**.

- [x] No new invariant: it uses only rules DOM-ROUTING declares.
- [x] No cross-domain flow: no call or event crosses a domain boundary
  (gateway HTTP only).
- [x] No High-tier dependency on its path.

**Tier reason:** A Thin domain: one online call, no data, no invariants.

**Purpose:** Show line, distance and ETA for walk, cycle and drive (C8).

**Owning domain:** `DOM-ROUTING`

**Acceptance criteria:**
- [ ] Online: draws the route and shows distance and ETA.
- [ ] Offline or on failure: shows 'unavailable' and offers 'Wake me' instead.
- [ ] Coordinates are sent in the POST body (STD-API).

**Open questions:** none.
