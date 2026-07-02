# keynor-rpg — Agent Context

> Project-level context for AI agents operating in keynor-rpg.
> Always read `ARCHITECTURE.md` at the workspace root before reading this file.

---

## What this project is

`keynor-rpg` is the backend of the RPG system built on the Keynor universe. It owns game mechanics — playable characters, attributes, skills, combat, sessions, and campaigns — as a domain distinct from the universe lore served by `keynor-core`. It is currently in the scaffolding phase: no use cases or domain entities have been implemented yet.

This project is paired with `keynor-rpg-client` (frontend). Both are architected by the same agent, Gaemes — see Agent structure below.

---

## Responsibilities

- Game mechanics: playable characters, attributes, skills, combat resolution
- Sessions and campaigns
- Consumes `keynor-core` entities (characters, places, items) as lore foundation via REST — never queries `keynor-core`'s database directly

---

## Stack (confirmed 2026-06-25 — scaffolded in PR #3)

| Concern | Technology |
|---------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 — **pending reconciliation against `keynor-core`'s actual version** (Gaemes has no read access to `keynor-core`; picked a current stable default, escalate to Omnia to confirm/align) |
| Database | PostgreSQL — **own dedicated Docker container, separate from keynor-core's** (game data is never stored in the lore database) — see `docker-compose.yml` |
| Build tool | Maven |
| Testing | JUnit 5 + Mockito + Testcontainers |

`pom.xml` was created 2026-06-25 after explicit user authorization — see PR #3. Any future dependency change still requires its own authorization.

---

## Local environment assumptions

`keynor-rpg` expects two things already running before any agent is invoked: its own PostgreSQL instance and the Spring Boot application itself. Agents never start, stop, or restart either, and never provision a disposable substitute (e.g. a one-off container) for a missing one.

**The database has exactly one sanctioned way to run: `docker compose up` against this project's own `docker-compose.yml`.** That file defines a single `postgres` service — its own container (`keynor-rpg-postgres`), its own named volume, its own database (`keynor_rpg`), published on host port `5433` (not `5432`, which is `keynor-core`'s) so the two can run side by side without conflict. There is no other supported way to provide this database — never a bare local Postgres install, and never `keynor-core`'s container or any other already-running Postgres instance, even if reachable on the default port. If `keynor-core`'s Postgres is the only thing listening on 5432, that is not this project's database and must not be treated as a substitute.

Starting, stopping, or restarting this container — `docker compose up`/`down` or any equivalent — is the user's action alone. Agents only consume the database once it is already running; they never run Docker commands against it themselves, even to self-test a change.

Any task that needs real lore data (characters, places, items) additionally depends on `keynor-core`'s REST API being reachable — that service belongs to `keynor-core`'s own agents, never started or restarted from here.

If something required is not running or not reachable, stop and report instead of working around it. See workspace `SKILLS.md` — Skill 13 for the general rule this follows.

---

## Architecture

Hexagonal architecture (ports & adapters), mirroring the pattern established in `keynor-core`. The domain layer has zero framework dependencies.

```
keynor-rpg/
├── src/
│   ├── main/
│   │   └── java/com/keynor/rpg/
│   │       ├── domain/                  ← pure domain (entities, value objects, exceptions)
│   │       │   ├── model/
│   │       │   ├── port/
│   │       │   │   ├── in/              ← input ports (use case interfaces)
│   │       │   │   └── out/             ← output ports (repository interfaces)
│   │       │   └── service/             ← domain services (implement input ports)
│   │       ├── application/             ← application layer (orchestration, DTOs)
│   │       │   ├── dto/
│   │       │   └── usecase/
│   │       └── infrastructure/          ← adapters (Spring, JPA, REST clients, etc.)
│   │           ├── web/                 ← REST controllers (input adapters)
│   │           ├── persistence/         ← JPA repositories (output adapters)
│   │           └── config/              ← Spring wiring (DomainConfiguration, etc.)
│   └── test/
│       └── java/com/keynor/rpg/
│           ├── domain/                  ← unit tests for domain services
│           └── infrastructure/          ← integration tests for adapters
├── pom.xml                              ← Spring Boot 3.5.0 / Java 21 (created 2026-06-25, PR #3)
└── docker-compose.yml                   ← own dedicated PostgreSQL container, host port 5433 — see Local environment assumptions above
```

