---
id: STD-SECURITY
title: "Security and Public-Repo Hygiene"
version: 0.2.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M4
category: Standard
priority: Critical
depends_on:
  - CONST-CORE
  - ARCH-SYSTEM
  - ADR-0007
  - ADR-0008
  - ADR-0009
  - DISC-REPO-STRUCTURE
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

# STD-SECURITY: Security and Public-Repo Hygiene

A Standard under Oriveda `OV-005` (Frozen). It may never contradict a
CONST-CORE Law (Frozen). **Tier: High** (Oriveda ADR-0005). Lifecycle:
**Draft** v0.2.0, the revision after owner-proxy round 1 (ADR-0001).
Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Secrets, the public repo, transport, auth, the gateway, the database and
the shared VM.

## Rules

1. **No secrets in the repo**: upload keystore, `.env`, DDNS token, DB
   credentials, API keys and the Play service-account key. `.gitignore`
   plus gitleaks ship with the first code commit.
2. **Signing and release keys:**
   - Play App Signing holds the app signing key;
   - the upload key is kept off-repo;
   - the **Play service-account key** (Gradle Play Publisher, ADR-0009)
     lives only in GitHub encrypted secrets, with release-manager
     permissions only, and is **never exposed to fork PRs**.
3. **`infra/` holds templates only.** No host IPs, ports or configs for
   the shared VM's other services (KC-124). The API hostname is the one
   sanctioned public hostname.
4. **The bootstrap file** (ADR-0009) carries only the API base URL plus
   the tile, glyph and DEM source endpoints, never secrets or styles.
   - **Threat:** a compromised Pages repo could redirect the API to a
     host with a valid certificate.
   - **Mitigation:** HTTPS only, and the API host must match a bundled
     allowlist pattern (the DDNS domain). Otherwise the app keeps its
     last known good value or the built-in default. A residual risk is
     recorded with TD16.
5. **Auth** (ADR-0008):
   - verify the Google ID token server-side (audience, issuer, expiry);
   - short-lived access tokens plus refresh;
   - the server stores **only hashes** of refresh tokens and only Google's
     subject ID;
   - on the device, the refresh token is in CE storage, **wrapped by an
     Android Keystore key** (never EncryptedSharedPreferences);
   - **account deletion revokes every session.**
6. **Gateway and ingest** (ADR-0007, ADR-0008, ADR-0009):
   - per-client and global rate limits;
   - **nginx `limit_req`** in front as a defence-in-depth layer;
   - the **ACRA endpoint** gets the same limits;
   - request-size caps;
   - Play Integrity on sync and gateway sessions, a **flagged commercial
     free tier** (Law 7) whose quota exhaustion degrades to
     rate-limit-only, never to an outage.
7. **Logs:**
   - application logs never contain coordinates, tokens or Google IDs;
   - the **API vhost's nginx `log_format` omits query strings**;
   - coordinates travel only in POST bodies (STD-API) (Law 5).
8. **Database:**
   - a dedicated **least-privilege role**, limited to the project
     database, reached through the Docker host gateway;
   - PostgreSQL is **never exposed publicly** (ADR-0007).
9. **DDNS account:** 2FA; the update token is kept on the VM only (TD16).
10. **Dependencies:** Renovate or Dependabot update PRs (ADR-0009) plus
    security alerts. Update PRs pass the full CI gate. High-severity
    issues are reviewed before each release.

## Examples

- Compliant: the DDNS token is read from a root-only file on the VM.
- Non-compliant: an nginx access log line containing `?lat=`.

## Exceptions

None for rules 1, 2, 5, 7 and 8. Others need an ADR.

## Enforcement

gitleaks in CI; Renovate or Dependabot; the release checklist; backend
and nginx log-format tests (STD-TESTING rule 5).
