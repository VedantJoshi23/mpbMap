---
id: STD-OBSERVABILITY
title: "Observability"
version: 1.0.0
status: Frozen
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
  - ADR-0007
  - ADR-0009
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
CONST-CORE Law (Frozen). **Tier: Medium** (Oriveda ADR-0005). Lifecycle:
**Frozen** v1.0.0 (2026-11-02), approved by the owner proxy after rounds
1–2 (KC-162, ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Crash reports, backend logs, metrics and the on-device safety log.
User analytics are forbidden (Law 8).

## Rules

1. **ACRA** posts to the backend (ADR-0009) using an **explicit field
   allowlist**: stack trace, app version, Android version, device model,
   build. It **excludes** USER_EMAIL, DEVICE_ID, logcat, shared-preference
   dumps and custom data. Messages are clean by STD-CODE rule 5. Reports
   are kept 90 days.
2. **Logs:**
   - backend logs are structured JSON (request ID, route, status,
     latency) and never contain coordinates, tokens or Google IDs;
   - the API vhost's **nginx access log** omits query strings
     (STD-SECURITY rule 7).
3. **Metrics** (backend only):
   - gateway error rate per provider (TD1, TD2, TD11);
   - 429 counts;
   - **nginx `limit_req` rejections**;
   - Integrity quota (TD12);
   - JVM memory (TD8);
   - **HikariCP pool saturation**.
4. **Alerts** (cron plus email or a GitHub issue; zero spend):
   - provider error rate, memory, disk;
   - **failure or backlog of deletion and purge jobs** (Law 6's disclosed
     windows);
   - **backup job failure** (TD13).
5. **On-device safety debug log:**
   - records heartbeat, watchdog and alert events for R25 diagnosis;
   - **no coordinates** (distances and timestamps only);
   - bounded retention (7 days);
   - **cleared by EraseLocalHistory** (Law 6);
   - exportable by the user, never uploaded automatically.

## Examples

- Compliant: `{"route":"/v1/search","status":200,"ms":184}`.
- Non-compliant: an ACRA report with `DEVICE_ID`.

## Exceptions

None for rules 1, 2 and 5.

## Enforcement

ACRA config review; log-format tests; alert test runs per release.

## Architecture Review (2026-11-02)

Checked against CONST-CORE Laws 1–14, ARCH-SYSTEM, and ADR-0002..0009 as
amended: no contradiction. Result: passes.
