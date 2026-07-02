# Additive Attribute Standard (rpg-11)

**Scope:** `PlayableCharacter`'s derived-attribute formulas and `BodyCoefficients`, in this project only. Maintained by Gaemes; referenced from `gaemes.md`'s Mandatory reading (Skill 06 pattern — see workspace `SKILLS.md`).

## Why this exists

Before rpg-11, every derived attribute (Strength, Speed, Durability, etc.) had its own bespoke formula — square-cube laws, power-to-weight ratios, logarithms, per-attribute multiplicative modifiers. Each was individually well-reasoned, but the *set* of formulas had no shared shape: some outputs sat around 0-2, others around 20-100, with no common baseline a player could learn once and reuse across every stat. rpg-11 replaced the whole set with one shape, on user request, to make the game's numbers assertive and predictable.

## The additive standard

Every derived attribute (with the two documented exceptions below) has the shape:

```
Attribute = baseline + Σ (weight_i × (input_i - neutral_i))
```

- **`baseline` = 60** (`BodyCoefficients.getBaseline()`). At every input's neutral value, every attribute equals exactly 60 — this is the single number a player can learn once ("60 is average") and apply to every stat in the game.
- **Neutral points are scale midpoints, not coefficients.** Most 1-9 inputs are neutral at 5; `limbRatio` (1-5) is neutral at 3; `bodyFat` (1-10) is neutral at 3, not 5, only inside `getDurability()` — everywhere else `bodyFat` is used as a direct (non-deviation) mass input. Neutral points are literals inside `PlayableCharacter`'s formulas, not `BodyCoefficients` fields, since they describe the scale itself rather than a tunable weight.
- **Weights are `BodyCoefficients` fields**, named `k<Formula><Term>` (e.g. `kStrengthMuscleMass`). Every weight in the design document is exposed this way so game balance can be tuned without touching formula code — this was already the pre-rpg-11 convention and rpg-11 preserves it even though the underlying formula shape changed completely.

### The two exceptions

- **`Speed`** does not add a `(input - neutral)` term for mass — it *subtracts* a mass **penalty**: `floor((SymbolicTotalMass - kSpeedMassNeutral) / kSpeedMassDivisor)`. This is what keeps Speed's worst case positive (see Safety floors below) instead of needing a hard floor like Strength does.
- **`Evasion`** and **`MaxMovementSpeed`** are anchored on `Speed`, not on `baseline` directly — they add their own deviation terms on top of whatever `Speed` already computed, rather than starting fresh from 60.

## Two mass numbers, not one

- **`SymbolicTotalMass`** (int) = `kSymbolicMassBase + Height + MuscleMass + BodyFat + (BoneDensity - 5)`. Abstract, game-balance-only — feeds the mass penalty inside `Speed` and `FatigueResistance`. At every input's neutral value it equals 25, which is why `kSpeedMassNeutral` and `kFatigueResistanceMassNeutral` are both 25 (not a coincidence with the attribute baseline of 60 — two independent constants that happen to share a value at neutral).
- **`DisplayMassKg`** (double) = `MuscleKg + FatKg + FrameKg + BoneModKg`. Real-world kg shown to the player. Computed on the backend (not the frontend) specifically so `DragCapacityKg` — which mixes `DisplayMassKg` with `Strength` — has a single source of truth. The frontend never recomputes either mass number; it only formats individual slider values (e.g. showing "175 cm" next to the height slider) from data it already has locally, which is a linear label conversion, not a duplicated gameplay formula.

## Safety floors

