---
id: STD-ACCESSIBILITY
title: "Accessibility"
version: 0.2.0
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
  - ADR-0005
  - DISC-FEATURE-INVENTORY
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
CONST-CORE Law (Frozen). **Tier: Medium** (Oriveda ADR-0005). Lifecycle:
**Draft** v0.2.0, the revision after owner-proxy round 1 (ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

All UI, with Law 14's alarm flows as the hard floor.

## Rules

1. **Alarm flows** (arm, alarm, dismiss and snooze):
   - fully usable with **TalkBack** and **200% font scale**;
   - dismiss and snooze are operable from the **lock screen** with
     TalkBack;
   - on both the foreground-service path and the `FLAG_INSISTENT` path,
     the **Law 14 vibration pattern repeats until dismissal**, alongside
     a visual (ADR-0005). A deaf, sleeping user must be woken.
2. Every interactive element has a content description and a touch target
   of at least 48 dp.
3. Map markers are distinguishable by **shape and label**, never colour
   alone. One high-contrast skin variant exists.
4. **Reduced motion:** honour both the in-app setting **and the system
   animation scale set to 0** ("Remove animations"). That means no
   animated skin transitions and no camera fly-to.
5. Text contrast is at least 4.5:1 for body text and 3:1 for large text,
   including over skins. UI chrome uses the design system.
6. Dark UI is supported.

## Examples

- Compliant: the "Wake me" button reads "Set arrival alarm for <place>".
- Non-compliant: a single vibration pulse with a looping sound.

## Exceptions

The map canvas is exempt from contrast rules. Its labels are not.

## Enforcement

Android Lint accessibility checks in CI; Compose semantics tests for J3;
a manual TalkBack and lock-screen pass on the alarm flows per release
(STD-TESTING).