### Layer rules

| Layer | Depends on | Never depends on |
|-------|-----------|-----------------|
| `domain` | nothing | Spring, JPA, any framework |
| `application` | `domain` | infrastructure adapters |
| `infrastructure` | `application`, `domain` | — |

---

## Boundary with keynor-core

- `keynor-rpg` has its own database — it never reads from or writes to `keynor-core`'s PostgreSQL instance
- Lore data (characters, places, items) is fetched exclusively through `keynor-core`'s REST API
- Any new endpoint or contract change needed on the `keynor-core` side is out of Gaemes' scope — coordinate with Omnia, who routes it to Imaws

---

## Domain model

`PlayableCharacter` is composed of independent pillars — `Corpo` (Body), `Mente` (Mind), `Alma` (Soul) — each with its own business rules. Composition inside the aggregate is used instead of a shared `Pilar` interface, since the pillars' rules differ fundamentally and a unifying abstraction would not be reusable.

### Body pillar (implemented — proposed for Void, see PR for `task/body-domain-model`)

- `BodyComponent` is a single recursive tree node (not one Java class per anatomical part), so race-specific anatomy (wings, tails, horns) can be expressed as data rather than new code.
- `Body` wires up the human template as 10 flat root components (`Skull`, `Neck`, `RightFlank`, `LeftFlank`, `Torso`, `RightHip`, `LeftHip`, `Genitals`, `Buttocks`, `LowerBody`) — each region is its own root, not a child of an implicit single "Corpo" node. `RightHip`/`LeftHip` are standalone leaf roots (same profile as `Buttocks`) and do not restructure `LowerBody`, which still holds both legs unsplit.
- `Skull` additionally carries `Mandible` (structural, alongside `Brain` and the senses). `Neck` carries `CervicalSpine` and `Esophagus` (both `PROTECTED_INTERNAL`+vital — previously `Neck` had no children). `Torso` carries two new spine segments, `ThoracicSpine` and `LumbarSpine` (both `PROTECTED_INTERNAL`+vital), plus a new `SolarComplex` node sitting between `Chest` and `Abdomen` that now owns `Liver` — `Liver` was moved out of `Chest` (which previously grouped it with `Heart`/`Lungs`, anatomically inconsistent) and is otherwise unchanged (`PROTECTED_INTERNAL`+vital). `Torso`'s children are now, in order: `Chest`, `ThoracicSpine`, `SolarComplex`, `Abdomen`, `LumbarSpine`.
- `CascadeRelation` models how damage propagates across a parent-child edge: `NONE` (plain nesting, no automatic cascade), `PROTECTED_INTERNAL` (resistance-overflow onto an internal organ, weighted-random pick among siblings by `maxHitPoints` when untargeted; precision attacks resolve directly against the named organ), `ATTACHED_APPENDAGE` (small per-hit chance for damage to "slip" into an appendage — eyes, ears, nose, hands, feet — dealing a fraction of the incoming damage).
- Wound model is two numbers per component: `currentHitPoints`/`maxHitPoints` plus a separate `irreversibleDamage` counter; `getReversibleDamage()` is derived. No discrete per-wound timers. Reversible damage auto-regenerates over time (not yet implemented); irreversible damage only heals via magic/supernatural means (not yet implemented).
- Vital components (`vital = true`) cause death at extreme irreversible damage and can also trigger side effects before that point; fainting removes a character from combat earlier than death. Blood loss, dehydration, and starvation as additional death vectors are documented intent only — no implementation yet.
- `BodyEffect` is an empty marker interface; `VisionImpairment`, `MovementImpairment`, `AttackImpairment` are empty placeholder implementations — each side effect gets real behavior later, case by case, in its own named domain method (never a hidden effect of damage application).
- `BodyCascadeResolver` (domain service) takes the cascade decisions (resisted damage, weighted organ pick, appendage slip chance/fraction) via an injected `RandomSource` output port, keeping `java.util.Random` out of the domain. `JdkRandomSource` is the infrastructure adapter; `DomainConfiguration` wires the service as a Spring bean.
- `BodyComponent.applyDamage` and every `BodyCascadeResolver` decision point log at DEBUG via SLF4J (`org.slf4j:slf4j-api`, already transitive through the Spring Boot starters — no new dependency added) for combat debugging; this is a logging facade, not a framework, so it does not violate the domain's zero-framework-dependency rule.
- `PlayableCharacter` holds an optional `loreReference` (nullable, format intentionally undecided) as the only link toward a `keynor-core` lore `Character` — no dependency in either direction, exact identifier scheme pending coordination with Omnia/Imaws.
- Player-facing output is always a qualitative label (e.g. "Resistente"), never the raw numeric stats above — this constrains the future DTO/API layer once it exists.
- `Mente` (Mind) and `Alma` (Soul) pillars are not yet designed.

