# Mind Pillar — Nature, Trait, Values, Erudition (rpg-18, 2026-07-04)

**Scope:** the `Mind` pillar and the new `InputNature`/`Trait` concepts, in this project only. Maintained by Gaemes; referenced from `gaemes.md`'s Mandatory reading (Skill 06 pattern — see workspace `SKILLS.md`). Formula-level detail (Concern mirrors, cross-pillar terms, the 9 new attributes) lives in `.claude/skills/additive-attribute-formulas.md` — this file covers the domain concepts those formulas are built on.

## Why a second pillar

`PlayableCharacter` was designed from rpg-1 to compose independent pillars (`Corpo`/`Body`, `Mente`/`Mind`, `Alma`/`Soul`) without a shared `Pilar` interface — the pillars' rules differ fundamentally enough that a unifying abstraction would not be reusable. `Mind` is the first pillar built beyond `Body`; `Soul` remains undesigned (the frontend's Soul super-tab is a disabled placeholder — see the FE skill).

`Mind` currently holds two data groups: `Values` (fourteen priorities) and `Erudition` (selected knowledge `Trait`s). Both are `InputNature.EVENTFUL`.

## InputNature — a third classification alongside genetic and trained

Every character-creation input already had an implicit two-layer nature (genetic/immutable vs. trainable — see `additive-attribute-formulas.md`'s "two-layer philosophy"). `InputNature` (`IMMUTABLE`, `TRAINED`, `EVENTFUL`) makes this explicit as a real enum so a new input's nature is a deliberate, recorded choice, and so the frontend can render a consistent badge at character creation. `EVENTFUL` is new: an input acquired through a natural or social event, not genetics or training, and (once per-input costs are defined) spent from `Mind.eventPoints` rather than `Body`'s genetic or training pools.

**When adding any new input to this project, going forward — Body, Mind, or a future pillar — declare its `InputNature` in the field's javadoc, the same way every existing field already documents whether it is genetic or trainable.** This is not a new runtime rule (nothing enforces it in code) — it is a documentation discipline this skill file establishes as a project convention, per the ticket's explicit request that a new input's nature always be a deliberate choice.

## Trait — the new input type

A `Trait` is a boolean input rendered as a checkbox in the UI, not a slider. It fits the additive-standard formula shape without a new exception: a trait's contribution is `weight × (hasTrait ? 1 : 0)` — a 0/1 input with neutral 0. See `PlayableCharacter.hasTrait(Trait)`/`flag(boolean)` and, e.g., `getSurvivalSkillsBreakdown()`.

`Trait` is a single enum (not one class per trait) with a `TraitGroup` and an `InputNature` (always `EVENTFUL` today). Every constant defined so far belongs to `Erudition` — if a future pillar introduces its own traits, keep `Erudition` holding only the subset that are genuinely knowledge traits.

**Prerequisites (general mechanism, not yet exercised):** `Trait.prerequisitesMet(PlayableCharacter)` defaults to `true` for every constant. The ticket asked for a general "can become ungreyed/greyed based on prerequisites" mechanism, but no concrete trait in this delta defines one — so no predicate/rule engine was built speculatively. When the first real prerequisite is specified, override `prerequisitesMet` on that specific constant (it receives the whole `PlayableCharacter`, so it can inspect any input or another trait). Do not generalize further than one override needs until a second, differently-shaped prerequisite actually shows up.

**Losing a previously-met prerequisite** (triggering the frontend's confirmation modal) is entirely a frontend concern for the same reason — see the FE skill's Trait section. The backend has nothing to compute here yet because no trait can currently fail its own prerequisite check.

## Erudition — free slots, not yet a real point economy

`Erudition.FREE_TRAIT_SLOTS = 2`: a character gets two knowledge traits for free. `canSelect(Trait, PlayableCharacter)` simply caps selection at that count once prerequisites are met — it does not touch `Mind.eventPoints`. Spending event points for a third-and-beyond trait is documented intent only, matching the same deferred-cost precedent already established for `Body`'s genetic/training pools (`AttributePointBudget`'s own javadoc) — implement it once the character-creation use case (and per-trait costs) actually exists, not now.

## Values — neutral is the floor, not the midpoint

Every other 1-9 (or similar) input in this codebase is neutral at its scale's midpoint. `Values` fields are 0-5 with **neutral at 1**, matching their own default — this was a deliberate inference from the ticket (every new Mind-driven attribute is specified as "default 60" while its Value input defaults to 1, which only holds if the deviation term uses neutral=1, not neutral=0 or neutral=2.5). Keep this in mind before copy-pasting a `(value - 5)` deviation pattern from a Body formula onto a Values-driven one — it must read `(value - 1)`.

**The creation-time validation rule (at least two Values away from 1, and a unique un-tied maximum) is intentionally not a domain method.** It follows the exact precedent of the hormone-neutral lock (`predominantMorphicHormone` — see both projects' character-creation skill sections): a purely frontend, UI-layer check on the current slider state, not a domain invariant enforced by `Values`' constructor or setters (which must keep accepting any combination so a player can pass through invalid intermediate states while adjusting sliders). The "Values cannot go below 1 without a specific Trait" rule is documented intent only, matching this codebase's convention for other not-yet-triggered future rules — no trait currently unlocks it.

## Concern attributes — a fourth exception to the additive standard

`SelfConcern`, `AcademicConcern`, etc. are direct mirrors of their matching `Values` field: `baseline = 0`, single term = the raw value (not a deviation from a neutral point). See `additive-attribute-formulas.md`'s exceptions list for the formal addition — this file just explains *why*: the ticket specified them as "espelhos diretos" (direct mirrors) with no independent tunable weight, so no `BodyCoefficients` field was added for the mirror itself (unlike every other weight in this codebase, which is a `BodyCoefficients` field by convention) — there is nothing to tune in a strict 1:1 mirror.

## Cross-pillar formula terms — Body attributes reading Mind inputs

`ShortMemory`, `Reasoning`, `Enfactuation`, and `Will` each gained one term reading a `Values` field (`Knowledge`, `Truth`, `Loyalty`, `Morality` respectively). This is the first time a `Body`-pillar-originated formula reads a `Mind` input — `PlayableCharacter` (the aggregate root) already owned every formula regardless of which pillar's data it read, so this required no structural change beyond adding `values()`/`erudition()` private accessors alongside the existing `genetics()`/`neuralSystem()`/etc.

**`Will` no longer delegates to `MentalHealthPool`.** Before this delta, `getWillBreakdown()` returned `getMentalHealthPoolBreakdown()` directly (documented as "expected to diverge once the Mind pillar exists"). It now builds its own breakdown — `MentalHealthPool`'s own terms plus a Morality term — so a change to `Morality` moves `Will` without moving `MentalHealthPool`. If either formula needs its own further, independent divergence later, this is the place to add it.

## REST contract — Body and Mind previewed together

The old `POST /api/v1/biomechanics/preview` (`BiomechanicsPreviewController`/`Request`/`Response`) is gone outright, not aliased — replaced by `POST /api/v1/character/preview` (`CharacterPreviewController`/`Request`/`Response`), which accepts `{ body: {...four Body groups, renamed from BiomechanicsPreviewRequest to BodyPreviewRequest}, mind: { values, erudition } }`. This became necessary once cross-pillar formulas existed: a Body-only preview could no longer correctly resolve `ShortMemory`/`Reasoning`/`Enfactuation`/`Will`. `GetPlayableCharacterService`/`CharacterController` similarly now build and serve a `Mind` alongside `Body` — `CharacterResponse` gained a `mind` field, and `attributes`/`attributeBreakdowns`/`calculatedValues`/`loadCapacity` moved from being nested under `body` to being top-level siblings of `body`/`mind`, since they were already a whole-`PlayableCharacter` concern (not `Body`-only) even before Mind existed — this rename just makes the response shape honest about that.

**`PlayableCharacter`'s two-argument constructor still exists** (`new PlayableCharacter(name, body)`), defaulting `Mind` to `Mind.humanTemplate()`, specifically so the large pre-existing `PlayableCharacterTest` suite did not need a mechanical rewrite of every call site. New tests exercising Mind-driven formulas use the three-argument constructor instead.

## Extending this pattern

When adding a new `Trait`: add the constant to `Trait` with its `TraitGroup` (add a new `TraitGroup` value if it doesn't fit an existing one), leave `prerequisitesMet` at its default unless the ticket specifies a real prerequisite, and declare which existing or new attribute formula reads it via `hasTrait(Trait.XXX)`.

When adding a new `Values` field: remember its neutral is 1, not 5 — every deviation term is `(value - 1)`.

When adding a new Mind-driven (or cross-pillar) attribute: follow the same procedure as `additive-attribute-formulas.md`'s own "Extending this pattern" section — pick neutral-anchored inputs, add one `BodyCoefficients` field per weight, verify the worst case against `attributeFloor` — except for a strict Concern-style mirror, which needs no coefficient at all.
