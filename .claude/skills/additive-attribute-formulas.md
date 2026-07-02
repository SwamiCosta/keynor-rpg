# Additive Attribute Standard (rpg-11, extended rpg-13, extended rpg-14)

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

### The three exceptions

- **`Speed`** does not add a `(input - neutral)` term for mass — it *subtracts* a mass **penalty**: `floor((SymbolicTotalMass - kSpeedMassNeutral) / kSpeedMassDivisor)`. This is what keeps Speed's worst case positive (see Safety floors below) instead of needing a hard floor like Strength does.
- **`Evasion`** and **`MaxMovementSpeed`** are anchored on `Speed`, not on `baseline` directly — they add their own deviation terms on top of whatever `Speed` already computed, rather than starting fresh from 60.
- **`FatGainRate`** and **`MuscleGainRate`** (rpg-14) do not add `baseline` at all — they are zero-baseline **rate** attributes (positive = gaining faster, negative = losing/gaining slower, zero = stable at every input's neutral value), not absolute stat values. See the rpg-14 section below.

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

## NeuralSystem absorbed SpatialIntelligence (rpg-13)

`SpatialIntelligence` (perception/agility/precision) no longer exists as its own `Body` field. Its three fields moved into `NeuralSystem` (renamed from `NervousSystem`) — `perception` was renamed `hippocampus` to match the biological-system naming the user chose for the rest of the group; `agility`/`precision` moved unchanged. `NeuralSystem` now holds ten 1-9 fields: `neuralDrive`, `neuromuscularEfficiency`, `cerebralCapacity`, `synapsisQuality`, `hippocampus`, `hypothalamus`, `amygdalaAndCingulum`, `immunity`, `agility`, `precision`. This was an explicit user instruction ("reestruturados... incorporação de Spatial Awareness dentro de Neural System") — a real domain-model merge, not just a UI grouping choice — and it broke the `body.spatialIntelligence` REST contract (removed entirely; those three values now live under `body.bodySystems.neuralSystem`).

Two more systems were added as `BodySystems` siblings, both fully trainable (mutable), all fields 1-9 neutral 5:
- **`HormonalSystem`**: `thyroid`, `adrenalGlands`.
- **`DigestiveSystem`**: `nutrientAbsorption`, `impurityCleaning`, `ketosisQuality`.

**`bloodThickness`** (added to `BloodSystem`, 1-5 neutral 3) and **`skinThickness`** (added to `Genetics`, 1-7 neutral 3) are both immutable/genetic for now, matching the precedent of every other structural/physical trait in those classes (`oxygenCarryingCapacity`, `boneDensity`). The user explicitly flagged these as provisional — expect a future revision to reconsider mutability workspace-wide. Flipping either to trainable is a two-line change (drop `final`, add a setter) with no other structural impact, by design.

`skinThickness` accepts its full 1-7 domain range even though the frontend currently locks the human character-creation slider to [2-4] — the wider range exists for future non-human races (documented as a UI-only restriction, not a domain constraint).

### The 15 rpg-13 attributes

All new attributes follow the same `baseline + Σ weight × (input - neutral)` shape — no new exceptions were introduced. Every one of them was verified by computed worst-case combination (not just eyeballed) to land inside 20-100; several hit exactly 20/100 at the extremes by design (see `PlayableCharacterTest`'s `*_atExtremes_staysWithinTwentyToOneHundred` tests). None needed a floor — natural minimums are all positive.

| Module | Attributes | Key inputs |
|---|---|---|
| Cognitive/Mental | `MemoryPool`, `Reasoning`, `ShortMemory`, `MentalHealthPool`, `Will` (= `MentalHealthPool`, kept deliberately simplified pending the future Mind pillar) | `cerebralCapacity`, `synapsisQuality`, `hippocampus`, `amygdalaAndCingulum` |
| Sensory/Hormonal/Stress | `Balance`, `StressResistance` | `hippocampus`, `neuralDrive`, `amygdalaAndCingulum`, `adrenalGlands` |
| Biological defense | `PoisonResistance`, `DiseaseResistance`, `BleedingResistance` | `immunity`, `cardiacOutput`, `bloodThickness`, `amygdalaAndCingulum` |
| Metabolic/survival | `ThermalResistance`, `BreathOutput`, `DehydrationResistance`, `StarvationResistance`, `FoodPoisoningAlcoholResistance` | `skinThickness`, `bodyFat`, `hypothalamus`, `pulmonaryCapacity`, `ketosisQuality`, `nutrientAbsorption`, `impurityCleaning`, `immunity` |

`ThermalResistance` is the one attribute with a documented sub-100 human ceiling: with `skinThickness` UI-locked to 4, the human-reachable maximum is 83 (`BodyFat`=10, `Hypothalamus`=9); the true domain ceiling (`skinThickness`=7, reserved for future races) is 98 — see `getThermalResistance_humanUiCeiling_isEightyThree` / `..._trueRaceCeiling_neverExceedsOneHundred`.

Two pre-existing formulas gained a term in rpg-13: `StaminaPool` (+`kStaminaPoolNutrientAbsorption × (NutrientAbsorption-5)`) and `FatigueResistance` (+`kFatigueResistanceHypothalamus × (Hypothalamus-5)` + `kFatigueResistanceThyroid × (Thyroid-5)`).

## Physical Traits, hormonal modifiers, and 6 new attributes (rpg-14)

A new top-level `Body` sibling, `PhysicalTraits`, joins `Biomechanics` and `BodySystems` — it groups two new sub-groups: `SensorialOrgans` (`eyesSensitivity`, `earsSensitivity`, `noseSensitivity`, all 1-9 neutral 5, mutable) and `BodyStructure` (`skinThickness` — **moved from `Genetics`, unchanged in nature: still immutable/genetic, 1-7 neutral 3** — plus new `shapeAesthetics` and `cellularHealth`, both 1-9 neutral 5, mutable). `boneDensity` **moved from `Genetics` to `BodyComposition`** and became mutable/trainable in the move (previously genetic/immutable) — the user's spec put it under "Biomechanics / Body Composition", the already-trainable class, so this was treated as a real semantic change, not just a relocation. `BodyComposition` also gained `tendonsAndLigaments` (1-9 neutral 5, mutable). `HormonalSystem` gained `predominantMorphicHormone` (1-9, neutral 5 — "theoretical" neutral, see below).

### Testosterone/Progesterone modifiers (T_mod / P_mod)

`predominantMorphicHormone` drives two private `PlayableCharacter` helpers, symmetric around its neutral point (5):

```
Tmod = (input < 5) ? 5 - input : 0   // 1-4, active only below neutral
Pmod = (input > 5) ? input - 5 : 0   // 1-4, active only above neutral
```

At input 5, both are 0 — this is why the frontend requires the player to move this slider away from neutral before finishing character creation (see the FE skill's "hormone-neutral lock" section): an undecided predisposition is a valid *default* but not a valid *finished* character, unlike every other neutral-default field in this domain.

### Formula changes

- **`Sight`/`Hearing`/`Smell` diverged** — each now reads its own `SensorialOrgans` input (`kSightEyesSensitivity`, `kHearingEarsSensitivity`, `kSmellNoseSensitivity`, all weight 6) plus the shared `Hippocampus`/`NeuralDrive` terms (now weight 1 each, down from the old shared `kSensePerception`=3/`kSenseNeuralDrive`=1) and `+ kSightPmod × Pmod` (weight 2). `getHearing()`/`getSmell()` no longer delegate to `getSight()` — they're independent formulas that happen to equal 60 at defaults because every input is at its own neutral.
- **`Balance`** gained `+ kBalanceTendons × (TendonsAndLigaments-5)` (weight 2); `kBalanceHippocampus` was reweighted 3→1 to make room for it.
- **`Strength`** gained `+ kStrengthTendons × (TendonsAndLigaments-5)` (weight 1).
- **`Durability`** gained `+ kDurabilitySkin × (SkinThickness-3)` (weight 1) — note SkinThickness's own neutral (3), same as BodyFat's inside this formula.
- **`MentalHealthPool`** (and `Will`, which still delegates to it) gained `- kMentalHealthTmod × Tmod + kMentalHealthPmod × Pmod` (both weight 5); `kMentalHealthAmygdala` was reweighted 10→5 to keep the extremes at the same [20,100] bounds now that two more terms contribute.
- **`PoisonResistance`, `DiseaseResistance`, `FoodPoisoningAlcoholResistance`** each gained `+ 2 × (CellularHealth-5)`. This widens their true (all-inputs-maxed) extremes beyond the old exact [20,100] — e.g. `PoisonResistance` now ranges [12,108] at the true combined worst/best case, not [20,100]. No floor was added (worst case well above `attributeFloor`=5) — this is a design observation, not a bug, comparable to `ThermalResistance`'s pre-existing asymmetric human-UI ceiling.

### 6 new attributes

- **Body-growth rates** (zero-baseline, see the exceptions above): `FatGainRate = (Endomorphy-5) - (Ectomorphy-5) + (NutrientAbsorption-5) - (KetosisQuality-5) - 0.5×(CellularHealth-5)`; `MuscleGainRate = (Mesomorphy-5) - (Ectomorphy-5) + (NutrientAbsorption-5) + Tmod`. These are the first formulas to read `Genetics.endomorphy`/`ectomorphy` (previously unused by any formula) and `Genetics.mesomorphy` outside `Durability`.
- **Social attributes** (baseline 60, driven by `BodyStructure.shapeAesthetics` and the hormone modifiers): `Intimidation = 60 - 5×(ShapeAesthetics-5) + 5×Tmod + 2×(SymbolicTotalMass-25)`, `Diplomacy = 60 + 7×(ShapeAesthetics-5) + 3×Pmod`, `Enfactuation = 60 + 7×(ShapeAesthetics-5) + 3×Pmod` (**currently identical to `Diplomacy`** — the design doc gave both the same formula; kept as specified and documented as a known duplication expected to diverge once the Mind pillar exists, not a copy-paste bug to silently fix), `Command = 60 + 10×|ShapeAesthetics-5|` (V-shaped — both repulsive and attractive extremes raise Command equally). `Intimidation` is unbounded in practice (uses raw `SymbolicTotalMass`, not a small deviation) — it is not expected to stay within [20,100] the way most other attributes do; this is intentional (an imposingly massive character should have no intimidation ceiling).

## Extending this pattern

When adding a new derived attribute: pick its neutral-anchored inputs, decide their weights, add one `BodyCoefficients` field per weight (named `k<Formula><Term>`), write the formula as `baseline + Σ weight × (input - neutral)`, and only add a floor if the actual worst-case combination (compute it — don't guess) lands at or below `attributeFloor`.