### Body data groups and output formulas (implemented — rpg-8/rpg-9, 2026-06-30)

- **`Biomechanics`** is now a **pure data holder** (`Genetics` + `BodyComposition`) living inside `Body` alongside two sibling groups. All derived-attribute formulas moved to `PlayableCharacter` (the aggregate root), which synthesizes inputs from all three groups. `Biomechanics.defaults()` is called by `Body.previewTemplate()` and `Body.humanTemplate()`.
- **Multi-group structure (rpg-8):** `Body` holds three data groups alongside the anatomical wound tree, coefficients, and point budgets: `Biomechanics` (`Genetics` + `BodyComposition`), `BodySystems` (`BloodSystem` + `CardiacSystem` + `PulmonarySystem` + `NervousSystem`), and `SpatialIntelligence` (`perception`, `agility`, `precision`). Two-layer philosophy still applies *within* `Biomechanics`: **genetic layer** (`Genetics`) is fixed at creation; **trainable layer** (`BodyComposition`) changes through training. `BloodSystem` is genetic (immutable, no setters) but lives in `BodySystems` since it models a physiological system, not a body-composition trait. The genetic layer's role in modulating training *rate* is documented intent only, not yet implemented.
- **`Genetics`** is immutable (final fields, no setters). Holds `endomorphy`, `mesomorphy`, `ectomorphy` (three independent 0–10 axes, Heath-Carter inspired — not mutually exclusive categories), `height` (cm), `limbRatio` (~0.85–1.15), `boneDensity` (0–10). Defaults: all somatotype/density axes at 5, height 170cm, limbRatio 1.0, boneDensity 5.
- **`muscleDistribution` (moved to `BodyComposition` in rpg-8, 2026-06-30 — was in `Genetics` from rpg-1..rpg-6):** now **trainable** — the distribution of muscle mass between upper and lower body changes through targeted upper- vs. lower-body training. Leg-biased (low) to arm-biased (high) axis around the balanced midpoint (5). `PlayableCharacter.muscleDistributionDeviation()` (private) computes the −5..+5 deviation that both effects scale from. Trade-off: arm-bias gives a *small* `getStrength()` bonus (leg-bias a small penalty); leg-bias gives a *larger* `getMaxMovementSpeed()` bonus (arm-bias a larger penalty) — opposite directions, deliberately asymmetric magnitudes (see `BodyCoefficients` exceptions). `getSpeed()` is the generic movement-capable speed (attacks etc.) and is **independent of `LimbRatio`** — `limbRatio` affects only `getMaxMovementSpeed()` via a stride modifier. `getMaxMovementSpeed()` is displacement/travel speed specifically.
- **`BodyComposition`** is mutable (plain setters, since it trains over time). Holds `bodyFat` (kg — a value the player generally wants to *lower*; changed from a 0–1 percentage to kg on 2026-06-28), `muscleMass` (kg), `dominantFiberType` (-1 slow/endurance to +1 fast/power), `muscleDistribution` (0–10 — see above), `flexibility` (0–10, muscle elasticity — higher values increase evasion/acrobatics, lower values increase durability via the `kFlexibilityDurability` deviation term). Defaults: 14kg body fat, 30kg muscle mass, fiber type 0, muscleDistribution 5 (balanced), flexibility 5 (balanced). **`totalMass` is not stored here** — see derived mass formulas below.
- **Derived mass — `getBoneMass()`, `getOrganWaterMass()`, `getTotalMass()` on `PlayableCharacter` (added 2026-06-28; moved from `Biomechanics` in rpg-8):**
  ```
  BoneMass       = kBoneMass * (height/100)^2 * (1 + kBoneDensity * (boneDensity - 5))
  OrganWaterMass = kOrganWaterMass * (height/100)^2
  TotalMass      = bodyFat + muscleMass + BoneMass + OrganWaterMass
  ```
  `boneDensity` is a bounded deviation from the mid-range default (5) — `boneDensity = 0` does not collapse bone mass to zero. `OrganWaterMass` covers organs, blood, skin, and water (~25% of real body weight) not captured by the other components; scales only with height. `kBoneMass = 2.7`, `kBoneDensity = 0.06`, `kOrganWaterMass = 6.3` are the only `BodyCoefficients` that don't default to `1.0` due to mass calibration (boneMass ≈ 7.80kg, organWaterMass ≈ 18.21kg, totalMass ≈ 70.01kg at `Genetics.defaults()`).
