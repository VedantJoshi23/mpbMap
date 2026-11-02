---
id: ADR-0010
title: Rename the App from Etak to Kairos
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Vedant Joshi
created: 2026-11-02
updated: 2026-11-02
milestone: M7
category: Decision
priority: High
depends_on:
  - CONST-CORE
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - identity
risk: Medium
complexity: Low
---

# ADR-0010: Rename the App from Etak to Kairos

## Context

The owner named the app "Etak" (KC-147). Before publication, a trademark
risk was flagged: the 1985 Etak Navigator's company assets passed into the
TomTom group, so the mark may still be live. Public identity is the
owner's decision (ADR-0001 rule 4). The Play package is permanent once
published, so the only cheap time to rename is now, before any code or
listing exists.

## Options Considered

- **Keep Etak and run a trademark search first.** This delays the
  decision, and the risk sits in the navigation category, the same
  category as this app.
- **Rename now (chosen).** No code, listing or package exists yet, so the
  rename costs documentation edits only.

## Decision

The app is named **Kairos** (Greek *kairos*, "the opportune moment",
which fits an alarm that wakes the traveller at the right moment). The
Play package is **`io.github.vedantjoshi23.kairos`**. Owner decision,
EVD-049 / KC-167.

**Constitution amendment record** (`OV-002`): this ADR is the amendment
record for CONST-CORE v1.0.1. The change is to identity text only: the
title and name references. No Law changes.

**Remediation**, done in the same change:
- every spec, README, NOTICE, CLAUDE.md and the manifest are renamed as
  non-semantic patch revisions citing this ADR;
- the summary artifact is renamed.

The evidence log keeps "Etak" in its historical entries (append-only).
KC-147 is superseded by KC-167.

## Consequences

- No technical impact: nothing is built yet.
- Name check (web search only, 2026-11-02): no Android maps or navigation
  app named Kairos was found. An F-Droid task-planner named "Kairos"
  (`com.skohscripts.kairos`) exists in a different category, and the
  word is widely used elsewhere. A formal trademark check is still due
  before the Play listing.
- The GitHub repository is still named `mpbMap`. Renaming it is the
  owner's call (DISC-REPO-STRUCTURE suggests matching the app name;
  GitHub redirects old links).

## Revisit Criteria

The pre-listing trademark check finds a conflicting mark in the
navigation or maps category.
