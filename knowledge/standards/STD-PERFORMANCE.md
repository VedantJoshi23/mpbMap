---
id: STD-PERFORMANCE
title: "Performance Budgets"
version: 1.0.0
status: Frozen
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
  - ADR-0003
  - ADR-0005
  - DISC-BUSINESS-VISION
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions: []
tags:
  - standard
risk: Medium
complexity: Low
---

# STD-PERFORMANCE: Performance Budgets

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). **Tier: Medium** (Oriveda ADR-0005). Lifecycle:
**Frozen** v1.0.0 (2026-11-02), approved by the owner proxy after rounds
1–2 (KC-162, ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Turns KC-070 ("feels like Google or Apple Maps") into numbers, measured
on a **mid-range** and a **low-end** reference device named at M8 start.

## Rules

| Metric | Mid-range | Low-end | Tool |
| --- | --- | --- | --- |
| Map pan and zoom (2D skin) | ≥ 55 fps median; jank < 5% | ≥ 45 fps median | JankStats / Macrobenchmark |
| Map pan and zoom (3D skin) | ≥ 50 fps median | 2D fallback engaged if < 40 fps | same |
| Cold start to interactive map | ≤ 2.0 s | ≤ 3.5 s | Macrobenchmark |
| Skin switch | ≤ 500 ms, camera preserved | ≤ 900 ms | Macrobenchmark |
| Typeahead result (online) | p95 ≤ 800 ms | same | client plus backend timing |
| Fix → alarm decision (BC1) | ≤ 1 s | ≤ 1 s | instrumented test |
| Alarm decision → audible ring and vibration started | ≤ 2 s | ≤ 2 s | S3 / instrumented test |
| Trip session battery (far mode) | ≤ 3% per hour | ≤ 5% per hour | Battery Historian, S3 |
| R25 detection latency | **Measured known bound** (heartbeat threshold plus `setAndAllowWhileIdle` deferral under Doze, roughly 9+ min). Not a promised target | same | S3 |

1. Every 3D skin declares its 2D fallback and is tested against the
   low-end threshold (C1).
2. A regression above 10% blocks release unless it is recorded with a
   reason.

## Examples

- Compliant: a CI benchmark job posts its numbers per release.
- Non-compliant: shipping a 3D skin with no fallback.

## Exceptions

The numbers are provisional until S1 and S3 report. They may then be
revised once, recording the measured baselines.

## Enforcement

Macrobenchmark in CI (relative numbers on an emulator); device runs per
release.

## Architecture Review (2026-11-02)

Checked against CONST-CORE Laws 1–14, ARCH-SYSTEM, and ADR-0002..0009 as
amended: no contradiction. Result: passes.
