---
id: ADR-0002
title: "App Platform: Language, UI, DI and Build"
version: 1.0.0
status: Frozen
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M3
category: Decision
priority: High
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
required_by: []
related_documents:
  - DISC-TECHNICAL-ARCHITECTURE
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - technology
risk: Medium
complexity: Medium
---

# ADR-0002: App Platform: Language, UI, DI and Build

Technology decision under Oriveda `OV-004`, with **Hybrid** stack freedom
(KC-151): options are compared, one is recommended, and the owner proxy
decides (ADR-0001). Constraints: CONST-CORE (Frozen), especially Law 7
(zero spend) and the KC-151 preferences. Lifecycle: **Frozen** v1.0.0 (2026-11-02), decided by the owner proxy
under KC-151 (EVD-045). Freezing does not authorise spikes or code
(KC-107).

## Context

A native-feeling Android app (KC-070) with a UI-independent alarm engine
(Law 13). It must extend to Wear OS and Android Auto later (KC-060), and
the modular boundaries must be checkable in CI (ARCH-SYSTEM §4).
minSdk and targetSdk are open (DISC-TECHNICAL-ARCHITECTURE).

## Options Considered

- **Kotlin + Jetpack Compose (native).** The platform default, with
  first-class foreground-service, direct-boot and AlarmManager APIs.
  Compose for Wear OS and Car App Library reuse the same language. The
  map is a native view (MapLibre) embedded in Compose. Cost: Android
  only. That is acceptable because the scope is Android (KC-001).
- **Flutter.** A fast UI and cross-platform. But the alarm-critical paths
  (FGS, LOCKED_BOOT_COMPLETED, AlarmManager) need platform channels and
  native code anyway, which splits the core across two languages.
  MapLibre's Flutter plugin lags the native SDK.
- **React Native.** The same drawbacks as Flutter, plus a JS runtime in
  the safety path.

**Dependency injection**
- **Hilt.** Compile-time graph checking, Jetpack integration, the
  largest community.
- **Koin.** Simpler, but runtime resolution means graph errors surface
  late.
- **Manual DI.** No framework cost, but it does not scale to about 12
  modules.

Hilt wins on compile-time safety.

## Decision

**Kotlin (2.x) with Jetpack Compose and Material 3.**
- DI: **Hilt**, with lazy injection for credential-encrypted stores to
  meet the direct-boot constraint.
- Build: Gradle Kotlin DSL with a version catalog, one Gradle module per
  ARCH-SYSTEM §4 module.
- minSdk: **26**.
- targetSdk: **Play's current requirement for new apps**, checked at M8
  start and bumped yearly (TD14).

### Amendments from owner-proxy round 1

- **Direct boot under Hilt:** `Application.onCreate`, and every
  `@Singleton` reached eagerly from it, must not touch CE storage. CE
  stores are injected lazily (`Lazy<T>` or `Provider<T>`). This makes
  ARCH-SYSTEM's lazy-CE rule concrete.
- **Spike clause:** confirmed by S3 (boot path under Hilt). A failure
  moves the boot path to manual DI through a draft revision.

## Consequences

- One language for the app, the future Wear OS and Auto modules and,
  with ADR-0007, the backend.
- Platform APIs for every safety path, with no bridging layer.
- Android only, by design.

## Revisit Criteria

An iOS port is requested (not in scope), or Hilt's startup cost breaks
the direct-boot path in spike S3.

## Architecture Review (2026-11-02)

Checked against CONST-CORE and ARCH-SYSTEM (both Frozen), with no conflict. Law 13 is served by module-per-context.
Divergences: none (no code). Worked example under Oriveda `examples/`: not
applicable (Oriveda ADR-0002). Result: passes.
