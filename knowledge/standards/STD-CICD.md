---
id: STD-CICD
title: "CI/CD and Release"
version: 0.2.0
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
  - ADR-0009
  - ADR-0007
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
CONST-CORE Law (Frozen). **Tier: Medium** (Oriveda ADR-0005). Lifecycle:
**Draft** v0.2.0, the revision after owner-proxy round 1 (ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

GitHub Actions pipelines, release to Play, and backend deployment.

## Rules

1. On every push and PR:
   - build;
   - ktlint and detekt;
   - unit tests and coverage gates;
   - module-boundary checks;
   - the **dependency denylist** (STD-CODE);
   - the **style lint**, including boundary references and a hidden
     attribution control (STD-SKINS);
   - the **OpenAPI diff** (STD-API);
   - gitleaks;
   - Android Lint, including the accessibility checks.

   Renovate or Dependabot PRs pass the same gates.
2. A PR merges only when it is green. `main` is always releasable.
3. **Release checkpoint** (Oriveda ADR-0005 rule 5): every spec covering
   shipped behaviour is Frozen, or deferred with a dated Principle-7
   entry. High-tier specs (STD-INDEX tiers) cannot be deferred.
4. **Play:**
   - tracks: internal, then closed, then **staged rollout** to
     production;
   - uploaded with **Gradle Play Publisher**, using the service-account
     key from GitHub secrets (STD-SECURITY rule 2);
   - targetSdk is checked against Play's requirement (TD14).
5. **Backend:**
   - an arm64 image built in CI;
   - deploy is a scripted pull, with the DB backup taken first;
   - rollback to the previous image relies on expand/contract migrations
     (STD-DATABASE rule 3), otherwise a restore.
6. **Versioning:** semantic versions; `versionCode` increases
   monotonically; a changelog per release.

## Examples

- Compliant: a release tag runs the checkpoint script, which lists any
  Draft specs.
- Non-compliant: publishing straight to 100% production.

## Exceptions

Play has no true rollback. For a bad release: **halt the staged
rollout**, then ship a fix release with a higher `versionCode` through the
internal track.

## Enforcement

Branch protection; a release workflow with the checkpoint script.
