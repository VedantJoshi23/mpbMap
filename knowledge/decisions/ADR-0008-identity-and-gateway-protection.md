---
id: ADR-0008
title: "Identity and Gateway Protection"
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
risk: High
complexity: Medium
---

# ADR-0008: Identity and Gateway Protection

Technology decision under Oriveda `OV-004`, with **Hybrid** stack freedom
(KC-151): options are compared, one is recommended, and the owner proxy
decides (ADR-0001). Constraints: CONST-CORE (Frozen), especially Law 7
(zero spend) and the KC-151 preferences. Lifecycle: **Frozen** v1.0.0 (2026-11-02), decided by the owner proxy
under KC-151 (EVD-045). Freezing does not authorise spikes or code
(KC-107).

## Context

The constraints:
- optional accounts (Law 4) with in-app and web deletion (Law 6, KC-093);
- no email infrastructure, and zero spend;
- gateway abuse protection (DISC-TECHNICAL-ARCHITECTURE constraint);
- commercial free tiers must be flagged (precedent).

## Options Considered

**Sign-in**
- **Sign in with Google through Credential Manager.** The backend
  verifies the Google ID token. Free, low friction, no passwords stored.
  A Google dependency (TD15).
- **Firebase Authentication.** Free tier, but it adds the Firebase SDK and
  a Google project, and its analytics-adjacent defaults need care
  (Law 8).
- **Self-hosted email OTP or magic link.** Needs outbound email, which
  free SMTP limits make unreliable, and stores emails.

**Session:** the server issues its own short-lived access token plus a
refresh token after verifying the ID token.

**Gateway app identity**
- **Play Integrity API.** Strong, but a **commercial free tier with
  quotas** (flagged).
- **A signed app token.** Extractable, so it only deters casual misuse.
- **Rate limits only.**

## Decision

- **Sign in with Google (Credential Manager).** The backend verifies the
  ID token and issues its own session tokens. The only stored identifier
  is Google's subject ID, never an email unless needed.
- **Web deletion:** the GitHub Pages form signs in with the same Google
  account and posts to the backend, with a contact-address fallback.
- **Gateway:**
  - per-client and global rate limits for every client;
  - **Play Integrity** checked on sync endpoints and on gateway sessions,
    within its free quota (**flagged commercial free tier**; quota
    exhaustion degrades to rate-limit-only, never to an outage);
  - **never a paid tier** without the owner (rule 2).

### Amendments from owner-proxy round 1

- **Refresh token:** stored in CE storage, wrapped with an **Android
  Keystore** key (not the deprecated EncryptedSharedPreferences). Account
  deletion revokes every server session.
- **Disclosure:** the Data safety form and privacy policy disclose the
  device-integrity data sent to Google by Play Integrity and by
  Credential Manager sign-in (R12, STD-COMPLIANCE).
- No spike dependency.

## Consequences

- No passwords or emails to protect.
- Google services are required for sign-in. Guests are unaffected
  (Law 4).
- Integrity raises the cost of abuse.

## Revisit Criteria

Integrity quota pressure (TD12); demand from devices without Google
services (TD15).

## Architecture Review (2026-11-02)

Checked against CONST-CORE and ARCH-SYSTEM (both Frozen), with no conflict. Laws 4, 6, 7 and 10 are satisfied.
Divergences: none (no code). Worked example under Oriveda `examples/`: not
applicable (Oriveda ADR-0002). Result: passes.
