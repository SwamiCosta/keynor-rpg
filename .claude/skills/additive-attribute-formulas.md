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
- **`Evasion`** and **`MaxMovementSpeed`** are anchored on `Speed`, not on `baseline` directly — they add their own deviation terms on top of whatever `Speed` already computed, rather than starting fresh from 60.
- **`FatGainRate`** and **`MuscleGainRate`** (rpg-14) do not add `baseline` at all — they are zero-baseline **rate** attributes (positive = gaining faster, negative = losing/gaining slower, zero = stable at every input's neutral value), not absolute stat values. See the rpg-14 section below.
- **`PushStrength`/`LegDrive`/`GripStrength`/`LiftStrength`** (Delta V4) are anchored on the hidden `meanStrength()` engine, not on `baseline` directly — same pattern as Evasion/MaxMovementSpeed anchoring on Speed, just with a different anchor attribute. See the Delta V4 section below.
- **`Balance`** (rebuilt Delta V4) uses another *already-resolved* attribute, `LegDrive`, as an additive **term** (`kBalanceLegDrive × (LegDrive - 60)`) rather than as a base — the first formula in the codebase to do this. Unlike Evasion/MaxMovementSpeed/the four strengths (which replace `baseline` with another attribute's *value*), Balance keeps its own `baseline` (60) and treats `LegDrive`'s deviation from 60 as just another weighted term alongside `Thalamus` and `NeuralDrive`.
- **The 14 Concern attributes** (rpg-18, Mind pillar) are direct mirrors of their matching `Values` field — `baseline = 0`, single term = the raw value, not a deviation from a neutral point. No `BodyCoefficients` field backs the mirror itself (there is nothing to tune in a strict 1:1 mirror) — see `.claude/skills/mind-pillar-traits-and-values.md`.

## Two mass numbers, not one

- **`SymbolicTotalMass`** (int) = `kSymbolicMassBase + Height + MuscleMass + BodyFat + (BoneDensity - 5)`. Abstract, game-balance-only — feeds the mass penalty inside `Speed` and `FatigueResistance`. At every input's neutral value it equals 25, which is why `kSpeedMassNeutral` and `kFatigueResistanceMassNeutral` are both 25 (not a coincidence with the attribute baseline of 60 — two independent constants that happen to share a value at neutral).
- **`DisplayMassKg`** (double) = `MuscleKg + FatKg + FrameKg + BoneModKg`. Real-world kg shown to the player. Computed on the backend (not the frontend) specifically so `DragCapacityKg` — which mixes `DisplayMassKg` with `Strength` — has a single source of truth. The frontend never recomputes either mass number; it only formats individual slider values (e.g. showing "175 cm" next to the height slider) from data it already has locally, which is a linear label conversion, not a duplicated gameplay formula.

## Safety floors

Eight attributes apply `Math.max(BodyCoefficients.getAttributeFloor(), raw)` (floor defaults to 5): the four specialized strengths (**PushStrength**, **LegDrive**, **GripStrength**, **LiftStrength** — Delta V4, replacing the old floored **Strength**), **SwingPower**/**GrapplingSelfLifting** (Delta V4, floored on their own averaged result), **FatigueResistance**, **Evasion**, **MaxMovementSpeed**. These were identified by computing the actual worst-case slider combination (see `PlayableCharacterTest`'s floor tests) — every other derived attribute has a natural worst-case comfortably above the floor and does not need one.

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

Private, never exposed via any getter or DTO — the design document explicitly forbids rendering it in the UI, and nothing outside `PlayableCharacter` needs it. It plays the role of `baseline` for the four specialized strengths below (their own dynamic anchor, the same way `Speed` anchors `Evasion`/`MaxMovementSpeed`).

### 4 specialized strengths + 2 derived combat attributes

```
PushStrength ("Upper Strike") = meanStrength() + 2×(LimbRatio-3) + 1×(MuscleDistribution-5) + 1×(TendonsAndLigaments-5) + 0.5×(Height-7)
LegDrive                      = meanStrength() + 2×(LimbRatio-3) + 1×(5-MuscleDistribution) + 1×(TendonsAndLigaments-5) + 0.5×(Height-7)
GripStrength                  = meanStrength() + 1×(MuscleDistribution-5) + 2×(TendonsAndLigaments-5)
LiftStrength ("Pull Strength") = meanStrength() - 2×(LimbRatio-3) + 1×(TendonsAndLigaments-5)

SwingPower           = floor((PushStrength + GripStrength) / 2)
GrapplingSelfLifting = floor((GripStrength + LiftStrength) / 2)
```

All six floored (continuing the old `Strength`'s floor convention). `LegDrive`'s `MuscleDistribution` term is inverted relative to `PushStrength` (leg-bias helps `LegDrive`, hurts `PushStrength`, and vice versa for arm-bias) — same asymmetric-but-related pattern as the pre-existing `Strength`/`MaxMovementSpeed` `MuscleDistribution` terms. `SwingPower`/`GrapplingSelfLifting` are plain averages of two already-resolved attributes, not additive-standard formulas in their own right — they get no `AttributeBreakdown` (see below).

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

## Extending this pattern

When adding a new derived attribute: pick its neutral-anchored inputs, decide their weights, add one `BodyCoefficients` field per weight (named `k<Formula><Term>`), write the formula as `baseline + Σ weight × (input - neutral)`, and only add a floor if the actual worst-case combination (compute it — don't guess) lands at or below `attributeFloor`.
