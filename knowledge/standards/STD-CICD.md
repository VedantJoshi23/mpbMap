---
id: STD-CICD
title: "CI/CD and Release"
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

# STD-CICD: CI/CD and Release

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

GitHub Actions pipelines, release to Play, and backend deployment.

## Rules

1. On every push and PR:
   - build;
   - ktlint and detekt;
   - unit tests and coverage gates;
   - module-boundary checks;
   - style lint (STD-SKINS);
   - gitleaks;
   - Android Lint, including the accessibility checks.
2. A PR merges only when it is green. `main` is always releasable.
3. **Release checkpoint** (Oriveda ADR-0005 rule 5): every spec covering
   shipped behaviour is Frozen, or deferred with a dated Principle-7
   entry. High-tier specs cannot be deferred.
4. **Play tracks:** internal, then closed testing, then production. The
   targetSdk is checked against Play's requirement before each release
   (TD14).
5. **Backend:** an arm64 Docker image built in CI. Deploy is a scripted
   pull on the VM, with the DB backup taken first and a rollback to the
   previous image tag.
6. **Versioning:** semantic versions; `versionCode` increases
   monotonically; a changelog per release.

## Examples

- Compliant: a release tag runs the checkpoint script, which lists any
  Draft specs.
- Non-compliant: a hotfix pushed directly to production without the
  internal track.

## Exceptions

An emergency rollback may skip the internal track. It is logged
afterwards.

## Enforcement

Branch protection; a release workflow with the checkpoint script.
