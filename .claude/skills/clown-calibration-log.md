# Clown Calibration Log

**Scope:** The running record of balance/calibration decisions for Clown's character-creation suggestions —
both the dev-tool agent (`clown.md`) and the in-app chat it also powers (`POST /api/v1/clown/chat`, see
`keynor-rpg/CLAUDE.md`'s "Clown chat" section). Maintained by Gaemes; referenced from `clown.md`'s Mandatory
reading (Skill 06 pattern).

**Status:** Seeded empty at creation (2026-07-21). This file only has content once real usage produces a
correction or a confirmation to record — do not pre-populate it with hypothetical entries.

## Why this exists

Without point costs implemented yet, the only thing keeping suggested characters realistic and fun is Clown's
own judgment — see `clown.md`'s balance-philosophy bullet and `clown/system-prompt.md`'s matching section.
That judgment is explicitly expected to evolve through iterative testing with the user, not be fixed once and
left alone. This file is where that evolution gets written down, so:

- A correction made once doesn't have to be re-explained the next time a similar case comes up.
- A confirmed judgment call isn't accidentally re-litigated or drifted away from in a later session.
- The in-app system prompt (`clown/system-prompt.md`) and this dev-tool agent's own behavior stay aligned on
  the same calibration, rather than silently diverging.

## How to use this file

**Reading:** apply every entry below the same way you apply a formula from `additive-attribute-formulas.md` —
as a binding fact about how to behave, not a suggestion to weigh against your own judgment.

**Writing:** add an entry whenever the user corrects a specific suggestion Clown made ("that trait was too
much for a normal soldier," "you undersold the Mind side for a scholar concept") or explicitly confirms a
judgment call that wasn't obvious from the balance philosophy alone. Do not log routine, uncontested
suggestions — only decisions that needed a correction or an explicit confirmation to settle.

Each entry:

```
### <short title> (YYYY-MM-DD)

**Situation:** what was suggested and for what character concept.
**Correction/confirmation:** what the user said.
**Rule going forward:** the concrete behavior change (or confirmed behavior) to apply next time a similar
concept comes up.
```

**Mandatory sync:** whenever an entry here would change what `clown/system-prompt.md` tells the in-app chat
(as opposed to being purely about the dev-tool agent's own conversational behavior), update that resource file
in the same delta — see `keynor-rpg/CLAUDE.md`'s "Clown chat" REST API section for the mandatory-sync rule
this mirrors.

---

## Entries

*(none yet — see Status above)*
