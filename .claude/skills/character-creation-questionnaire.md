# Character Creation Questionnaire

**Scope:** The scripted question set Clown's *interactive* mode walks a player through before proposing a character. Not used by Clown's *template* or *prompt* modes (see `clown.md`) — those infer answers to these same underlying questions from a short label or a free-text description instead of asking them one by one.

**Maintained by:** Whoever ships a change to a character-creation input or a new derived attribute — Void (backend), Dot (frontend), or Gaemes — in the same delta of work, the same mandatory-sync discipline already applied to `additive-attribute-formulas.md` and (on `keynor-rpg-client`) `formulas-reference-page.md`. A new slider, a new Knowledge/Trait/Weapon constant, or a materially changed formula is not complete until this questionnaire reflects it. Clown consumes this file but does not maintain it — Clown has no formula-design authority, only suggestion authority (see `clown.md`).

**Status:** Work in progress, seeded with the user's own example questions. Extend it as new mechanics ship; do not invent questions for mechanics that don't exist yet.

---

## How Clown uses this file

Interactive mode asks these questions in order (skipping any whose answer is already implied by an earlier one — e.g. "Soul" questions are skipped entirely while the Soul pillar has no domain model). Clown does not propose a character until every applicable question has been answered. Template and prompt-input modes use this same question list as the checklist of *what a complete character needs an opinion on* — they infer answers instead of asking, but should not silently skip a question a template/prompt genuinely gave no signal about; in that case, default toward the balanced/realistic middle per `clown.md`'s behavior rules, not toward an arbitrary extreme.

---

## Section 1 — Identity and concept

1. Will your character be male or female?
2. Do they use magic, or not at all?
3. Give me a one-sentence concept if you have one (e.g. "a retired soldier turned innkeeper").

## Section 2 — Body vs. Mind emphasis

4. Is this someone who trained their body more, their mind more, or roughly both?
5. Within the body, do they lean more toward raw power, agility/precision, or endurance?
6. Within the mind, do they lean more toward knowledge, social skill, or willpower/resolve?

## Section 3 — Physical build (Biomechanics)

7. Are they built more stocky/muscular, lean/wiry, or somewhere in between?
8. Tall, short, or average height?
9. Any notable physical trait they'd want to stand out (very strong grip, very fast, very tough, etc.)?

## Section 4 — Training background (Training and Conditioning)

10. Have they trained for explosive power, careful control, or endurance/toughness?
11. Do they have any combat training — hand-to-hand, weapons, or ranged? Which, if any?

## Section 5 — Knowledge and Labours

12. Any field of knowledge they've studied (survival, medicine, engineering, arcane studies, etc.)?
13. Any trade or labour they practice?
14. Any athletic/martial-arts specialty — archery, dancing, fencing?

## Section 6 — Personality (Values)

15. What does this character care about most — freedom, order, loyalty, truth, tradition, something else?
16. Any strong conviction or flaw that should shape how they act under pressure?

## Section 7 — Magic (Soul pillar / arcane organs) — only if Q2 was "yes"

17. *(No Soul-pillar domain model exists yet — this section is a placeholder. Do not ask magic-specific follow-up questions beyond Q2 until the Soul pillar or the arcane-organ inputs have real character-creation support.)*

---

## Example free-form prompts this questionnaire should be able to translate

These aren't extra questions — they're a sanity check that the sections above can absorb requests like the ones a player might actually type in prompt-input mode:

- "A character optimized as much as possible for speed."
- "A futuristic warrior."
- "An elf archer."

If a request implies an answer to a section above that hasn't been asked yet (interactive mode) or given (prompt mode), Clown fills it from the request; anything still unaddressed falls back to the balanced default per `clown.md`.
