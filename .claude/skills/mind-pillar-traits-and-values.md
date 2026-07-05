# Mind Pillar — Nature, Trait, Values, Erudition, Personality, Labours (rpg-18/rpg-19)

**Scope:** the `Mind` pillar and the `InputNature`/`Trait`/`Knowledge` concepts, in this project only. Maintained by Gaemes; referenced from `gaemes.md`'s Mandatory reading (Skill 06 pattern — see workspace `SKILLS.md`). Formula-level detail lives in `.claude/skills/additive-attribute-formulas.md` — this file covers the domain concepts those formulas are built on.

**rpg-19 rewrote most of what rpg-18 established.** Read this file in full even if you remember rpg-18 — several rpg-18 mechanisms (boolean knowledge traits, four cross-pillar formula terms) were reverted outright, not merely extended.

## Why a second pillar

`PlayableCharacter` composes independent pillars (`Corpo`/`Body`, `Mente`/`Mind`, `Alma`/`Soul`) without a shared `Pilar` interface — the pillars' rules differ fundamentally enough that a unifying abstraction would not be reusable. `Soul` remains undesigned.

`Mind` now holds **four** data groups: `Values` (fourteen priorities), `Erudition` (leveled `Knowledge`), `Personality` (selected `Trait`s, added rpg-19), and `Labours` (leveled `Job`, added rpg-19).

## InputNature — BIRTH (renamed from IMMUTABLE), TRAINED, EVENTFUL

