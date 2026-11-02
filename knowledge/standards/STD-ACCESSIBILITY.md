---
id: STD-ACCESSIBILITY
title: "Accessibility"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M4
category: Standard
priority: High
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
risk: High
complexity: Low
---

# STD-ACCESSIBILITY: Accessibility

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

All UI, with Law 14's alarm flows as the hard floor.

## Rules

1. **Alarm flows** (arming, the alarm itself, dismiss and snooze) are
   fully usable with **TalkBack** and **200% font scale**, and every alert
   has a **non-audio channel**: a distinct vibration plus a visual (Law
   14).
2. Every interactive element has a content description and a touch
   target of at least 48 dp.
3. Map markers are distinguishable by **shape and label**, never colour
   alone (C10). One high-contrast skin variant exists.
4. A **reduced-motion** setting is honoured: no animated skin transitions
   and no camera fly-to.
5. Text contrast is at least 4.5:1 for body text and 3:1 for large text,
   including over skins. UI chrome uses the design system, not skin
   colours.
6. Dark UI is supported (C10).

## Examples

- Compliant: the "Wake me" button reads "Set arrival alarm for <place>".
- Non-compliant: a quest marker that differs only by colour.

## Exceptions

The map canvas itself is exempt from contrast rules. Its labels are not.

## Enforcement

Android Lint accessibility checks in CI; Compose semantics tests for J3;
a manual TalkBack pass on the alarm flows per release (STD-TESTING).
