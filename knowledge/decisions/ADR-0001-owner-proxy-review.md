---
id: ADR-0001
title: Owner Proxy Agent Approves Specifications on the Owner's Behalf
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Vedant Joshi
created: 2026-11-02
updated: 2026-11-02
milestone: M1
category: Decision
priority: High
depends_on:
  - DISC-BUSINESS-VISION
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions: []
tags:
  - governance
  - review
risk: High
complexity: Low
---

# ADR-0001: Owner Proxy Agent Approves Specifications on the Owner's Behalf

This is the project's own ADR series. Oriveda's ADRs are cited as
"Oriveda ADR-NNNN".

## Context

Oriveda's protocols (`OV-001`–`OV-007`) put a human in the loop for
Discussion, review and approval at every step. The owner has given their
vision, constraints and decisions (EVD-001, EVD-010, EVD-011, EVD-022;
frozen in DISC-BUSINESS-VISION). They now want the remaining steps to move
without them in every round (EVD-023).

## Options Considered

- **The owner reviews every round.** This is the most faithful option and
  the slowest. The owner explicitly does not want it.
- **The drafting agent approves its own work.** This is fast, but no one
  argues the other side. That breaks Oriveda's role split (`OV-001`), in
  which the agent drafts and a human confirms.
- **A separate proxy agent reviews and approves for the owner (chosen).**
  A second agent, holding the owner's recorded intent, challenges each
  draft over as many rounds as needed. Only defined critical matters go
  back to the owner.

## Decision

A separate **owner proxy** agent (model: Opus 5.5) acts as the owner's
reviewer for every Oriveda artifact from M1 onward. The drafting agent
presents drafts and suggestions. The proxy reviews, argues and returns
`APPROVE`, `CHANGES REQUESTED` or `ESCALATE`. Rounds repeat until it
approves. An approval by the proxy counts as the owner's approval. It is
recorded in each spec's `reviewers` as "Owner proxy (ADR-0001)" and in
the evidence log.

**Escalation to the owner is mandatory for:**

1. Anything that contradicts an owner-stated fact (the evidence log's
   owner-sourced claims) or a Frozen specification.
2. Spending money, or committing to a paid service or plan.
3. Freezing the project Constitution. It is always High tier (Oriveda
   `ADR-0005`), and amending it later costs an ADR plus remediation.
4. Public identity that is costly to reverse: the app name, the Play
   package name, store listing copy.
5. Legal exposure beyond Play's published rules (e.g. privacy-law
   interpretation, map-boundary depiction).
6. Starting implementation (M8). Implementation is paused until the
   owner gives explicit approval.
7. Deadlock: no agreement after three rounds on the same point.

## Consequences

- Specs move quickly, with an adversarial reviewer in place of a silent
  self-check.
- The proxy can only be as right as the recorded intent. If the evidence
  log misses something the owner believes, the proxy cannot know it. This
  is why escalation rule 1 exists.
- The owner gets a full summary artifact at the end, plus specific
  escalations along the way.

## Revisit Criteria

- The owner overrides a proxy approval. That means the proxy misread
  intent, and the escalation rules should widen.
- Escalations become frequent enough that the proxy saves little time.