`InputNature.IMMUTABLE` was renamed `BIRTH` in rpg-19 (pure rename, no behavior change) — the workspace ticket used "Birth" as the player-facing label, matching "Trained" and "Eventful". `Knowledge` and `Job` are both `TRAINED` (a change from knowledge's rpg-18 `EVENTFUL` classification, since knowledge is no longer a Trait). `Values` and every `Trait` remain `EVENTFUL`.

## Knowledge — leveled sliders, not boolean traits (rpg-19, replaces rpg-18's knowledge Traits)

The 17 knowledge domains (Ecology, Biology, Wizardry, etc.) are **no longer `Trait` constants**. They are a new enum, `Knowledge`, each a 0-4 slider ("Unknown"/"Initiate"/"Student"/"Specialist"/"Master"), grouped by the renamed `KnowledgeGroup` (same 7 groups, formerly `TraitGroup`). `Erudition` now holds a level per `Knowledge` (an `EnumMap`-backed constructor from `Map<Knowledge, Integer>`) instead of a `Set<Trait>`.

**Point budget, not free slots:** `Erudition.BASE_POINTS = 2`, spendable as 2-in-one-knowledge or 1-in-two — this replaces rpg-18's `FREE_TRAIT_SLOTS` boolean cap. The *effective* budget is per-character, not a constant: `getEffectivePoints(PlayableCharacter)` adds `character.getMind().getPersonality().getKnowledgePointsModifier()` (Iliterate costs a point, Orphan Mind grants one back — see Personality below). `setLevel(Knowledge, int, PlayableCharacter)` validates against this effective budget, not the base constant.

**Formula terms scale with level, not presence.** Where a knowledge previously contributed a flat `weight × (hasTrait ? 1 : 0)`, it now contributes `weight × level` directly (neutral 0) — same `BodyCoefficients` field, same magnitude at level 1 as the old flat bonus, but it now doubles/triples with further investment. See `getSurvivalSkillsBreakdown()`/`getAnimalCaringBreakdown()` for the only two Knowledge constants (`ECOLOGY`, `BIOLOGY`) with a real formula effect today — the other 15 knowledge domains have no wired effect yet, same as before.

## Trait — repurposed for the Values-linked personality catalog (rpg-19)

`Trait`/`TraitGroup` **no longer represent knowledge** (see Knowledge above) — they exclusively hold a new catalog of 28 Values-linked personality traits, 14 base/advanced pairs, one pair per `Values` concern. `TraitGroup`'s 14 constants (`SELF`, `FRIENDSHIP`, `ORDER`, ... `PEACE`) now name the concern each pair is linked to, replacing the old 7 knowledge-domain groups.

**Prerequisites are now real, not the rpg-18 always-`true` default:**
- Every **base** trait (e.g. `SELF_SACRIFICE`) requires its linked `Values` field to sit at **exactly its default (1)** — `Trait.prerequisitesMet(PlayableCharacter)` checks `character.getMind().getValues().getXxx() == 1`. This represents a character whose history around that concern is unremarkable, not one who has already invested in it via the slider.
- Every **advanced** trait (e.g. `SUICIDAL`) requires its pair's base trait to already be selected — checks `character.getMind().getPersonality().hasTrait(BASE)`.

**Selecting a base trait forces its linked `Values` field to 0** — a permanent personality commitment, not a reversible slider tweak. `Trait.applyForcedValue(Values)` (no-op by default, overridden per base trait) is invoked by `Personality.select(...)` immediately on selection. Deselecting a trait does **not** revert the forced value (`Personality.deselect` is a pure removal) — this was a deliberate choice: the ticket describes these as one-way personality-forming events ("something in the character's past..."), not toggles.

**Effect split (explicit product decision, not an inference):** every *passive, unconditional* bonus a trait grants is a real additive-standard term on the affected attribute — e.g. Self Sacrifice's `+4 Fear Resistance`/`+8 Pain Threshold`. Every *situational* effect (bonuses only against a specific opponent type, stress relief tied to a specific narrative action) has **no backing mechanic** (no resisted-test or stress-event system exists) and lives only in `Trait.getDescription()` as tooltip text — it is not a formula term and must not become one without a real mechanic to back it. See `Trait`'s own javadoc for the full rationale before adding a new trait or judging whether an effect belongs in a formula or in the description string.

**Two traits also adjust a sibling budget instead of (or in addition to) an attribute:** `Trait.getKnowledgePointsModifier()` (`ILLITERATE`: −1, `ORPHAN_MIND`: +1, both else 0) and `Trait.getLabourPointsModifier()` (`CONSERVATIVE`/`LUDDITE`: +1 each, both else 0). `Personality` sums these across every selected trait.

## Personality — the new third data group (rpg-19)

`Personality` holds `Set<Trait> selectedTraits`. Unlike `Erudition`, there is **no shared point budget** here — each trait is gated purely by its own `prerequisitesMet` check (a concern at its default, or the pair's base already selected). `select(Trait, PlayableCharacter)` throws if prerequisites aren't met; on success it applies the trait's forced value immediately.

## Labours / Job — mirrors Erudition/Knowledge exactly (rpg-19, new)

`Job` (Masonry, Tailoring, Carpentry, Building, Blacksmithing, Brewing, Cooking) is structurally identical to `Knowledge` — 0-4 slider, always `TRAINED` — but carries **no formula effect of its own**; only the point budget matters today (`Labours.BASE_POINTS = 2`, adjustable by Conservative/Luddite via `Personality.getLabourPointsModifier()`). All seven jobs belong to a single implicit group — no `JobGroup` enum was added, since the ticket specified "just one group, Jobs."

## Values — unchanged shape, several cross-pillar terms reverted (rpg-19)

`Values`' own shape (14 fields, 0-5, neutral 1) is unchanged from rpg-18. What changed: **`ShortMemory`, `Reasoning`, `Enfactuation`, `Will`, `Bluffing` (both its Truth and Morality terms), `Faith`, `IllusionResistance` (renamed, see below), and `Creativity` all had their rpg-18 `Values`-reading term removed outright**, per explicit user instruction — verified against the actual formula code, not assumed from a changelog, since all 9 terms were confirmed still present on `main` before this revert (do not trust a changelog summary over `PlayableCharacter.java` itself for questions like this). Four of the eight (`ShortMemory`, `Reasoning`, `Enfactuation`, `Will`) pre-existed rpg-18 and simply lost their added term; the other four (`Bluffing`, `Faith`, `IllusionResistance`, `Creativity`) were rpg-18-only attributes and now have no `Values`-driven term at all — most gained a `Trait`-driven replacement instead (see `additive-attribute-formulas.md`).

The creation-time validation rule (at least two Values away from 1, unique un-tied maximum) is still frontend-only, unchanged from rpg-18.

## Concern attributes — unchanged (rpg-18)

`SelfConcern`, `AcademicConcern`, etc. still directly mirror their matching `Values` field (baseline 0, no coefficient). Unaffected by rpg-19.

## Renames (rpg-19)

- `IllusionResistanceSanity` → `IllusionResistance` (attribute, DTO field, coefficient names dropped entirely rather than renamed, since the formula itself changed — see above).
- `DisplayMassKg` → `TotalMassKg` (`PlayableCharacter.getTotalMassKg()`, `CalculatedValuesResponse.totalMassKg`).

## REST contract — Mind now carries four groups (rpg-19, extends rpg-18)

`MindResponse` gained `personality` (`PersonalityResponse.selectedTraits`) and `labours` (`LaboursResponse` — same `{levels, points}` shape as the rewritten `EruditionResponse`). `EruditionResponse` changed from `{selectedTraits, freeTraitSlots}` to `{levels: Map<String,Integer>, points: PointBudgetResponse}`. `MindPreviewRequest`/`CharacterPreviewRequest` similarly gained `personality`/`labours` fields (`PersonalityInput`, `LaboursInput`), and `PreviewAttributesUseCase.calculate(...)` grew from 5 to 7 parameters — a breaking signature change, same precedent as rpg-18's own breaking change to this method.

## Extending this pattern

When adding a new `Trait` (personality): pick its linked `Values` concern, add the base/advanced pair to the matching `TraitGroup`, override `prerequisitesMet`/`applyForcedValue` on the base constant, and add real formula terms only for unconditional bonuses — situational effects go in `getDescription()` only.

When adding a new `Knowledge` or `Job`: add the constant, declare `TRAINED`, and if it needs a real formula effect, read its level directly (`erudition().getLevel(Knowledge.XXX)` or `labours().getLevel(Job.XXX)`) as a multiplier — no flag/boolean pattern.

When adding a new `Values` field: remember its neutral is 1, not 5.
