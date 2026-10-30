# CLAUDE.md — Oriveda bootstrap

This project is governed by the **Oriveda** engineering framework
(https://github.com/VedantJoshi23/Oriveda), pulled in as a git submodule at
`.oriveda-framework/` (per Oriveda `ADR-0003`).

**This file is a pointer, not a copy.** It says where the rules live; it
never restates them. If it starts accumulating restated Laws, Standards,
invariants or protocol steps instead of references, that is a mistake to
fix, not a convenience to keep.

## Before any action

1. Identify which `PRM-*` prompt applies to the current task. The full
   catalog is in
   `.oriveda-framework/knowledge/prompts/OV-008-prompt-library-protocol.md`;
   each prompt lives beside it as `.oriveda-framework/knowledge/prompts/PRM-*.md`.
2. Load the specs that prompt points to, and mark each one as **Frozen** or
   **Draft** from its frontmatter `status`.
3. Frozen specs bind the work. Draft specs inform it; any disagreement with
   a Draft spec is logged as a divergence (`DIV-NNN`, per Oriveda
   `ADR-0005`), never resolved silently.
4. Never restate a spec's content here or in a prompt — cite its ID.

## Constitution

None yet. Once this project has a Frozen Constitution under
`knowledge/constitution/` (this project's, not the submodule's), link it
here. Its Laws must never be silently violated.

## Where this project's artifacts live

`knowledge/` mirrors Oriveda's layout — `discovery/` (evidence log under
`discovery/evidence/`), `constitution/`, `architecture/`, `standards/`,
`domains/`, `features/`, `decisions/`. These are this project's own
artifacts and use project-scope IDs (`EVD-`, `KC-`, `DISC-`, `CONST-`,
`ARCH-`, `STD-`, `DOM-`, `FEAT-`, `ADR-`, `DIV-`), never Oriveda's `OV-`
numbering.

## Current stage

`PRM-DISCOVERY` (M1): Knowledge Acquisition and Discovery.