- **`BodySystems`** (rpg-8, 2026-06-30): groups the four physiological systems inside `Body` alongside `Biomechanics`. `getCardiovascularCapacity()` on `PlayableCharacter` is the live average of the three cardiovascular inputs.
  - **`BloodSystem`** (`oxygenCarryingCapacity`, 0–10) — genetic, immutable (no setters), like `Genetics`.
  - **`CardiacSystem`** (`cardiacOutput`, 0–10) and **`PulmonarySystem`** (`pulmonaryCapacity`, 0–10) — both trainable (mutable setters).
  - **`NervousSystem`** (`neuralDrive` 0–10, `neuromuscularEfficiency` 0–1, both trainable) — `neuromuscularEfficiency` moved here from `BodyComposition` in rpg-8 (it is neural, not compositional). `neuralDrive` feeds `getSight()`, `getHearing()`, `getSmell()`, and `getEvasion()` directly; it will additionally modulate `BodyComposition.dominantFiberType` once that rate formula is implemented. All four systems default to 5 (neuromuscularEfficiency defaults to 0.5).
- **`SpatialIntelligence`** (rpg-9, 2026-06-30): third sibling group inside `Body`, holds three trainable 0–10 axes (default 5 each): `perception` (ability to detect external stimuli), `agility` (efficiency of body movement in any direction), `precision` (ability to make objects move as intended). All three feed the seven new derived attributes on `PlayableCharacter`.
- **`AttributePointBudget`** is a generic spend/remaining tracker. `Body` holds one for the genetic pool and one for the training pool, both seeded at 20 points. Illustrative placeholders — the per-attribute point cost is deferred to the character-creation use case, which does not exist yet.
- **Output formulas are plain instance methods on `PlayableCharacter`** (moved from `Biomechanics` in rpg-8, 2026-06-30), using private accessors (`genetics()`, `composition()`, `bodySystems()`, `nervousSystem()`, `spatialIntelligence()`, `coeff()`) to reduce deep-chain noise. `intensity` parameters are method arguments (not stored state).
  - **Biomechanics-derived:** `getCardiovascularCapacity()`, `getStrength()`, `getSpeed()`, `getMaxMovementSpeed()`, `getStaminaPool()`, `getFatigueRate(intensity)`, `getEnergyCost(intensity)`, `getDurability()`. `getStrength()` follows the ²⁄₃-power law with leverage (LimbRatio) and muscle-distribution modifiers. `getSpeed()` is **independent of Strength**: `k2 × MuscleMass^(2/3) × (1 + 0.4 × FiberType) × NeuromuscularEfficiency / TotalMass` — pure power-to-weight with no LimbRatio dependency (redesigned in rpg-10, 2026-07-01). `getMaxMovementSpeed()` extends Speed with a LimbRatio stride modifier `(1 + kLimbRatioSpeed × (LimbRatio − 1))` and the muscle-distribution modifier — LimbRatio affects only this method, not Speed (rpg-10). `getEnergyCost(intensity)` concretizes the doc's named-but-unspecified `ActivityCost` and `Eficiencia` terms as `kActivityCost × M × intensity` and `kEfficiency × CV × (1 − 0.3 × FT)`. `getDurability()` adds a `- kFlexibilityDurability × (flexibility - 5)` deviation term (backwards-compatible at default flexibility = 5).
  - **SpatialIntelligence-derived (rpg-9):** `getSight()`, `getHearing()`, `getSmell()` (all identical: `kSense × (perception + neuralDrive) / 2` — can diverge through training in the future); `getEvasion()` (`kEvasion × agility × speed × (1 + kEvasionNeural × neuralDrive) × (1 + kEvasionFlex × flexibility)`); `getAcrobatics()` (`kAcrobatics × (agility + flexibility) / 2`); `getMeleeAccuracy()` (`kMelee × (precision + agility) / 2`); `getAim()` (`kAim × (precision + perception) / 2`).
