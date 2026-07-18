# Additive Attribute Standard (rpg-11, extended rpg-13, extended rpg-14, extended Delta V4)

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

### The exceptions

- **`Speed`** does not add a `(input - neutral)` term for mass — it *subtracts* a mass **penalty**: `floor((SymbolicTotalMass - kSpeedMassNeutral) / kSpeedMassDivisor)`. This is what keeps Speed's worst case positive (see Safety floors below) instead of needing a hard floor like the Strength-family does.
- **`Evasion`** and **`MovementSpeed`** are anchored on `Speed`, not on `baseline` directly — they add their own deviation terms on top of whatever `Speed` already computed, rather than starting fresh from 60.
- **`FatGainRate`** and **`MuscleGainRate`** (rpg-14) do not add `baseline` at all — they are zero-baseline **rate** attributes (positive = gaining faster, negative = losing/gaining slower, zero = stable at every input's neutral value), not absolute stat values. See the rpg-14 section below.
- **`PushStrength`/`LegDrive`/`GripStrength`/`LiftStrength`** (Delta V4) are anchored on the hidden `meanStrength()` engine, not on `baseline` directly — same pattern as Evasion/MovementSpeed anchoring on Speed, just with a different anchor attribute. See the Delta V4 section below.
- **`Balance`** (rebuilt Delta V4) uses another *already-resolved* attribute, `LegDrive`, as an additive **term** (`kBalanceLegDrive × (LegDrive - 60)`) rather than as a base — the first formula in the codebase to do this. Unlike Evasion/MovementSpeed/the four strengths (which replace `baseline` with another attribute's *value*), Balance keeps its own `baseline` (60) and treats `LegDrive`'s deviation from 60 as just another weighted term alongside `Thalamus` and `NeuralDrive`.
- **The 14 Concern attributes** (rpg-18, Mind pillar) are direct mirrors of their matching `Values` field — `baseline = 0`, single term = the raw value, not a deviation from a neutral point. No `BodyCoefficients` field backs the mirror itself (there is nothing to tune in a strict 1:1 mirror) — see `.claude/skills/mind-pillar-traits-and-values.md`.

## Two mass numbers, not one

- **`SymbolicTotalMass`** (int) = `kSymbolicMassBase + Height + MuscleMass + BodyFat + (BoneDensity - 5)`. Abstract, game-balance-only — feeds the mass penalty inside `Speed` and `FatigueResistance`. At every input's neutral value it equals 25, which is why `kSpeedMassNeutral` and `kFatigueResistanceMassNeutral` are both 25 (not a coincidence with the attribute baseline of 60 — two independent constants that happen to share a value at neutral).
- **`DisplayMassKg`** (double) = `MuscleKg + FatKg + FrameKg + BoneModKg`. Real-world kg shown to the player. Computed on the backend (not the frontend) specifically so `DragCapacityKg` — which mixes `DisplayMassKg` with `Strength` — has a single source of truth. The frontend never recomputes either mass number; it only formats individual slider values (e.g. showing "175 cm" next to the height slider) from data it already has locally, which is a linear label conversion, not a duplicated gameplay formula.

## Safety floors

Eight attributes apply `Math.max(BodyCoefficients.getAttributeFloor(), raw)` (floor defaults to 5): the four specialized strengths (**PushStrength**, **LegDrive**, **GripStrength**, **LiftStrength** — Delta V4, replacing the old floored **Strength**), **SwingPower**/**GrapplingSelfLifting** (Delta V4, floored on their own averaged result), **FatigueResistance**, **Evasion**, **MovementSpeed**. These were identified by computing the actual worst-case slider combination (see `PlayableCharacterTest`'s floor tests) — every other derived attribute has a natural worst-case comfortably above the floor and does not need one.

At `baseline = 35` (the design document's original value before the user raised it to 60), the four floored attributes' worst-case combos landed at -1, -17, -14, and -6 respectively — genuinely reachable through slider extremes. At `baseline = 60`, the same worst-case combos land at 24, 8, 11, and 19 — all positive on their own. **The floors are kept anyway**, as defense-in-depth against future coefficient tuning or scale changes, not because today's ranges require them. `Speed` never needed a floor at either baseline (its mass-penalty divisor keeps the worst case at 2 / 27 respectively) — see `PlayableCharacterTest.getSpeed_worstCaseSliderCombination_staysPositiveWithoutAFloor`.

## Load capacity (added rpg-11, recalibrated rpg-12, re-anchored on LiftStrength Delta V4)

`MaxCapacityKg`, `LightLoadKg`, `HeavyLoadKg`, `DragCapacityKg` are all derived from `LiftStrength` (renamed from `Strength`, Delta V4 — see below) and `DisplayMassKg` for `DragCapacityKg` specifically) — see `PlayableCharacter`'s Load capacity section. All four return `int` (whole kg), unlike every other derived attribute (`double`) — this matches the design document's own `int` arithmetic and reflects that a carry-capacity ceiling doesn't need fractional-kg precision.

```
MaxCapacityKg  = floor(LiftStrength^2 / kMaxCapacityDivisor) + LiftStrength   (LiftStrength truncated to int; kMaxCapacityDivisor = 150)
LightLoadKg    = floor(MaxCapacityKg / kLightLoadDivisor)             (kLightLoadDivisor = 3 — exactly one third)
HeavyLoadKg    = floor(MaxCapacityKg * kHeavyLoadMultiplier / kHeavyLoadDivisor)  (2/3 — the practical carry ceiling)
DragCapacityKg = kDragCapacityMultiplier * MaxCapacityKg + floor(DisplayMassKg * kDragCapacityMassFraction)
```

**`MaxCapacityKg` reads `LiftStrength` directly — no offset.** rpg-11 originally introduced `kLoadCapacityStrengthOffset` (subtracting 25 from `Strength` before the load formula) to keep load numbers calibrated after `baseline` was raised from 35 to 60. rpg-12 replaced that entirely: the divisor itself was recalibrated (25 → 150) so the formula produces the same result working directly off a baseline-60 attribute, with no offset needed. Delta V4 swapped the input from the old global `Strength` to the new `LiftStrength` — same divisor, same shape, since `LiftStrength` equals 60 at human defaults just like the old `Strength` did. **Do not reintroduce an offset alongside this divisor** — the two corrections solve the same problem and combining them would double-correct.

At human defaults: `LiftStrength = 60`, `MaxCapacityKg = floor(3600/150) + 60 = 84`, `LightLoadKg = 28`, `HeavyLoadKg = 56`, `DragCapacityKg = 2×84 + floor(71×0.5) = 203` — unchanged numerically from the pre-Delta-V4 values.

`MaxCapacityKg` inherits `LiftStrength`'s floor transitively (`LiftStrength` can never go below `attributeFloor`, 5): it can never go below `floor(5²/150) + 5 = 5`.

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

## Strength deprecation, Thalamus split, 3 new attributes, attribute breakdowns (Delta V4, 2026-07-03)

The old global `Strength` (and its Load Capacity group) is **deleted outright**, per explicit user/design-document instruction — not deprecated-but-kept, not aliased. `NeuralSystem.hippocampus` was split: `hippocampus` now feeds **only** memory (`MemoryPool`, `ShortMemory`); a new `thalamus` field (1-9, neutral 5) takes over every formula that used to read `hippocampus` for perception/sensory purposes (`Sight`, `Hearing`, `Smell`, `Balance`, `Aim`). `DigestiveSystem.nutrientAbsorption` was renamed to `digestiveAbsorption` (pure rename, no weight changes to its existing terms).

### Mean Strength — hidden base engine

```
meanStrength() = 60 + kMeanStrengthMuscleMass×(MuscleMass-5) + kMeanStrengthNeuromuscular×(NeuromuscularEfficiency-5) + kMeanStrengthFiberType×(FiberType-5)
```

Private, never exposed via any getter or DTO — the design document explicitly forbids rendering it in the UI, and nothing outside `PlayableCharacter` needs it. It plays the role of `baseline` for the four specialized strengths below (their own dynamic anchor, the same way `Speed` anchors `Evasion`/`MovementSpeed`).

### 4 specialized strengths + 2 derived combat attributes

```
PushStrength ("Upper Strike") = meanStrength() + 2×(LimbRatio-3) + 1×(MuscleDistribution-5) + 1×(TendonsAndLigaments-5) + 0.5×(Height-7)
LegDrive                      = meanStrength() + 2×(LimbRatio-3) + 1×(5-MuscleDistribution) + 1×(TendonsAndLigaments-5) + 0.5×(Height-7)
GripStrength                  = meanStrength() + 1×(MuscleDistribution-5) + 2×(TendonsAndLigaments-5)
LiftStrength ("Pull Strength") = meanStrength() - 2×(LimbRatio-3) + 1×(TendonsAndLigaments-5)

SwingPower           = floor((PushStrength + GripStrength) / 2)
GrapplingSelfLifting = floor((GripStrength + LiftStrength) / 2)
```

All six floored (continuing the old `Strength`'s floor convention). `LegDrive`'s `MuscleDistribution` term is inverted relative to `PushStrength` (leg-bias helps `LegDrive`, hurts `PushStrength`, and vice versa for arm-bias) — same asymmetric-but-related pattern as the pre-existing `Strength`/`MovementSpeed` `MuscleDistribution` terms. `SwingPower`/`GrapplingSelfLifting` are plain averages of two already-resolved attributes, not additive-standard formulas in their own right — they get no `AttributeBreakdown` (see below).

### 3 new resistance/threshold attributes

```
AngerResistance = 60 - 10×(AmygdalaAndCingulum-5)
FearResistance  = 60 - 10×(AmygdalaAndCingulum-5)   (identical formula, kept as two methods/coefficients per the design doc, in case they diverge later)
PainThreshold   = 60 + 3×(BodyFat-3) + 3×(SkinThickness-3) - 4×(AmygdalaAndCingulum-5)
```

`PainThreshold`'s `BodyFat` term: the design document literally wrote `3×(BodyFat-5)`, but confirmed with the user that `BodyFat`'s neutral is 3 everywhere else in this codebase (`Durability`, `ThermalResistance`) — implemented as `-3` for consistency, not the doc's literal `-5`.

### Balance and Aim rebuilt

```
Balance = 60 + 4×(Thalamus-5) + 1×(NeuralDrive-5) + 0.2×(LegDrive-60)
Aim     = 60 + 5×(Precision-5) + 3×(Thalamus-5)
```

`Balance`: `Hippocampus` and `TendonsAndLigaments` are gone (tendons already factor into `LegDrive`, which Balance now reads as a term); `NeuralDrive` was explicitly kept (an earlier draft proposed swapping it for `Agility` — the user rejected that and asked for `NeuralDrive` kept, plus the new `LegDrive` term, instead). `Aim`: `Precision`'s weight rose 3→5; the old `Hippocampus` term (weight 1) became a `Thalamus` term (weight 3); an `EyesSensitivity` term proposed in an earlier draft was explicitly dropped by the user, so `Aim` has no sensory-organ input.

### Sight/Hearing/Smell and Memory reweighted

`Sight`/`Hearing`/`Smell` keep their exact rpg-14 shape and weights — only the `Hippocampus` term became a `Thalamus` term (same weight, 1). `MemoryPool`/`ShortMemory` (still `Hippocampus`-driven, untouched by the Thalamus split) were reweighted: `MemoryPool` = `60 + 6×(CerebralCapacity-5) + 4×(Hippocampus-5)` (was 8/2); `ShortMemory` = `60 + 3×(CerebralCapacity-5) + 3×(SynapsisQuality-5) + 4×(Hippocampus-5)` (was 4/4/2).

### Digestive Absorption rename + new penalty term

`nutrientAbsorption` → `digestiveAbsorption` everywhere (domain field, DTOs, every formula that already read it — `StaminaPool`, `StarvationResistance`, `FatGainRate`, `MuscleGainRate` — pure rename, same weights). New term on `FoodPoisoningAlcoholResistance`: `- 1×(DigestiveAbsorption-5)` — easier nutrient absorption carries a light extra exposure to food-borne/alcohol effects (weight confirmed with the user as "light", -1).

### Attribute breakdowns — API-side, not client-side

Every additive-standard getter (all of them except `SwingPower`/`GrapplingSelfLifting` and the Load Capacity group, which aren't additive-standard formulas) has a companion `getXxxBreakdown()` returning a new `AttributeBreakdown(double baseline, List<Double> terms)` record — the exact resolved values (multiplications/divisions already collapsed) that sum to the getter's own return value. `AttributesResponse.from(character)` stays a flat, unchanged-shape DTO; a new sibling `AttributeBreakdownsResponse` (keyed the same as `AttributesResponse`'s fields) rides alongside it in both `CharacterResponse`/`BodyResponse` and `BiomechanicsPreviewResponse`.

This exists to back the frontend's new tooltip format ("60 + 4 + 0 + 0 + 0 = 64") **without duplicating any formula logic in TypeScript** — the frontend has never independently computed attributes (it only displays what `/biomechanics/preview` returns), and reimplementing ~40 formulas client-side would create a second, driftable source of truth every future formula change would have to keep in sync by hand. The backend computing and serving the breakdown keeps `PlayableCharacter` the single source of truth for both the number and its resolved explanation.

For the four specialized strengths, the breakdown's `baseline` is the **dynamic `meanStrength()` value**, not the literal `60` — matching the design document's own framing of Mean Strength as "the base engine" for these four (at neutral inputs `meanStrength()` happens to equal 60, matching the doc's own worked example, but it moves with `MuscleMass`/`NeuromuscularEfficiency`/`FiberType` just like any other baseline in this design). `FatGainRate`/`MuscleGainRate` breakdowns use `baseline = 0`, matching their zero-baseline nature.

## Ketosis/Hormonal renames and arcane organs (Delta V4 continued, 2026-07-04)

Two pure renames, no weight/formula changes: `DigestiveSystem.ketosisQuality` → `ketosisEfficiency`; `HormonalSystem` (the class and the `BodySystems.hormonalSystem` field) → `HormonalGlandularSystem`/`hormonalGlandularSystem`. Every historical section above that still says "Ketosis Quality"/"HormonalSystem" is describing the model as it existed at the time that section was written — same convention already used for the `nutrientAbsorption`→`digestiveAbsorption` rename earlier in this same delta.

### Arcane organs — magical races only

Three new inputs, each absent (`0`) on the human default template and populated only for magical races (no magic-race character-creation flow exists yet — the frontend keeps these sliders permanently disabled/greyed for the current human-only template, passing `0`):

- **`NeuralSystem.noeticPlexus`** (0 default; magical-race range undesigned/deferred) — "a network of arcane nerves capable of perceiving and sensing magical signals."
- **`CardiacSystem.astralVentriculum`** (0 default) — "a fifth muscular chamber capable of pumping magical energy through the body."
- **`HormonalGlandularSystem.subtleEpiphysealGland`** (0 default) — "a gland that contains and concentrates magical energy."

Unlike every other input in this codebase, these three read a **neutral point of 6, not 5** — chosen so that the absent value (`0`) lands exactly `6 × weight` below baseline, per the user's explicit worked example (`60 - 48 = 12`). Each formula reads **only its own single input** — no cross-terms, unlike almost every other formula in this file:

```
ManaPool     = 60 + 8×(SubtleEpiphysealGland-6)
ArcaneOutput = 60 + 8×(AstralVentriculum-6)
SixthSense   = 60 + 8×(NoeticPlexus-6)
```

At the human-default absent value (`0`): each resolves to `60 + 8×(0-6) = 60 - 48 = 12` — confirmed against the user's exact stated expectation. No floor was added (a human character will always read exactly 12 on all three; a magical race's reachable range is undesigned pending that race's own character-creation UI, so there's nothing yet to compute a worst case against).

## Mind pillar — cross-pillar terms, Concern mirrors, 9 new attributes, and a rename (rpg-18, 2026-07-04)

The `Mind` pillar (`Values` + `Erudition`) is introduced — full domain-model rationale, `InputNature`, and the `Trait` input type are documented in `.claude/skills/mind-pillar-traits-and-values.md`; this section covers only the formula changes.

**`SixthSense` renamed to `Mediunity`** (pure rename — field, formula, `kSixthSenseNoeticPlexus` → `kMediunityNoeticPlexus`, DTO field, no weight change): `Mediunity = 60 + 8×(NoeticPlexus-6)`, same as before under the old name.

**Four existing formulas gained a `Values`-driven term** (each `Values` field's own neutral is 1, its own default — see the Mind-pillar skill, not the usual 5):
```
ShortMemory   += 3×(Knowledge-1)
Reasoning     += 3×(Truth-1)
Enfactuation  += 3×(Loyalty-1)
Will          += 3×(Morality-1)   (Will no longer delegates to MentalHealthPool — see the Mind-pillar skill)
```

**14 Concern attributes**, one per `Values` field, each a direct mirror (see the exceptions list above): `SelfConcern` (Ego), `FriendshipConcern` (Loyalty), `OrderConcern` (Organization), `FreedomConcern` (Freedom), `PatriotismConcern` (Society), `SpiritualConcern` (Divinity), `PhilosophyConcern` (Truth), `AcademicConcern` (Knowledge), `EnvironmentalismConcern` (Nature), `MoralityConcern` (Morality), `TraditionalismConcern` (Tradition), `JusticeConcern` (Justice), `ProgressConcern` (Progress), `PeaceConcern` (Peace).

**9 new attributes**, baseline 60 unless noted, driven by Erudition `Trait`s (as a 0/1 input, neutral 0 — see the Mind-pillar skill), `Values`, or existing `BodyStructure.shapeAesthetics`:
```
SurvivalSkills            = 60 + 2×hasEcology
AnimalCaring              = 60 + 2×hasEcology + 2×hasBiology
Manipulation              = 60                                    (no modifier yet)
BehaviorReading           = 60                                    (no modifier yet)
Discretion                = 60 - 10×|ShapeAesthetics-5|            (inverted-V, same |deviation| shape as Command, sign flipped — only a neutral ShapeAesthetics is discreet)
Bluffing                  = 60 - 3×(Truth-1) - 3×(Morality-1)
Faith                     = 60 + 3×(Divinity-1)
IllusionResistanceSanity  = 60 + 3×(Truth-1)
Creativity                = 60 + 3×(Progress-1)
```
`Discretion`'s weight (10) was not specified by the ticket — picked to match `Command`'s existing `kCommandShapeAesthetics` magnitude on the same input/deviation, per this file's own "default coefficients are not balanced game data" convention. Tune through play like every other coefficient in this class.

## rpg-19 — cross-pillar terms reverted, Knowledge levels replace boolean traits, 28 Values-trait bonus terms, 4 new attributes

Full domain-model rationale for `Knowledge`/`Personality`/`Labours`/the rewritten `Trait` catalog lives in `.claude/skills/mind-pillar-traits-and-values.md` — this section covers only the formula-level changes. **Verify against `PlayableCharacter.java` itself before trusting any older section of this file (including the rpg-18 section above) about which cross-pillar terms currently exist** — this delta reverted several of them, and a stale changelog read is exactly how that mistake was nearly repeated while implementing rpg-19.

### 8 cross-pillar `Values` terms reverted outright

`ShortMemory`, `Reasoning`, `Enfactuation`, `Will`, `Bluffing` (both its `Truth` and `Morality` terms), `Faith`, `IllusionResistanceSanity`, and `Creativity` all lost their rpg-18 `Values`-reading term, per explicit user instruction. `ShortMemory`/`Reasoning`/`Enfactuation`/`Will` return to their pre-rpg-18 shape (see the rpg-13/Delta-V4 sections above). `Bluffing`/`Faith`/`IllusionResistanceSanity`/`Creativity` (all rpg-18-only attributes) temporarily became flat `baseline`-only formulas — most immediately gained a `Trait`-driven replacement term in the same delta (see below), so "flat 60" was never actually shipped as their final rpg-19 state, just an intermediate one during the revert.

### Ecology/Biology terms: flag → per-level multiplier

`SurvivalSkills`/`AnimalCaring`'s `Trait.ECOLOGY`/`Trait.BIOLOGY` terms became `Knowledge.ECOLOGY`/`Knowledge.BIOLOGY` level reads — same `BodyCoefficients` field and magnitude, now multiplied by the 0-4 level instead of gated by a 0/1 flag:
```
SurvivalSkills += kSurvivalSkillsEcology × EcologyLevel                              (was: × hasEcology)
AnimalCaring   += kAnimalCaringEcology × EcologyLevel + kAnimalCaringBiology × BiologyLevel
```

### IllusionResistanceSanity renamed IllusionResistance

Pure rename (ticket-requested), landing in the same delta as its formula rewrite (see below).

### Values-trait bonus terms (the new 28-trait catalog)

Every *passive, unconditional* bonus a Values-trait grants is a real additive term, added directly to the affected attribute(s) — never inferred, always exactly the flat number the ticket specified per trait. *Situational* effects (conditional on a specific opponent type or narrative action) are not formulas at all — see the Personality skill's "Effect split" section. The full per-trait bonus list:

```
FearResistance  += kFearResistanceSelfSacrifice×hasSelfSacrifice + kFearResistanceSuicidal×hasSuicidal   (4, 4)
PainThreshold   += kPainThresholdSelfSacrifice×hasSelfSacrifice                                          (8)
Discretion      += kDiscretionLoneWolf×hasLoneWolf + kDiscretionBackstabber×hasBackstabber                (8, 8)
Command         += kCommandDominant×hasDominant + kCommandPossessive×hasPossessive                        (4, 4)
Manipulation    += kManipulationDominant×hasDominant + kManipulationPossessive×hasPossessive + kManipulationRelativist×hasRelativist  (4, 4, 4)
SurvivalSkills  += kSurvivalSkillsExpatriated×hasExpatriated + kSurvivalSkillsAnarchist×hasAnarchist       (10, 10)
Mediunity       -= kMediunityPagan×hasPagan                                                                (5)
Faith           = 60 - kFaithPagan×hasPagan + kFaithRelativist×hasRelativist - kFaithProfane×hasProfane    (10, 4, 5)
Intimidation    += kIntimidationProfane×hasProfane + kIntimidationBellicose×hasBellicose                   (4, 6)
Will            = mentalHealthCoreTerms() + kWillRelativist×hasRelativist + kWillPracticalist×hasPracticalist - kWillNihilist×hasNihilist   (4, 4, 10)
Enfactuation    += kEnfactuationRelativist×hasRelativist - kEnfactuationBellicose×hasBellicose             (4, 4)
Reasoning       -= kReasoningRelativist×hasRelativist + kReasoningIliterate×hasIliterate                   (5, 5)
IllusionResistance = 60 - kIllusionResistanceRelativist×hasRelativist + kIllusionResistancePracticalist×hasPracticalist  (5, 5 — cancel out if both selected)
AngerResistance += kAngerResistancePracticalist×hasPracticalist - kAngerResistanceBellicose×hasBellicose   (4, 3)
MentalHealthPool += kMentalHealthPracticalist×hasPracticalist - kMentalHealthNihilist×hasNihilist          (4, 15)
MemoryPool      += kMemoryPoolIliterate×hasIliterate + kMemoryPoolPastEraser×hasPastEraser                 (20, 5)
AnimalCaring    -= kAnimalCaringAntiNaturalist×hasAntiNaturalist                                            (5)
PoisonResistance += kPoisonResistanceAntiNaturalist×hasAntiNaturalist                                       (2)
FoodPoisoningAlcoholResistance += kFoodPoisoningAntiNaturalist×hasAntiNaturalist                            (2)
DiseaseResistance += kDiseaseResistanceAntiNaturalist×hasAntiNaturalist                                     (6)
Creativity      = 60 + kCreativityOrphanMind×hasOrphanMind + kCreativityPastEraser×hasPastEraser            (5, 5)
BehaviorReading += kBehaviorReadingDogEatDog×hasDogEatDog                                                   (5)
MeleeAccuracy   += kMeleeAccuracyDogEatDog×hasDogEatDog                                                     (5)
Aim             += kAimDogEatDog×hasDogEatDog                                                               (5)
ArcaneOutput    += kArcaneOutputConservative×hasConservative                                                (5)
ManaPool        += kManaPoolConservative×hasConservative                                                    (5)
```

**`Will`/`MentalHealthPool` and Nihilist — why the shared core was factored out.** Nihilist penalizes `MentalHealthPool` by 15 but `Will` by only 10 — two different magnitudes on the same trait. Since `getWillBreakdown()` used to copy `getMentalHealthPoolBreakdown()`'s terms wholesale (see the rpg-18 section above), adding Nihilist/Practicalist directly to `MentalHealthPool`'s term list would have made `Will` inherit the *same* magnitude via the copy — wrong for Nihilist specifically. `mentalHealthCoreTerms()` (a new private helper) now returns only the three original physiological terms (Amygdala/Tmod/Pmod); both `getMentalHealthPoolBreakdown()` and `getWillBreakdown()` start from that shared core and then each add their *own* independent Values-trait terms with their own magnitudes. Follow this pattern — do not add a new shared trait term back into `MentalHealthPool`'s own list expecting `Will` to inherit it correctly, unless the two really do share the exact same magnitude.

### 4 new attributes

```
Analysis        = 60 + floor(kAnalysisReasoning × (Reasoning-60)) + kAnalysisDogEatDog×hasDogEatDog   (kAnalysisReasoning = 0.5, kAnalysisDogEatDog = 5)
CloseCombat     = 60 + kCloseCombatBellicose×hasBellicose        (4)
LowRangeCombat  = 60 + kLowRangeCombatBellicose×hasBellicose     (4)
LongRangeCombat = 60                                              (no modifier yet)
```

`Analysis` is the second formula in the codebase (after `Balance`'s `LegDrive` term) to read another *derived* attribute (`Reasoning`) as an additive term rather than a raw input — the `floor()` wrapper matches the ticket's literal `floor(0.5 × (Reasoning-60))` specification.

## GeneralPersonality (Vanity/Focus), Phaxic Cerebelum, 5 new attributes, 12 new traits (2026-07-07)

A fifth `Mind` data group, `GeneralPersonality` (`vanity`, `focus`, both 1-9 neutral 5, `EVENTFUL`), joins `Values`/`Erudition`/`Personality`/`Labours`. `NeuralSystem` gains a 13th field, `phaxicCerebelum` — absent (0) on the human template, same shape as the existing arcane organs (`noeticPlexus`, `astralVentriculum`, `subtleEpiphysealGland`): neutral point 6, not 5.

### 5 new attributes

```
PsyquismOutput  (Supernatural) = 60 + 8×(PhaxicCerebelum-6) + 1×(CerebralCapacity-5)
PsyquismDefense (Supernatural) = 60 + 8×(PhaxicCerebelum-6)
CharmResistance (Social)       = 60 - 3×(Vanity-5) + floor(0.5×(Discretion-60)) - 3×hasProtagonist
Concentration   (Cognitive)    = 60 + 4×(Focus-5) - 1×(CerebralCapacity-5)
Purity          (Supernatural) = 60 + 6×hasCleanVessel
```

At the human-default absent value (`PhaxicCerebelum=0`), `PsyquismOutput`/`PsyquismDefense` both resolve to `60-48=12`, matching the arcane-organ precedent. `CharmResistance` reads `getDiscretion()` — an already-resolved derived attribute — as an ordinary term (third formula to do this, after `Balance`'s `LegDrive` term and `Analysis`'s `Reasoning` term), floored the same way `Analysis` floors its own fractional-weight term.

**The ticket's formula text used a literal `-` before the Phaxic Cerebelum term** (`60 - (8×(PhaxicCerebelum-6))`) but its own worked arithmetic (`60 - 48 + 0 = 12`) only holds if the term is actually added, not subtracted (literal subtraction at the human default would give `60-(-48)=108`, not 12) — implemented to match the arithmetic the ticket itself demonstrated, i.e. the same `+8×(input-6)` shape as every other arcane-organ formula, not the literal `-` glyph. Treat this as resolved by the ticket's own worked example, not a guess.

### Vanity modifiers on existing formulas

```
Enfactuation  += 2×(Vanity-5)
Intimidation  -= 2×(Vanity-5)
```

### 12 new Values-trait bonus terms (added to existing formulas)

These accompany 12 new standalone `Trait` constants — see `.claude/skills/mind-pillar-traits-and-values.md` for the domain-model side (a new "concern-threshold" prerequisite kind, distinct from the base/advanced pairs).

```
Enfactuation           += 6×hasReliable + 6×hasPeacekeeper
Intimidation           -= 3×hasPeacekeeper
Faith                  += 6×hasReligionPractitioner
IllusionResistance     += 6×hasRealitic
Bluffing               -= 3×hasRealitic
Reasoning              += 6×hasPhilosopher
SurvivalSkills         += 6×hasOutdoorLifestyle
AnimalCaring           += 6×hasOutdoorLifestyle
Creativity             += 6×hasInventor
```

`Egotist`, `Loyalist`, and `Retribution Seeker` grant **no formula term at all** — their effects (double stress relief, "Motivated" status, resisted-test bonuses against criminals) are entirely situational/narrative, same "no mechanic exists yet" rule as every other situational trait effect in this codebase. `Protagonist`'s `-3 CharmResistance` is folded into `CharmResistance`'s own formula above rather than listed here.

## Rename: MaxMovementSpeed → MovementSpeed (2026-07-07)

Pure rename, no formula/weight change — `getMaxMovementSpeed()`/`getMaxMovementSpeedBreakdown()` → `getMovementSpeed()`/`getMovementSpeedBreakdown()`, `AttributesResponse.maxMovementSpeed` → `movementSpeed`, and the three coefficients `kMaxMovementSpeedLimbRatio`/`kMaxMovementSpeedMuscleDistribution`/`kMaxMovementSpeedHeight` → `kMovementSpeedLimbRatio`/`kMovementSpeedMuscleDistribution`/`kMovementSpeedHeight`. Every mention throughout this file (including inside the dated `rpg-11`/Delta V4 sections above, which still describe currently-true formula shapes in present tense, not frozen historical snapshots) was updated to the new name in the same delta.

## Testing scope for formulas (reduced 2026-07-08)

Earlier sections of this file describe a heavier testing convention from rpg-11 through rpg-14 — every new attribute got its own worst-case-combination test, several got `*_atExtremes_staysWithinTwentyToOneHundred`-style tests, and every floor decision was backed by a dedicated test proving the exact worst case. **That level of coverage is no longer required going forward.** Those historical sections are kept as-is (they accurately describe what those deltas actually shipped and why), not as a template to keep repeating.

For any new formula or `BodyCoefficients` change, write:

- **One test at every input's neutral/default value**, confirming the formula resolves to `baseline` (or the documented exception's own baseline/anchor) — this is the test that actually catches a wrong weight, sign, or wiring mistake.
- **One direction-sanity test per term** (or one combined test covering all terms at once), confirming the attribute moves the correct way when its input moves off neutral — mainly guards against a flipped sign or a term wired to the wrong field.

Do **not** default to also writing:

- A worst-case/best-case combination test, unless a floor is actually being introduced (i.e., the worst case genuinely needs to be computed to decide whether `attributeFloor` applies — see the paragraph above). If a floor isn't needed, don't compute or test the worst case just to confirm that.
- An exhaustive sweep across every input's full range, or an "extremes" test asserting the result stays inside some bound, unless the ticket specifically calls out a boundary behavior that needs locking down (e.g. `ThermalResistance`'s documented human-UI ceiling).
- A separate test per coefficient value — the neutral-value and direction-sanity tests above already exercise every coefficient that's wired correctly.

This applies to `PlayableCharacter` formulas and `BodyCoefficients` specifically. Domain-rule tests (prerequisites, point budgets, validation) and use-case/application-layer tests are unaffected by this section — keep testing those the same way (happy path + the specific rule being enforced), see `mind-pillar-traits-and-values.md`'s own note on this.

## Astral Atrium/Chi Pool, Pool Attributes, Training and Conditioning, 4 new attributes, Weapon Proficiencies (2026-07-07, rpg-20)

### Astral Atrium — a second, distinct arcane heart organ

`CardiacSystem` gains a fourth field, `astralAtrium` ("a supernatural organ capable of pumping organic energy into the body") — absent (0) on the human template, same disabled-slider treatment as `astralVentriculum`/`noeticPlexus`/`subtleEpiphysealGland`. It anchors a new arcane-organ attribute, `ChiPool`, following the exact same neutral-6/weight-8 shape as the other three:

```
ChiPool = 60 + 8×(AstralAtrium-6)
```

At the human-default absent value (0), `ChiPool` resolves to `60-48=12`, matching every other arcane-organ attribute.

**Unlike the other three arcane organs, `AstralAtrium` also contributes to `StaminaPool` — as its own raw value, not a neutral-6 deviation:**

```
StaminaPool += 4 × AstralAtrium
```

This is a **third documented input-contribution shape**, alongside the standard neutral-5 deviation and the neutral-6 arcane-organ deviation: a **raw-value, zero-at-default** term. The ticket's own formula text (`+ (4 × (Astral Atrium))`, no neutral subtraction) is deliberate, not an oversight — `AstralAtrium` defaults to 0 for ordinary humans (the organ doesn't exist in the standard race), so the term must contribute exactly 0 at that default. Subtracting a neutral-6 here (matching `ChiPool`'s own shape) would instead contribute `-24` to `StaminaPool` for every ordinary human, which is wrong — humans without the organ should see no `StaminaPool` change at all. Apply this same raw-value shape to any future input that (a) defaults to 0 because the underlying trait/organ is absent in the standard race or template, and (b) must contribute nothing at that absent default.

### Training and Conditioning — Vigor/Reflexes (raw-value shape again)

A third `PhysicalTraits` sub-group, `TrainingAndConditioning`, joins `SensorialOrgans`/`BodyStructure`: `vigor`/`reflexes`, both 0-8, **default 0** (unlike almost every other Body trait, which defaults to its scale's midpoint) — a fresh character starts with no training investment here. Both use the same raw-value shape as `AstralAtrium` above, for the same reason (zero investment must contribute exactly zero):

```
StaminaPool    += 5 × Vigor
ReactionSpeed  += 5 × Reflexes    (see below)
```

### Pool Attributes — total/current, a new domain concept

Five existing attributes — `StaminaPool`, `MentalHealthPool`, `MemoryPool`, `ManaPool`, `ChiPool` — become **Pool Attributes**: each now carries a `total` (the attribute's own unchanged additive-standard formula result) and a `current` (the remaining amount), tracked as two genuinely separate numbers rather than one computed value. `current` always equals `total` today — no spend/damage/rest mechanic exists yet to deplete or restore a pool — but the two are kept distinct in both the domain (`PoolAttribute` record, `PoolAttribute.atFull(total)`) and the REST contract specifically so a future mechanic can make them diverge later without another breaking contract change. Same "document intent, not yet implemented" precedent as `BodyComponent`'s reversible-damage regeneration.

**These five attributes moved OUT of the flat `attributes`/`AttributesResponse` map into a new sibling `poolAttributes`/`PoolAttributesResponse` field**, each entry shaped `{total, current}`. Their `getXxxBreakdown()` methods and `AttributeBreakdownsResponse` entries are unchanged — the breakdown still describes the `total` computation only, since `current` has no formula of its own yet.

### Reaction Speed, Hiding, Sneaking — 3 new attributes

```
ReactionSpeed = 60 + 6×(NeuralDrive-5) + 5×Reflexes                    (Reflexes: raw-value term, see above)
Hiding        = 60 - 1×|ShapeAesthetics-5|                              (inverted-V around neutral, same shape as Discretion/Command)
Sneaking      = 60 + 1×(Agility-5)
```

`Sneaking`'s ticket formula was given as `60 + (Agility - 60)` — internally inconsistent, since `Agility` is a raw 1-9 input (subtracting 60 would always yield a large negative, e.g. -54 at neutral). Confirmed with the user via `AskUserQuestion`: `Sneaking` reads `Agility` using **its own scale's neutral (5)**, the same deviation shape as every other raw 1-9 input in this codebase — not a typo'd reference to some derived "Speed"-like attribute. `Hiding`'s weight (1) and `Sneaking`'s weight (1) were not otherwise specified by the ticket beyond the corrected formula shape.

### Weapon Proficiencies — a leveled Mind group with no point budget

`Mind` gains a sixth data group, `WeaponProficiencies` — the sole content of a new "Physical Techniques" Mind tab, "Weapon Proficiencies" group. A new `Weapon` enum (13 constants: `DAGGERS`, `SHORT_SWORDS`, `LONG_SWORDS`, `RAPIERS`, `SABERS`, `SHORT_AXES_HAMMERS`, `LONG_AXES_HAMMERS`, `SPEARS`, `POLE_WEAPONS`, `STAFFS`, `BOWS`, `ONE_HANDED_TRIGGER_WEAPONS`, `TWO_HANDED_TRIGGER_WEAPONS`), always `InputNature.TRAINED`, each independently a 0-3 slider (labels Unknown/Trained/Specialist/Master) with **no shared point budget** — unlike `Knowledge`/`Job`, `WeaponProficiencies.canSetLevel` only bounds-checks against `Weapon.MIN_LEVEL`/`MAX_LEVEL`, never against a spent-points cap. This is a new sub-pattern for leveled Mind groups: structurally close to `Erudition`/`Labours` (an `EnumMap`-backed leveled map, every entry defaulting to 0), but budget-free. Carries no formula effect of its own yet.

`PreviewAttributesUseCase.calculate(...)` grew from 8 to 9 parameters (`WeaponProficiencies` added).

## Labeled breakdown terms, Durability split, Training and Conditioning expansion, Skills (rpg-21, 2026-07-08)

### `AttributeBreakdown.Term` — every term now carries a label

`AttributeBreakdown(double baseline, List<Double> terms)` became `AttributeBreakdown(double baseline, List<Term> terms)` with a nested `Term(String label, double value)` record. Every `getXxxBreakdown()` method across the whole file was updated to wrap each term in a `new AttributeBreakdown.Term("Label", value)`. `AttributeBreakdownResponse`/`TermResponse` mirror this on the DTO side. This backs a new frontend tooltip format — description, then `Affected By:` / `Base Value: X` / `<Label>: Y` per term — replacing the old unlabeled "60 + 4 + 0 + 0 = 64" sum-line format. Labels use the same Title Case wording as the formula's own javadoc term names (e.g. "Limb Ratio", "Tendons and Ligaments").

### Durability replaced outright by Soft Tissue Durability + Bone Durability

The old unified `Durability` (and `kDurabilityXxx` coefficients) is **deleted**, not aliased — same "replace, don't keep both" convention as every prior breaking rename in this file. Two new attributes:

```
SoftTissueDurability = 10 + kSoftTissueDurabilityMesomorphy×(Mesomorphy-5) + kSoftTissueDurabilityBodyFat×(BodyFat-3)
                           - kSoftTissueDurabilityFlexibility×(Flexibility-5) + kSoftTissueDurabilitySkin×(SkinThickness-3)
                           + kSoftTissueDurabilityResilience×Resilience
BoneDurability        = baseline + kBoneDurabilityBoneDensity×(BoneDensity-5)
```

`SoftTissueDurability`'s baseline is **10, not the shared 60** — the only attribute besides the zero-baseline rate attributes to deviate from `BodyCoefficients.getBaseline()`, per explicit user spec (`BodyCoefficients.softTissueDurabilityBaseline`). It is also **floored** — the worst-case combination (Mesomorphy=1, BodyFat=1, Flexibility=9, SkinThickness=1, Resilience=0) computes to -2, below `attributeFloor` (5) — the first floor introduced outside the pre-existing eight (see "Safety floors" above). `BoneDurability` needs no floor (same magnitude as the old formula's bone term, which never needed one).

### Training and Conditioning — 6 new raw-value inputs

`Intensity`, `Coordination`, `Resilience`, `Fighting`, `WeaponPracticing`, `Shooting` join `Vigor`/`Reflexes` in `TrainingAndConditioning` — same shape (0-8, default 0, raw-value contribution, zero at the training-absent default):

```
MeanStrength   += kMeanStrengthIntensity × Intensity        (feeds all 4 specialized strengths via the hidden engine)
Speed          += kSpeedIntensity × Intensity
Acrobatics     += kAcrobaticsCoordination × Coordination
Evasion        += kEvasionCoordination × Coordination
Balance        += kBalanceCoordination × Coordination
SoftTissueDurability += kSoftTissueDurabilityResilience × Resilience
PainThreshold  += kPainThresholdResilience × Resilience
CloseCombat    += kCloseCombatFighting × Fighting
LowRangeCombat += kLowRangeCombatWeaponPracticing × WeaponPracticing
LongRangeCombat += kLongRangeCombatShooting × Shooting
Aim            += kAimShooting × Shooting
```

### Athletism and Martial Arts — Dancing/Fencing join Archery, Archery wired for the first time

`Knowledge.ATHLETISM_AND_MARTIAL_ARTS` gains two new constants, `DANCING` and `FENCING` (level 0-4, `TRAINED`), alongside the pre-existing `ARCHERY` — which had no formula effect before this delta. All three use the standard `weight × level` shape (neutral 0), matching the Ecology/Biology precedent:

```
Acrobatics      += kAcrobaticsDancing × DancingLevel
Evasion         += kEvasionDancing × DancingLevel
Balance         += kBalanceDancing × DancingLevel
LowRangeCombat  += kLowRangeCombatFencing × FencingLevel
LongRangeCombat += kLongRangeCombatArchery × ArcheryLevel
Aim             += kAimArchery × ArcheryLevel
```

### Rename: `ALCHEMY_CHEMISTRY` → `CHEMISTRY`

Pure rename, no group or behavior change — done to remove the ambiguity once a dedicated `Alchemy` attribute existed.

### Material Durability catalog — reference data, not yet consumed

Two new domain types, `DamageType` (9 constants: Chop, Slice, Blunt, Piercing, Burning, Frost, Corrosive, Tear, Compress) and `Material` (21 constants, each with a base durability and a per-`DamageType` multiplier, translated to English identifiers from the design document's own Portuguese names — e.g. `MUSCLE_TISSUE`, `GLASS_OBSIDIAN`, `KERATIN_CARAPACE`, `ADAMANTINE`). **Not wired into any use case, DTO, or REST endpoint** — reserved as static reference data for a future strikes-against-objects mechanic, same "document intent, not yet implemented" precedent as `BodyComponent`'s reversible-damage regeneration.

### 6 new Skills attributes

All baseline 60, all driven by `Knowledge` levels (weight × level, neutral 0) plus a couple of existing raw/deviation inputs:

```
Alchemy               = 60 + 8×Chemistry + 8×Wizardry
MachineHandling       = 60 + 8×Engineering
Performance           = 60 + kPerformanceCoordination×Coordination + 8×Dancing + 2×(ShapeAesthetics-5) + 8×Art
SciencePractice       = 60 + 8×Biology + 8×Chemistry
Healing               = 60 + 8×Medicine + 4×Biology
HackingAndPrograming  = 60 + 8×ComputerScience
```

`Performance`'s `Coordination` term reads the new `TrainingAndConditioning.coordination` raw value directly (`kPerformanceCoordination = 1`), the third input in this delta (after `Intensity`/`Resilience`) to use the raw-value shape inside a brand-new formula rather than an existing one.

## Localization — Term labels need an EN/PT pair (rpg-23, i18n, 2026-07-12)

`AttributeBreakdown.Term.label()` is the only backend-sourced text in this project, and it is now localized: `/api/v1/characters/{id}` and `/api/v1/character/preview` both accept a `language` query parameter (`EN` default, or `PT`), parsed via `LanguageRequestParser` into the new `Language` enum (`domain.model`, mirrors `keynor-core`'s identical type). Translation happens entirely at the response layer — `AttributeBreakdownResponse.from(breakdown, language)` looks up each term's English label in `TermLabelTranslations` (`application.dto`, package-private) and substitutes the Portuguese equivalent when `language == PT`; `PlayableCharacter.java`'s formula code is untouched and still only ever writes the English label literal.

**Mandatory sync rule:** every new `AttributeBreakdown.Term("Some Label", ...)` introduced in `PlayableCharacter.java` must get a matching `Map.entry("Some Label", "Tradução")` added to `TermLabelTranslations.EN_TO_PT` in the same delta. A label missing from that map silently falls back to the English string for `PT` requests too — this fails safe (no exception, no broken response) but produces a mixed-language tooltip, so treat a missing entry as a real bug to fix, not a passable default. `getPushStrengthBreakdown()`-style javadoc comments describing formulas in prose stay English-only (internal documentation, not shipped to any client).

## Rename: ReactionSpeed → CognitiveSpeed (2026-07-14)

Pure rename, no formula/weight change — `getReactionSpeed()`/`getReactionSpeedBreakdown()` → `getCognitiveSpeed()`/`getCognitiveSpeedBreakdown()`, `AttributesResponse.reactionSpeed`/`AttributeBreakdownsResponse`'s `reactionSpeed` key → `cognitiveSpeed`, and `kReactionSpeedNeuralDrive`/`kReactionSpeedReflexes` → `kCognitiveSpeedNeuralDrive`/`kCognitiveSpeedReflexes`. Driven by the new combat-timing UT system (`game-rules.md`'s Time tracking section) needing a clearer name for the attribute that governs both initiative order and several action-speed formulas. The rpg-20 section above still says "Reaction Speed" — describing the model as it existed at the time, same convention already applied to the `nutrientAbsorption`→`digestiveAbsorption` and `HormonalSystem`→`HormonalGlandularSystem` renames.

## Removal: LongRangeCombat (2026-07-18)

`getLongRangeCombat()`/`getLongRangeCombatBreakdown()`, `kLongRangeCombatShooting`, `kLongRangeCombatArchery`, and the `longRangeCombat` field on `AttributesResponse`/`AttributeBreakdownsResponse`/`CombatAttributeInputs`/`CombatActionTimeRequest` are all deleted outright — user-requested removal, not a rename. The four `CombatActionType` formulas that used to weight it (`DRAW_RANGED_WEAPON`, `RELOAD_PISTOL`, `RELOAD_LONG_GUN`, `AIM`) now score on Speed alone at weight 1.0 (not the old partial Speed weight left standing) — see `game-rules.md`'s Time tracking table. `Archery`/`Shooting` (the two inputs `LongRangeCombat` used to read) are untouched and still feed `Aim` via `kAimArchery`/`kAimShooting`, so neither input became dead. Sections above describing rpg-19/rpg-21 formulas that included `LongRangeCombat` are left as-is — accurate history of what those deltas shipped, per this file's own convention (see the rpg-20 "Reaction Speed" note just above).

## Physical Integrity and Valor (2026-07-18)

Two new values, added alongside the `LongRangeCombat` removal. Full narrative/design rationale lives in `game-rules.md` (new "Physical Integrity" subsection under Damage, new "Valor" subsection under Combat) — this section covers only the formula-level detail.

**`PhysicalIntegrity` — not an additive-standard formula, no breakdown** (same "no breakdown" precedent as `SwingPower`/`GrapplingSelfLifting`/Load Capacity — it aggregates the wound tree, not Body/Mind inputs):

```
componentSeverity   = (ReversibleDamage/MaxHitPoints) × kIntegrityReversibleSeverity
                     + (IrreversibleDamage/MaxHitPoints) × kIntegrityIrreversibleSeverity
componentLossPercent = min(100, componentSeverity / kIntegrityIrreversibleSeverity × 100)
componentWeight      = CascadeRelation == PROTECTED_INTERNAL
                          ? (vital ? kIntegrityWeightVitalInternal : kIntegrityWeightInternal)
                          : (CascadeRelation == ATTACHED_APPENDAGE ? kIntegrityWeightAppendage
                                                                    : kIntegrityWeightStructural)
PhysicalIntegrity    = 100 − Σ(componentLossPercent × componentWeight) / Σ(componentWeight)
```

...except that any `vital` component with `IrreversibleDamage >= MaxHitPoints` (fully destroyed) forces `PhysicalIntegrity = 0` outright, bypassing the weighted average — a single destroyed Heart/Brain/etc. kills regardless of the rest of the body's condition. Defaults: `kIntegrityReversibleSeverity = 1`, `kIntegrityIrreversibleSeverity = 8`, `kIntegrityWeightVitalInternal = 5`, `kIntegrityWeightInternal = 3`, `kIntegrityWeightStructural = 2`, `kIntegrityWeightAppendage = 1` — first-pass, not balanced game data, tune through play like every other coefficient in this class. The importance tiers are derived from each `BodyComponent`'s existing `CascadeRelation`/`vital` fields rather than 45 hand-authored per-node weights, a deliberate simplification.

**`Valor` — additive-standard baseline, Combat/Competition group, new sixth Pool Attribute:**

```
Valor(total)   = 60 + kValorBellicose×hasBellicose + kValorTestosterone×Tmod
Valor(current) = Valor(total) − (100 − PhysicalIntegrity)
```

`kValorBellicose = 8`, `kValorTestosterone = 4` (first-pass, tune through play). `current` is the **first Pool Attribute whose value doesn't always equal `total`** (see `PoolAttribute`'s own javadoc — every other pool's `current` still mirrors `total` today, pending its own future spend/damage/rest mechanic). `current` is allowed to go negative; `PlayableCharacter.hasFallen()` (`current <= 0`) is a new plain boolean, not part of the REST contract.

**Explicitly not implemented — see game-rules.md's linked TODOs:** bleeding/poison/disease/starvation/dehydration as a 5-UT periodic Integrity drain (resisted-but-never-fully-stopped by Bleeding/Disease/Poison Resistance etc.), damage-type-triggered bleeding status (Slice/Pierce/Chop), and every non-Integrity Valor drain (Fear, Demoralization, Confusion, pain beyond Pain Threshold). All of these need the still-open damage-vs-resistance formula (`game-rules.md`'s "Damage vs. resistance" `*TODO*`, 2026-07-08 decision, "do not invent") as a prerequisite — do not build them ahead of that formula landing.

## Extending this pattern

When adding a new derived attribute: pick its neutral-anchored inputs, decide their weights, add one `BodyCoefficients` field per weight (named `k<Formula><Term>`), write the formula as `baseline + Σ weight × (input - neutral)` with each term wrapped in a labeled `AttributeBreakdown.Term`, add the label's Portuguese translation to `TermLabelTranslations` in the same delta (see "Localization" above), and only add a floor if the actual worst-case combination (compute it — don't guess) lands at or below `attributeFloor`. See "Testing scope for formulas" above for what to actually write tests for.
