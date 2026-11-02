---
id: FEAT-CODEX-PARKING
title: "Etak — Spec Card: Codex & Parking"
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
  - DOM-CODEX
required_by: []
related_documents: []
related_domains:
  - DOM-CODEX
related_features: []
related_decisions: []
tags:
  - feature
  - spec-card
risk: Low
complexity: Low
---

# Spec Card: Codex & Parking

The Low-tier form of a Feature Specification (Oriveda ADR-0005 rule 2,
`OV-007`). Lifecycle: **Draft**.

- [x] No new invariant: it uses only rules DOM-CODEX declares.
- [x] No cross-domain flow: no call or event crosses a domain boundary
  (the online route preview for 'Return to vehicle' is a separate UI action on FEAT-ROUTE-PREVIEW, not a call from DOM-CODEX).
- [x] No High-tier dependency on its path.

**Tier reason:** Saving and listing places within DOM-CODEX's own rules.

**Purpose:** Mark places in gaming terms and save parking spots (C5, C6).

**Owning domain:** `DOM-CODEX`

**Acceptance criteria:**
- [ ] Long-press or a place card, then 'Mark as…', with the C6 categories and rename.
- [ ] 'Park here' saves a Garage entry, and the newest is active.
- [ ] Offline 'Return to vehicle' shows bearing and distance (J4).
- [ ] Works with no account (Law 4).

**Open questions:** none.
