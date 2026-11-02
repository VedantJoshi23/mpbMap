---
id: STD-SECURITY
title: "Security and Public-Repo Hygiene"
version: 0.1.0
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
CONST-CORE Law (Frozen). Lifecycle: **Draft**, under owner-proxy review
(ADR-0001). Shape: Scope, Rules, Examples, Exceptions, Enforcement.

## Scope

Secrets, the public repository, transport, auth tokens, the gateway, and
the shared VM.

## Rules

1. **No secrets in the repo**: upload keystore, `.env`, DDNS token, DB
   credentials and API keys. Ship `.gitignore` plus gitleaks secret
   scanning with the first code commit (DISC-REPO-STRUCTURE).
2. **Play App Signing.** Google holds the app signing key. The upload key
   is stored off-repo.
3. **`infra/` holds templates only.** No host IPs, ports or configs for
   the shared VM's other services (KC-124). The API hostname is the one
   sanctioned public hostname.
4. **TLS everywhere.** The Let's Encrypt certificate is validated
   normally. The bootstrap file is a hostname hint only and never carries
   secrets.
5. **Auth (ADR-0008):**
   - verify the Google ID token server-side (audience, issuer, expiry);
   - short-lived access tokens plus refresh;
   - store only Google's subject ID.
6. **Gateway (BC10):**
   - per-client and global rate limits;
   - Play Integrity on sync and gateway sessions, within quota;
   - request-size caps.
7. **Logs** never contain coordinates, tokens or Google IDs (Law 5).
8. **DDNS account:** 2FA; the update token is kept on the VM only (TD16).
9. **Dependencies:** Dependabot alerts on; review high-severity issues
   before each release.

## Examples

- Compliant: the DDNS token is read from a root-only file on the VM.
- Non-compliant: the nginx config for another site is committed under
  `infra/`.

## Exceptions

None for rules 1, 2 and 7. Others need an ADR.

## Enforcement

gitleaks in CI; Dependabot; a release checklist; backend log-format
tests (STD-TESTING rule 5).
