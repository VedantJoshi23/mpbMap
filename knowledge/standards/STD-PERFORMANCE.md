---
id: STD-PERFORMANCE
title: "Performance Budgets"
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
risk: Medium
complexity: Low
---

# STD-PERFORMANCE: Performance Budgets

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Turns KC-070 ("feels like Google or Apple Maps") into numbers.
Measured on a **mid-range reference device** and a **low-end reference
device**, both named at M8 start.

## Rules

| Metric | Mid-range | Low-end | Tool |
| --- | --- | --- | --- |
| Map pan and zoom (2D skin) | ≥ 55 fps median; jank < 5% of frames | ≥ 45 fps median | JankStats / Macrobenchmark |
| Map pan and zoom (3D skin) | ≥ 50 fps median | **2D fallback engaged** if < 40 fps | same |
| Cold start to interactive map | ≤ 2.0 s | ≤ 3.5 s | Macrobenchmark |
| Skin switch | ≤ 500 ms, camera preserved | ≤ 900 ms | Macrobenchmark |
| Typeahead result (online) | p95 ≤ 800 ms | same | backend plus client timing |
| Fix → alarm decision (BC1) | ≤ 1 s | ≤ 1 s | instrumented test |
| Trip session battery (far mode) | ≤ 3% per hour | ≤ 5% per hour | Battery Historian, S3 |

1. Every 3D skin declares its 2D fallback and is tested against the
   low-end threshold (C1).
2. A regression above 10% on any metric blocks release unless it is
   recorded with a reason.

## Examples

- Compliant: a CI benchmark job posts its numbers per release.
- Non-compliant: shipping a 3D skin with no fallback.

## Exceptions

The numbers are provisional until spikes S1 and S3 report. They may then
be revised once, recording the measured baselines.

## Enforcement

Macrobenchmark in CI (an emulator for relative numbers); device runs per
release.