- **`BodyCoefficients`** (renamed from `BiomechanicsBalance` in rpg-8, 2026-06-30): holds every free coefficient as a mutable, independently settable field. **Fully rewritten in rpg-11 (2026-07-01)** — see the rpg-11 subsection below. Everything in this bullet describes the pre-rpg-11 multiplicative model and is kept only as historical record of how the previous formulas were tuned; it no longer matches the current field set.

### Additive attribute standard (rpg-11, 2026-07-01) — supersedes the formulas above

Every derived attribute in `PlayableCharacter` was rewritten from the multiplicative/power-law model described above to a single shared shape: `attribute = baseline + Σ weight × (input - neutral)`, with `baseline = 60`. This was a full, deliberate replacement (including of the same-day rpg-10 Speed redesign) driven by a product goal: make attribute numbers assertive and standardized across the whole game rather than each formula having its own scale and shape. Full formula list, the SymbolicTotalMass/DisplayMassKg mass split, the four floored attributes (Strength, FatigueResistance, Evasion, MaxMovementSpeed), and the extension pattern for future attributes are documented in `.claude/skills/additive-attribute-formulas.md` — read it before touching any formula or `BodyCoefficients` field, rather than duplicating it here.

**Input scale change:** every `Genetics`/`BodyComposition`/`BloodSystem`/`CardiacSystem`/`PulmonarySystem`/`NervousSystem`/`SpatialIntelligence` field changed from `double` to `int`, on new discrete ranges: most traits are 1-9 (neutral 5); `limbRatio` is 1-5 (neutral 3); `height`/`muscleMass` are 1-15 (neutral 7/5); `bodyFat` is 1-10 (neutral 3, except inside `getDurability()` where its own neutral of 3 is used directly — everywhere else `bodyFat`/`muscleMass`/`height` are direct mass inputs, not deviations). `neuromuscularEfficiency` moved from a 0-1 float to this same 1-9 int scale.

**Removed:** `getCardiovascularCapacity()`, `getFatigueRate(intensity)`, `getEnergyCost(intensity)`, `getTotalMass()`/`getBoneMass()`/`getOrganWaterMass()` (replaced by `getSymbolicTotalMass()`/`getDisplayMassKg()`).

**Added:** `getStaminaRecovery()` (new attribute), `getFatigueResistance()` (replaces `FatigueRate` with inverted semantics — higher is now better), and a Load Capacity group — `getLightLoadKg()`, `getHeavyLoadKg()`, `getMaxCapacityKg()`, `getDragCapacityKg()` — all derived from `Strength` (`DragCapacityKg` additionally mixes in `DisplayMassKg`).

**Load Capacity recalibrated again, rpg-12 (2026-07-02) — supersedes the rpg-11 offset correction:** the `kLoadCapacityStrengthOffset` trick (subtract 25 from `Strength` before the load formula) was replaced outright by a new set of exact-integer formulas from the user: `MaxCapacityKg = floor(Strength² / 150) + Strength` (divisor raised 25→150, calibrated to work directly off the baseline-60 `Strength` — no offset), `LightLoadKg = floor(MaxCapacityKg / 3)` (exactly ⅓, replacing the old 0.3 fraction), `HeavyLoadKg = floor(MaxCapacityKg × 2 / 3)` (exactly ⅔, replacing 0.7), `DragCapacityKg` unchanged in shape. All four Load Capacity methods now return `int`, not `double`, matching the design document's own arithmetic. At human defaults: `MaxCapacityKg = 84`, `LightLoadKg = 28`, `HeavyLoadKg = 56`, `DragCapacityKg = 203`.

### Neural/Hormonal/Digestive expansion, rpg-13 (2026-07-02) — 15 new attributes, group consolidation

