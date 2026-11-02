---
id: DISC-REPO-STRUCTURE
title: "Discovery: Repository Structure"
version: 0.2.0
status: Draft
owner: Vedant Joshi
reviewers:
  - Owner proxy (ADR-0001)
created: 2026-11-02
updated: 2026-11-02
milestone: M1
category: Discovery
priority: Medium
depends_on:
  - DISC-BUSINESS-VISION
  - DISC-FEATURE-INVENTORY
required_by: []
related_documents: []
related_domains: []
related_features: []
related_decisions:
  - ADR-0001
tags:
  - discovery
  - repo-structure
risk: Low
complexity: Low
---

# DISC-REPO-STRUCTURE: Repository Structure

One of the ten M1 investigations (`OV-001`, Frozen), over [the evidence
log](evidence/README.md). Lifecycle: **Draft** v0.2.0, the revision after
owner-proxy round 1 (ADR-0001). Suggestions are marked **[S]**.

## Purpose

Record how the repository is organised today. Set the top-level layout
that code and assets will follow, leaving module-level design to M3
(`OV-003`).

## Observed Facts

- No application code exists (KC-007). No reference-app source is
  available (KC-008).
- The GitHub repository `VedantJoshi23/mpbMap` is **public** (KC-112).
- The Oriveda submodule is pinned at `a62b657` (KC-113, Oriveda ADR-0003).
- `knowledge/` holds this project's specs and the evidence log, and
  Oriveda's validator passes in target mode (KC-114).
- `reference/` holds the MapShifter images (© Filipp Bazun), the owner's
  inspiration table and `info.md`, now with a credit notice. There is no
  `LICENSE` file (KC-115).

## Interpretation

- The repo is a spec-first workspace that will become the product
  repository, which `OV-000` expects for an idea-stage project.
- Public visibility is itself a portfolio asset (KC-066): reviewers can
  read the specs and the history of how decisions were made.
- [S] **Monorepo.** One repository holds the Android app, the backend,
  the skin assets and the specs. For a solo developer, one public repo
  makes it easier for reviewers to find everything and keeps specs next
  to the code they govern (Oriveda Principle 4, under its ADR-0005
  amendment).
- [S] **Top-level layout**, with internal structure decided in M3:

  ```text
  android/      Android app (Gradle); alarm engine kept UI-independent (KC-060)
  backend/      API for accounts, sync, provider proxy (KC-081, KC-099)
  skins/        skin bundles as data: styles, fonts, icons, textures
  infra/        deployment templates only (see rule below)
  knowledge/    Oriveda specs (exists)
  reference/    evidence (exists; its public status is Q1)
  ```

- [S] **Public-repo hygiene rules**, to become a Standard in M4. They
  take effect with the first commit that adds code:
  - Play App Signing: Google holds the app signing key. The upload
    keystore, `.env` files and any credentials never enter the repo.
    `.gitignore` and secret scanning ship in the first code commit.
  - `infra/` holds templates only. It never contains host IPs, ports or
    nginx configs for other services on the shared VM (KC-085), or
    anything else that maps that VM's attack surface.

## Hidden Assumptions

- Secrets will not be committed before the hygiene rules exist. The risk
  is low, because no code exists yet and M8 is gated by KC-107.

## Strengths

- The specs and their evidence are versioned together, and every freeze
  is a commit.
- The submodule pin makes framework upgrades explicit (Oriveda ADR-0003).

## Weaknesses

- Third-party images sit in a public repo until Q1 is decided.
- There is no licence for the project's own work (Q1b).
- The repo name `mpbMap` will not match the eventual app name (Q-08).
  [S] Rename the repo once the name is decided; GitHub redirects old
  links.

## Questions

1. **Escalated to the owner (ADR-0001 rule 5).** The proxy recommends
   (b). An interim credit notice is in place, and nothing is deleted or
   rewritten until the owner answers.
   - **a. Third-party images in the public `reference/`.** Choose one:
     - (a) Keep them public with the credit notice.
     - (b) Remove them from the current tree. Keep them locally,
       untracked and gitignored, and replace them with links to the store
       page and the website. The evidence log already transcribes them
       (KC-009–KC-025). Reversible.
     - (c) Do (b) and also purge them from git history. This needs a
       force-push and cannot be undone.
     - (d) Make the repo private. This works against KC-066.
   - **b. Licence for the project's own code and art.** Choose
     all-rights-reserved (no LICENSE file) or an open licence. Bundled
     fonts and data (OFL, ODbL) must be compatible with the choice.
     Needed before any code is published.

   This does not block freezing: no layout decision depends on the answer
   (`OV-001` cross-investigation rule).

## Recommendations

- **Keep:** the spec-first, public monorepo; the submodule pin.
- **Improve:** the hygiene rules ship with the first code commit; the
  interim credit notice is done.
- **Remove:** whatever Q1 decides.

## Confidence Level

**Overall: Inference (70)**, capped by the proposed layout. Q1 is an
escalated open question and does not bear on the layout. Observed facts:
Fact (100).