Four attributes apply `Math.max(BodyCoefficients.getAttributeFloor(), raw)` (floor defaults to 5): **Strength**, **FatigueResistance**, **Evasion**, **MaxMovementSpeed**. These were identified by computing the actual worst-case slider combination for all 13 derived attributes (see `PlayableCharacterTest`'s floor tests) — the other 9 attributes have a natural worst-case comfortably above the floor and do not need one.

At `baseline = 35` (the design document's original value before the user raised it to 60), the four floored attributes' worst-case combos landed at -1, -17, -14, and -6 respectively — genuinely reachable through slider extremes. At `baseline = 60`, the same worst-case combos land at 24, 8, 11, and 19 — all positive on their own. **The floors are kept anyway**, as defense-in-depth against future coefficient tuning or scale changes, not because today's ranges require them. `Speed` never needed a floor at either baseline (its mass-penalty divisor keeps the worst case at 2 / 27 respectively) — see `PlayableCharacterTest.getSpeed_worstCaseSliderCombination_staysPositiveWithoutAFloor`.

## Load capacity (added rpg-11, recalibrated rpg-12)

`MaxCapacityKg`, `LightLoadKg`, `HeavyLoadKg`, `DragCapacityKg` are all derived from `Strength` (and `DisplayMassKg` for `DragCapacityKg` specifically) — see `PlayableCharacter`'s Load capacity section. All four return `int` (whole kg), unlike every other derived attribute (`double`) — this matches the design document's own `int` arithmetic and reflects that a carry-capacity ceiling doesn't need fractional-kg precision.

```
MaxCapacityKg  = floor(Strength^2 / kMaxCapacityDivisor) + Strength   (Strength truncated to int; kMaxCapacityDivisor = 150)
LightLoadKg    = floor(MaxCapacityKg / kLightLoadDivisor)             (kLightLoadDivisor = 3 — exactly one third)
HeavyLoadKg    = floor(MaxCapacityKg * kHeavyLoadMultiplier / kHeavyLoadDivisor)  (2/3 — the practical carry ceiling)
DragCapacityKg = kDragCapacityMultiplier * MaxCapacityKg + floor(DisplayMassKg * kDragCapacityMassFraction)
```

**`MaxCapacityKg` reads `Strength` directly — no offset.** rpg-11 originally introduced `kLoadCapacityStrengthOffset` (subtracting 25 from `Strength` before the load formula) to keep load numbers calibrated after `baseline` was raised from 35 to 60. rpg-12 replaced that entirely: the divisor itself was recalibrated (25 → 150) so the formula produces the same result working directly off the baseline-60 `Strength`, with no offset needed. **Do not reintroduce an offset alongside this divisor** — the two corrections solve the same problem and combining them would double-correct.

At human defaults: `Strength = 60`, `MaxCapacityKg = floor(3600/150) + 60 = 84`, `LightLoadKg = 28`, `HeavyLoadKg = 56`, `DragCapacityKg = 2×84 + floor(71×0.5) = 203`.

`MaxCapacityKg` inherits `Strength`'s floor transitively (`Strength` can never go below `attributeFloor`, 5): it can never go below `floor(5²/150) + 5 = 5`.

## Removed and renamed (breaking changes)

- **`CardiovascularCapacity`** removed entirely — the three cardiovascular inputs (`oxygenCarryingCapacity`, `cardiacOutput`, `pulmonaryCapacity`) now feed `StaminaPool`, `FatigueResistance`, and `StaminaRecovery` directly with per-formula cross-weights, instead of funneling through one shared intermediate average.
- **`FatigueRate(intensity)`** removed, replaced by **`FatigueResistance`** (no `intensity` parameter — fully static baseline attribute) — semantics inverted: higher `FatigueRate` used to mean "fatigues faster" (worse); higher `FatigueResistance` means "resists fatigue better" (better).
- **`EnergyCost(intensity)`** removed — it was already excluded from the REST contract before rpg-11 (real-time-activity-dependent, not a static trait) and depended on the old mass model that no longer exists.
- **`StaminaRecovery`** is new — oxygenation-led recovery speed, penalized by fast-twitch fiber bias.

## Extending this pattern

When adding a new derived attribute: pick its neutral-anchored inputs, decide their weights, add one `BodyCoefficients` field per weight (named `k<Formula><Term>`), write the formula as `baseline + Σ weight × (input - neutral)`, and only add a floor if the actual worst-case combination (compute it — don't guess) lands at or below `attributeFloor`.