`SpatialIntelligence` was removed as a separate `Body` field — its three fields merged into `NeuralSystem` (renamed from `NervousSystem`, per explicit user instruction), which now holds ten 1-9 fields: `neuralDrive`, `neuromuscularEfficiency`, `cerebralCapacity`, `synapsisQuality`, `hippocampus` (renamed from `perception`), `hypothalamus`, `amygdalaAndCingulum`, `immunity`, `agility`, `precision`. Two new trainable systems joined `BodySystems`: `HormonalSystem` (`thyroid`, `adrenalGlands`) and `DigestiveSystem` (`nutrientAbsorption`, `impurityCleaning`, `ketosisQuality`). `BloodSystem` gained `bloodThickness` (1-5, neutral 3) and `Genetics` gained `skinThickness` (1-7, neutral 3, domain accepts the full range for future non-human races even though the FE currently locks it to [2-4]) — both immutable/genetic for now, explicitly flagged by the user as provisional (easy to flip mutable later: drop `final`, add a setter, no other structural change).

15 new derived attributes, all following the same additive shape (no new exceptions, no new floors needed — every worst case checked and stayed positive): **Cognitive** — `MemoryPool`, `Reasoning`, `ShortMemory`, `MentalHealthPool`, `Will` (= `MentalHealthPool` for now, pending the future Mind pillar). **Sensory/Hormonal/Stress** — `Balance`, `StressResistance`. **Biological defense** — `PoisonResistance`, `DiseaseResistance`, `BleedingResistance`. **Metabolic/survival** — `ThermalResistance` (human UI ceiling 83, true domain ceiling 98), `BreathOutput`, `DehydrationResistance`, `StarvationResistance`, `FoodPoisoningAlcoholResistance`. `StaminaPool` and `FatigueResistance` each gained one additional term. Full formula list and per-attribute input mapping: `.claude/skills/additive-attribute-formulas.md`.

### Physical Traits, hormonal modifiers, 6 new attributes — rpg-14 (2026-07-02)

A new top-level `Body` sibling, `PhysicalTraits`, joins `Biomechanics` and `BodySystems`: `SensorialOrgans` (`eyesSensitivity`, `earsSensitivity`, `noseSensitivity`, 1-9 neutral 5, mutable) and `BodyStructure` (`skinThickness` — **moved from `Genetics`, still immutable, unchanged range** — plus new `shapeAesthetics`/`cellularHealth`, 1-9 neutral 5, mutable). `boneDensity` **moved from `Genetics` to `BodyComposition` and became mutable/trainable** (a real semantic change, not just relocation — it now lives in the already-trainable class). `BodyComposition` gained `tendonsAndLigaments` (1-9 neutral 5, mutable). `HormonalSystem` gained `predominantMorphicHormone` (1-9, neutral 5) driving two new private `PlayableCharacter` helpers, `testosteroneModifier()`/`progesteroneModifier()` (`Tmod`/`Pmod`, symmetric 1-4 deviations below/above neutral, zero at neutral).

`Sight`/`Hearing`/`Smell` diverged into three independent formulas (each reads its own `SensorialOrgans` input plus `Hippocampus`/`NeuralDrive`/`Pmod`) — `getHearing()`/`getSmell()` no longer delegate to `getSight()`. `Strength`, `Durability`, and `Balance` each gained one term (`TendonsAndLigaments`, `SkinThickness`, `TendonsAndLigaments` respectively). `MentalHealthPool`/`Will` gained `Tmod`/`Pmod` terms (with `kMentalHealthAmygdala` reweighted 10→5 to compensate). `PoisonResistance`/`DiseaseResistance`/`FoodPoisoningAlcoholResistance` each gained a `CellularHealth` term (widening their true extremes beyond the old exact [20,100], by design — no floor needed).

6 new attributes: **zero-baseline** `FatGainRate`/`MuscleGainRate` (a third documented exception to the additive standard — no `baseline` term, since these express a rate of change, not an absolute stat; the first formulas to read `Genetics.endomorphy`/`ectomorphy`, previously unused by anything) and **baseline-60 social** `Intimidation`/`Diplomacy`/`Enfactuation`/`Command`, all driven by `BodyStructure.shapeAesthetics` and the hormone modifiers (`Enfactuation` is currently formula-identical to `Diplomacy` — kept as specified by the design doc, documented as an expected-to-diverge duplication, not fixed as a bug; `Intimidation` is intentionally unbounded, using raw `SymbolicTotalMass` rather than a small deviation). Full formula list: `.claude/skills/additive-attribute-formulas.md`.

