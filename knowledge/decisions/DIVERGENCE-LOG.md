---
id: DIVERGENCE-LOG
title: "Kairos — Divergence Log"
version: 0.1.0
status: Draft
owner: Vedant Joshi
reviewers: []
created: 2026-11-02
updated: 2026-11-02
milestone: M8
category: Decision
priority: High
depends_on: []
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0005
  - ADR-0007
tags:
  - divergence
risk: Medium
complexity: Low
---

# Divergence Log

Save this file as `knowledge/decisions/DIVERGENCE-LOG.md`. A project
has exactly one log, and `DIV-NNN` numbers are per project
(`ADR-0007` rule 5).

A divergence is a recorded disagreement between code and a
specification that is **not yet Frozen** (`ADR-0005` rule 1). A
disagreement with a Frozen specification is not a divergence: the code
is wrong, and it is fixed or the spec is formally revised.

**Adoption entries** are the one exception (`ADR-0007` rule 2). When an
Oriveda upgrade brings a rule that existing work already breaks, the
upgrade change logs each case here as an `open` row with a **Due** date.
Such a row may name a Frozen spec. It records a known gap; it does not
make the code right. Leave Due empty for ordinary divergences.

Rules (`ADR-0005` rule 4):

- A change that introduces a divergence logs it **in the same change**.
  An unlogged divergence counts as a defect.
- A specification cannot be Frozen while it has `open` entries here.
- An `open` row against a Frozen spec needs a Due date, and no `open`
  row may pass its Due date. `scripts/validate.py --root` checks both.
- Entries are never deleted. A resolved entry keeps its history.

This is a living log, never Frozen. Its own `status` stays `Draft`.

## Entries

Slice 1 code (branch `m8/slice-1`) against Frozen specs. Each row is a
known gap with a Due date. The code is wrong until it is fixed (ADR-0005
rule 1).

| ID | Spec § | Spec says | Code does | Found in | Status | Due | Resolved |
| --- | --- | --- | --- | --- | --- | --- | --- |
| DIV-001 | `STD-CICD` rule 1; `STD-CODE` rules 1, 6 | CI runs ktlint, detekt (with the location-interpolation rule), module-boundary checks, the dependency denylist and Android Lint accessibility checks | CI runs the build, unit tests, the spec validator, the skin lint, the manifest Law 3 check and gitleaks only | `m8/slice-1` | open | 2026-11-30 | |
| DIV-002 | `STD-TESTING` rules 2, 7 | Instrumented safety-path tests (a)–(i) and coverage gates (≥90% trip domain) | JVM unit tests (58) plus one emulator launch smoke test; safety-path instrumented tests (a)–(i) and the coverage gate are still missing | `m8/slice-1` | open | 2026-11-30 | |
| DIV-003 | `FEAT-SKINNED-MAP` §3 (bootstrap) | Fetch the bootstrap file, enforce the allowlist, keep last-known-good | `ConfigResolver` is implemented and tested, but the app uses only the built-in config; no fetch or last-known-good persistence yet | `m8/slice-1` | open | 2026-11-30 | |
| DIV-004 | `STD-ACCESSIBILITY` rule 3; `FEAT-ARRIVAL-ALARM` §3 | The quest marker is distinguished by shape and label, not colour alone | My-location and quest markers are circles that differ only by colour and size | `m8/slice-1` | open | 2026-11-30 | |
| DIV-005 | `FEAT-LOCATION-CONSENT` §3 (withdrawal) | Consent can be withdrawn in Settings, with defined effects per kind | Withdrawal is supported by `ConsentApi`, but there is no Settings screen yet | `m8/slice-1` | open | 2026-11-30 | |

| ID | Spec § | Spec says | Code does | Found in | Status | Due | Resolved |
| --- | --- | --- | --- | --- | --- | --- | --- |

**Status values:** `open` · `spec-updated` · `code-fixed` · `both`.
**Due:** `YYYY-MM-DD`; required for adoption entries, empty otherwise.

### Notes

Use this section only when a one-line row can't explain a resolution
(for example a divergence that led to an ADR). Head each note with its
`DIV-NNN` ID.
