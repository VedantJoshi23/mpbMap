---
id: STD-OBSERVABILITY
title: "Observability"
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
risk: Medium
complexity: Low
---

# STD-OBSERVABILITY: Observability

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Crash reports, backend logs and metrics. Excludes user analytics, which
are forbidden (Law 8).

## Rules

1. **ACRA** crash reports go to the backend (ADR-0009). Logcat and
   location fields are excluded. Reports are kept for 90 days.
2. **Backend logs** are structured JSON: request ID, route, status and
   latency. **No coordinates, tokens or Google IDs** (Law 5).
3. **Metrics** come from the backend only:
   - gateway error rate per provider (TD1, TD2, TD11 triggers);
   - 429 counts;
   - Integrity quota use (TD12);
   - JVM memory (TD8).
4. **Alerts:** a simple threshold check (cron plus email or a GitHub
   issue) on provider error rate, memory and disk. Zero spend.
5. **Safety-path telemetry stays on the device.** It sits in a local
   debug log the user can export, never uploaded automatically.

## Examples

- Compliant: `{"route":"/v1/search","status":200,"ms":184}`.
- Non-compliant: `"q":"28.61,77.20"` in a log line.

## Exceptions

None for rule 2.

## Enforcement

Log-format tests; a review checklist.