---

## REST API

### Character sheet (implemented — restructured in rpg-8, 2026-06-30; contract changed again in rpg-11/rpg-13/rpg-14)

- `GET /api/v1/characters/{id}` returns a `CharacterResponse` (`id`, `name`, nested `body`) — first REST surface in this project.
- **`body` response structure:** `body.biomechanics` (genetics + bodyComposition), `body.bodySystems` (bloodSystem + cardiacSystem + pulmonarySystem + neuralSystem + hormonalSystem + digestiveSystem), `body.physicalTraits` (sensorialOrgans + bodyStructure, new rpg-14), `body.attributes`, `body.calculatedValues`, `body.loadCapacity`, `body.geneticPoints`, `body.trainingPoints`. **`body.spatialIntelligence` no longer exists (rpg-13)** — its three fields moved into `body.bodySystems.neuralSystem`.
- **`body.attributes`** (rpg-11, expanded rpg-13, expanded rpg-14): 35 fields — the 14 additive-standard attributes from rpg-11, 15 rpg-13 fields, and 6 new rpg-14 fields (`fatGainRate`, `muscleGainRate`, `intimidation`, `diplomacy`, `enfactuation`, `command`). `cardiovascularCapacity`, `fatigueRate`, `energyCost` remain absent (see rpg-11 removal note above).
- **`body.calculatedValues`**: `symbolicTotalMass` (int), `displayMassKg` (double).
- **`body.loadCapacity`**: `lightLoadKg`, `heavyLoadKg`, `maxCapacityKg`, `dragCapacityKg` — all `int` (rpg-12).
- **`bodyComposition`** carries `muscleDistribution`, `flexibility`, and (rpg-14) `boneDensity` + `tendonsAndLigaments`. **`genetics` no longer carries `boneDensity` or `skinThickness` (both moved out in rpg-14)** — `genetics` is now just `endomorphy`/`mesomorphy`/`ectomorphy`/`height`/`limbRatio`.
- **`bloodSystem`** carries `bloodThickness` (rpg-13) alongside `oxygenCarryingCapacity`.
- **`neuralSystem`** (renamed from `nervousSystem`, rpg-13) carries all ten fields — `neuralDrive`, `neuromuscularEfficiency`, `cerebralCapacity`, `synapsisQuality`, `hippocampus`, `hypothalamus`, `amygdalaAndCingulum`, `immunity`, `agility`, `precision`.
- **`hormonalSystem`**: `thyroid`, `adrenalGlands`, and (rpg-14) `predominantMorphicHormone`. **`digestiveSystem`** (rpg-13): `nutrientAbsorption`, `impurityCleaning`, `ketosisQuality`.
- **`physicalTraits`** (new, rpg-14): `sensorialOrgans` (`eyesSensitivity`, `earsSensitivity`, `noseSensitivity`) and `bodyStructure` (`skinThickness` — moved from `genetics`, `shapeAesthetics`, `cellularHealth`).
- No persistence yet: `GetPlayableCharacterService` always returns `PlayableCharacter("Keynor", Body.humanTemplate())`.
- `AttributesResponse.from(PlayableCharacter)` is the single point of delegation for both character sheet and preview response.
- `GetPlayableCharacterUseCase` is wired as a `@Bean` in `DomainConfiguration`.

### Biomechanics preview (implemented — restructured in rpg-8, 2026-06-30; contract changed again in rpg-11/rpg-13/rpg-14)

- `POST /api/v1/biomechanics/preview` takes a `BiomechanicsPreviewRequest` with **4 groups (rpg-14, was 3)**: `genetics`, `bodyComposition`, `bodySystems` (bloodSystem + cardiacSystem + pulmonarySystem + neuralSystem + hormonalSystem + digestiveSystem), `physicalTraits` (sensorialOrgans + bodyStructure, new rpg-14). `spatialIntelligence` no longer exists as a request field — its inputs moved into `bodySystems.neuralSystem`. All leaf fields are `int`.
- Response: `BiomechanicsPreviewResponse` nests `attributes` (35 fields), `calculatedValues` (`symbolicTotalMass`, `displayMassKg`), and `loadCapacity` — same shapes as the character sheet response.
- **Stateless** — no persistence, no character identity. Not the eventual creation contract.
- `PreviewAttributesService` builds a transient `Body.previewTemplate(biomechanics, bodySystems, physicalTraits)` (3-arg since rpg-14) and wraps it in a `PlayableCharacter` — `BodyCoefficients.defaults()` apply since none of the formulas depend on tuned coefficients for preview purposes.
- `PreviewAttributesUseCase` is wired as a `@Bean` in `DomainConfiguration`.

---

## Agent structure

```
keynor-rpg/
└── .claude/
    ├── CLAUDE.md              ← this file
    └── agents/
        ├── gaemes.md          ← Level 3 — architect for keynor-rpg AND keynor-rpg-client
        ├── void.md            ← Level 2 — Java backend developer
        └── jung.md            ← Level 2 — database schema (migrations) and data
```

| Agent | Level | Scope |
|-------|-------|-------|
| Gaemes | 3 — Architect | Both `keynor-rpg` (backend) and `keynor-rpg-client` (frontend) |
| Void | 2 — Developer | Java source: domain, application, infrastructure layers |
| Jung | 2 — Developer | Flyway migrations, seed/maintenance scripts, read-only DB queries — see `jung.md` for the named exception to the workspace's default migration-authorship rule |

No dedicated test agent exists yet for `keynor-rpg` — Gaemes will propose one as implementation work begins, following the pattern established by Judis (`keynor-core`).

---

## Agent operational rules

Before beginning the task itself — reading project source or task-specific documentation, implementing features, creating branches, running commands, or opening PRs — every agent must:

1. Switch to `main`: `git checkout main`
2. Pull the latest changes: `git pull`

This does not apply to the agent's own fixed mandatory reading (`ARCHITECTURE.md`, the root `CLAUDE.md`, `SKILLS.md`, this file, the agent's own `.md` file, and any Always-tier skill file) — reading those is how an agent learns this very rule, not an action on the project's current state. Sync once the agent moves on to the task itself.

A second pull is not required within the same task session. See workspace `SKILLS.md` — Skill 09.

---

*Last updated: 2026-07-02 (rpg-11, 2026-07-01: full replacement of every derived-attribute formula — including the same-day rpg-10 Speed redesign — with the additive standard, baseline 60; all Body input scales moved from double/float to discrete int; CardiovascularCapacity and FatigueRate removed, FatigueResistance and StaminaRecovery added; new Load Capacity group; BodyCoefficients rewritten; same-day fix subtracted kLoadCapacityStrengthOffset (25) from Strength for Load Capacity. rpg-12, 2026-07-02: Load Capacity recalibrated again — kLoadCapacityStrengthOffset removed entirely, kMaxCapacityDivisor raised 25→150 to work directly off baseline-60 Strength, LightLoad/HeavyLoad switched from 0.3/0.7 fractions to exact ⅓/⅔ integer division, all four Load Capacity methods now return int. rpg-13, 2026-07-02: SpatialIntelligence merged into NeuralSystem (renamed from NervousSystem, perception→hippocampus); new HormonalSystem and DigestiveSystem groups; bloodThickness added to BloodSystem, skinThickness added to Genetics (both immutable, explicitly provisional); 15 new derived attributes across 4 modules (Cognitive, Sensory/Hormonal/Stress, Biological defense, Metabolic/survival); StaminaPool and FatigueResistance each gained a term. rpg-14, 2026-07-02: new PhysicalTraits Body sibling (SensorialOrgans + BodyStructure); skinThickness moved Genetics→BodyStructure (unchanged), boneDensity moved Genetics→BodyComposition (became mutable); tendonsAndLigaments added to BodyComposition, predominantMorphicHormone added to HormonalSystem driving new Tmod/Pmod hormone modifiers; Sight/Hearing/Smell diverged into 3 independent formulas; Strength/Durability/Balance/MentalHealthPool/3 biological-defense resistances each gained a term; 6 new attributes (zero-baseline FatGainRate/MuscleGainRate, social Intimidation/Diplomacy/Enfactuation/Command); see `.claude/skills/additive-attribute-formulas.md`)*