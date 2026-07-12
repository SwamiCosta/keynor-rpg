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
- `Alma` (Soul) pillar is not yet designed. `Mente` (Mind) is now implemented — see the Mind pillar section below.

### Mind pillar (implemented — rpg-18, 2026-07-04)

- `Mind` is a sibling of `Body` on `PlayableCharacter` — composition, not a shared `Pilar` interface, same rationale as `Body`/`Mind`/`Soul` generally. Holds `Values` and `Erudition`, plus its own `AttributePointBudget` (`eventPoints`, seeded at 20, per-input costs deferred like `Body`'s genetic/training pools).
- **`InputNature`** (`IMMUTABLE`/`TRAINED`/`EVENTFUL`) is a new enum formalizing every input's acquisition nature — `Values` and every `Trait` are `EVENTFUL`. New inputs going forward (any pillar) should declare their nature in the field's javadoc.
- **`Trait`** is the new boolean/checkbox input type — one enum (17 constants today, all Erudition knowledge, grouped by `TraitGroup`), contributing `weight × (hasTrait ? 1 : 0)` to a formula (neutral 0), no new exception to the additive standard. `Trait.prerequisitesMet(PlayableCharacter)` defaults to always-available; no current trait defines a real prerequisite.
- **`Values`**: 14 fields, 0-5, neutral **1** (not the usual 5 — matches each field's own default). `Ego`, `Loyalty`, `Organization`, `Freedom`, `Society`, `Divinity`, `Truth`, `Knowledge`, `Nature`, `Morality`, `Tradition`, `Justice`, `Progress`, `Peace`. The "≥2 values off-default, unique untied maximum" creation rule is FE-only validation, not a domain invariant — same precedent as the hormone-neutral lock.
- **`Erudition`**: `Set<Trait> selectedTraits`, capped at `FREE_TRAIT_SLOTS` (2) until a real per-trait event-point cost is defined.
- Full design rationale: `.claude/skills/mind-pillar-traits-and-values.md`. Formula-level detail: `.claude/skills/additive-attribute-formulas.md`.

### Body data groups and output formulas (implemented — rpg-8/rpg-9, 2026-06-30)

- **`Biomechanics`** is now a **pure data holder** (`Genetics` + `BodyComposition`) living inside `Body` alongside two sibling groups. All derived-attribute formulas moved to `PlayableCharacter` (the aggregate root), which synthesizes inputs from all three groups. `Biomechanics.defaults()` is called by `Body.previewTemplate()` and `Body.humanTemplate()`.
- **Multi-group structure (rpg-8):** `Body` holds three data groups alongside the anatomical wound tree, coefficients, and point budgets: `Biomechanics` (`Genetics` + `BodyComposition`), `BodySystems` (`BloodSystem` + `CardiacSystem` + `PulmonarySystem` + `NervousSystem`), and `SpatialIntelligence` (`perception`, `agility`, `precision`). Two-layer philosophy still applies *within* `Biomechanics`: **genetic layer** (`Genetics`) is fixed at creation; **trainable layer** (`BodyComposition`) changes through training. `BloodSystem` is genetic (immutable, no setters) but lives in `BodySystems` since it models a physiological system, not a body-composition trait. The genetic layer's role in modulating training *rate* is documented intent only, not yet implemented.
- **`Genetics`** is immutable (final fields, no setters). Holds `endomorphy`, `mesomorphy`, `ectomorphy` (three independent 0–10 axes, Heath-Carter inspired — not mutually exclusive categories), `height` (cm), `limbRatio` (~0.85–1.15), `boneDensity` (0–10). Defaults: all somatotype/density axes at 5, height 170cm, limbRatio 1.0, boneDensity 5.
- **`muscleDistribution` (moved to `BodyComposition` in rpg-8, 2026-06-30 — was in `Genetics` from rpg-1..rpg-6):** now **trainable** — the distribution of muscle mass between upper and lower body changes through targeted upper- vs. lower-body training. Leg-biased (low) to arm-biased (high) axis around the balanced midpoint (5). `PlayableCharacter.muscleDistributionDeviation()` (private) computes the −5..+5 deviation that both effects scale from. Trade-off: arm-bias gives a *small* `getStrength()` bonus (leg-bias a small penalty); leg-bias gives a *larger* `getMovementSpeed()` bonus (arm-bias a larger penalty) — opposite directions, deliberately asymmetric magnitudes (see `BodyCoefficients` exceptions). `getSpeed()` is the generic movement-capable speed (attacks etc.) and is **independent of `LimbRatio`** — `limbRatio` affects only `getMovementSpeed()` via a stride modifier. `getMovementSpeed()` is displacement/travel speed specifically.
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
  - **Biomechanics-derived:** `getCardiovascularCapacity()`, `getStrength()`, `getSpeed()`, `getMovementSpeed()`, `getStaminaPool()`, `getFatigueRate(intensity)`, `getEnergyCost(intensity)`, `getDurability()`. `getStrength()` follows the ²⁄₃-power law with leverage (LimbRatio) and muscle-distribution modifiers. `getSpeed()` is **independent of Strength**: `k2 × MuscleMass^(2/3) × (1 + 0.4 × FiberType) × NeuromuscularEfficiency / TotalMass` — pure power-to-weight with no LimbRatio dependency (redesigned in rpg-10, 2026-07-01). `getMovementSpeed()` extends Speed with a LimbRatio stride modifier `(1 + kLimbRatioSpeed × (LimbRatio − 1))` and the muscle-distribution modifier — LimbRatio affects only this method, not Speed (rpg-10). `getEnergyCost(intensity)` concretizes the doc's named-but-unspecified `ActivityCost` and `Eficiencia` terms as `kActivityCost × M × intensity` and `kEfficiency × CV × (1 − 0.3 × FT)`. `getDurability()` adds a `- kFlexibilityDurability × (flexibility - 5)` deviation term (backwards-compatible at default flexibility = 5).
  - **SpatialIntelligence-derived (rpg-9):** `getSight()`, `getHearing()`, `getSmell()` (all identical: `kSense × (perception + neuralDrive) / 2` — can diverge through training in the future); `getEvasion()` (`kEvasion × agility × speed × (1 + kEvasionNeural × neuralDrive) × (1 + kEvasionFlex × flexibility)`); `getAcrobatics()` (`kAcrobatics × (agility + flexibility) / 2`); `getMeleeAccuracy()` (`kMelee × (precision + agility) / 2`); `getAim()` (`kAim × (precision + perception) / 2`).
- **`BodyCoefficients`** (renamed from `BiomechanicsBalance` in rpg-8, 2026-06-30): holds every free coefficient as a mutable, independently settable field. **Fully rewritten in rpg-11 (2026-07-01)** — see the rpg-11 subsection below. Everything in this bullet describes the pre-rpg-11 multiplicative model and is kept only as historical record of how the previous formulas were tuned; it no longer matches the current field set.

### Additive attribute standard (rpg-11, 2026-07-01) — supersedes the formulas above

Every derived attribute in `PlayableCharacter` was rewritten from the multiplicative/power-law model described above to a single shared shape: `attribute = baseline + Σ weight × (input - neutral)`, with `baseline = 60`. This was a full, deliberate replacement (including of the same-day rpg-10 Speed redesign) driven by a product goal: make attribute numbers assertive and standardized across the whole game rather than each formula having its own scale and shape. Full formula list, the SymbolicTotalMass/DisplayMassKg mass split, the four floored attributes (Strength, FatigueResistance, Evasion, MovementSpeed), and the extension pattern for future attributes are documented in `.claude/skills/additive-attribute-formulas.md` — read it before touching any formula or `BodyCoefficients` field, rather than duplicating it here.

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

### Delta V4 — Strength deprecated, Hippocampus/Thalamus split, 3 new attributes, attribute breakdowns (2026-07-03)

The old global `Strength` (and its Load Capacity group) is **deleted outright**. `NeuralSystem` gains an 11th field, `thalamus` (1-9 neutral 5) — `hippocampus` now feeds only memory (`MemoryPool`/`ShortMemory`); `thalamus` takes over every formula that used `hippocampus` for perception (`Sight`/`Hearing`/`Smell`/`Balance`/`Aim`). `DigestiveSystem.nutrientAbsorption` is renamed `digestiveAbsorption` (pure rename).

A hidden `meanStrength()` engine (never exposed via any DTO — UI/API must never render it) replaces `Strength`'s core terms and anchors 4 new specialized, player-facing strengths: `PushStrength`, `LegDrive`, `GripStrength`, `LiftStrength` (all floored), plus 2 averaged combat attributes `SwingPower`/`GrapplingSelfLifting` (floored, no breakdown — not additive-standard formulas). Load Capacity now derives from `LiftStrength` instead of the old `Strength` — same divisors, numerically unchanged at human defaults.

3 new attributes: `AngerResistance`/`FearResistance` (identical formula, `60 - 10×(AmygdalaAndCingulum-5)`) and `PainThreshold` (`60 + 3×(BodyFat-3) + 3×(SkinThickness-3) - 4×(AmygdalaAndCingulum-5)` — BodyFat's neutral confirmed as 3, not the design doc's literal 5). `Balance` rebuilt (`Thalamus` + `NeuralDrive`, kept per explicit user instruction, + a new `LegDrive` term — the first formula in the codebase using another derived attribute as an additive *term* rather than a base); `Aim` rebuilt (`Precision` reweighted, `Hippocampus`→`Thalamus`, no `EyesSensitivity` term). `MemoryPool`/`ShortMemory` reweighted (still `Hippocampus`-driven). `FoodPoisoningAlcoholResistance` gains a light `- 1×(DigestiveAbsorption-5)` penalty term.

Every additive-standard getter now has a companion `getXxxBreakdown()` returning a new `AttributeBreakdown(baseline, terms)` record — backs the frontend's resolved-calculation tooltip format without duplicating formula logic client-side (the frontend has never independently computed attributes). Served via a new sibling DTO, `AttributeBreakdownsResponse`, alongside the existing flat `attributes` in both response shapes. Full formula list and rationale: `.claude/skills/additive-attribute-formulas.md`.

### Delta V4 continued — renames, arcane organs, 3 new attributes (2026-07-04)

Two pure renames: `DigestiveSystem.ketosisQuality` → `ketosisEfficiency`; `HormonalSystem` (class + `BodySystems.hormonalSystem` field) → `HormonalGlandularSystem`/`hormonalGlandularSystem`. Three new inputs, each absent (`0`) on the human default template and populated only for magical races (no magic-race UI exists yet, so the frontend permanently disables these three sliders for the current human-only flow): `NeuralSystem.noeticPlexus`, `CardiacSystem.astralVentriculum`, `HormonalGlandularSystem.subtleEpiphysealGland`.

3 new attributes, each reading only its own single input around a neutral point of **6** (not the usual 5) with weight 8: `ManaPool = 60 + 8×(SubtleEpiphysealGland-6)`, `ArcaneOutput = 60 + 8×(AstralVentriculum-6)`, `SixthSense = 60 + 8×(NoeticPlexus-6)`. At the human-default absent value (0), all three resolve to exactly `60 - 48 = 12`. Full detail: `.claude/skills/additive-attribute-formulas.md`.

### rpg-19 — Knowledge/Labours sliders, Personality (28 Values traits), 8 reverted cross-pillar terms, 4 new attributes (2026-07-05)

`InputNature.IMMUTABLE` renamed `BIRTH`. Knowledge stopped being a boolean `Trait` and became a leveled slider (new `Knowledge`/`KnowledgeGroup` enums, 0-4, always `TRAINED`) spending from a small shared budget (`Erudition.BASE_POINTS = 2`, adjustable per-character by `Personality`). `Trait`/`TraitGroup` were repurposed entirely for a new 28-constant Values-linked personality catalog (14 base/advanced pairs, one per `Values` concern), stored in a new `Mind` data group, `Personality` — gated by real prerequisites (a concern at its own default, or the pair's base trait already selected) and forcing the linked `Values` field to 0 on selection. A new `Labours`/`Job` pair mirrors `Erudition`/`Knowledge` (7 jobs, same 2-point budget mechanic) with no formula effect of its own. 8 of the 9 rpg-18 cross-pillar `Values` terms were reverted outright (`ShortMemory`, `Reasoning`, `Enfactuation`, `Will`, `Bluffing` ×2, `Faith`, `IllusionResistanceSanity`) — verified against the actual formula code, not the rpg-18 changelog text, after the changelog was found to be missing 5 of the 9 — and mostly replaced by Values-trait bonus terms instead. `IllusionResistanceSanity` renamed `IllusionResistance`; `DisplayMassKg` renamed `TotalMassKg`. 4 new attributes: `Analysis` (reads `Reasoning` as a term, second formula in the codebase to do this after `Balance`), `CloseCombat`, `LowRangeCombat`, `LongRangeCombat`. Full detail: `.claude/skills/mind-pillar-traits-and-values.md` (rewritten) and `.claude/skills/additive-attribute-formulas.md`'s new rpg-19 section.

### GeneralPersonality, Phaxic Cerebelum, 5 new attributes, 12 concern-threshold traits (2026-07-07)

`Mind` gained a fifth data group, `GeneralPersonality` (`vanity`/`focus`, both 1-9 neutral 5, `EVENTFUL`) — unrelated to `Personality` despite the name overlap. `NeuralSystem` gained a 13th field, `phaxicCerebelum`, absent (0) on the human template, following the arcane-organ shape (neutral 6). 5 new attributes: `PsyquismOutput`/`PsyquismDefense` (Supernatural, driven by `PhaxicCerebelum`), `CharmResistance` (Social, reads `Vanity` and `Discretion` as a term), `Concentration` (Cognitive, reads `Focus`/`CerebralCapacity`), `Purity` (Supernatural, driven by a new trait). `Vanity` also gained modifier terms on `Enfactuation`(+)/`Intimidation`(-). 12 new standalone `Trait` constants (`Trait.values()` now 40, was 28) introduce a second prerequisite kind — gated by a concern sitting **at or above** a threshold (2 or 4) rather than the base/advanced pair's exact-default/already-selected checks; 9 of them add a real formula term to an existing attribute, 3 (`Egotist`/`Loyalist`/`Retribution Seeker`) are entirely situational. Full detail: `.claude/skills/additive-attribute-formulas.md` and `.claude/skills/mind-pillar-traits-and-values.md`.

### Astral Atrium, Pool Attributes, Training and Conditioning, 4 new attributes, Weapon Proficiencies — rpg-20 (2026-07-07)

`CardiacSystem` gained a fourth field, `astralAtrium` (a second, distinct arcane heart organ, absent/0 on the human template, same disabled-slider treatment as the other three arcane organs), anchoring a new arcane-organ attribute `ChiPool` (`60 + 8×(AstralAtrium-6)`) and also contributing to `StaminaPool` as its own raw value (`+ 4×AstralAtrium`) — a **third documented input-contribution shape** (raw-value, zero-at-default), distinct from both the standard neutral-5 deviation and the neutral-6 arcane-organ deviation. A new `PhysicalTraits` sub-group, `TrainingAndConditioning` (`vigor`/`reflexes`, 0-8, default 0), uses the same raw-value shape: `Vigor` feeds `StaminaPool` (`+5×Vigor`), `Reflexes` feeds the new `ReactionSpeed` attribute (`+5×Reflexes`).

Five existing attributes — `StaminaPool`, `MentalHealthPool`, `MemoryPool`, `ManaPool`, `ChiPool` — became **Pool Attributes**: a new `PoolAttribute` record (`{total, current}`, `current` always equal to `total` today pending a future spend/damage/rest mechanic) replaces their flat `double` in the REST contract. They moved out of the flat `attributes`/`AttributesResponse` map into a new sibling `poolAttributes`/`PoolAttributesResponse` field on `CharacterResponse`/`CharacterPreviewResponse`; their `AttributeBreakdownsResponse` entries are unchanged.

`Mind` gained a sixth data group, `WeaponProficiencies` — the sole content of a new "Physical Techniques" Mind tab, "Weapon Proficiencies" group. A new `Weapon` enum (13 constants) is always `InputNature.TRAINED`, each independently a 0-3 slider with **no shared point budget** (a new sub-pattern for leveled Mind groups, unlike `Erudition`/`Labours`). `PreviewAttributesUseCase.calculate(...)` grew from 8 to 9 parameters.

4 new attributes: `ReactionSpeed` (`60 + 6×(NeuralDrive-5) + 5×Reflexes`), `Hiding` (`60 - 1×|ShapeAesthetics-5|`), `Sneaking` (`60 + 1×(Agility-5)` — corrected from the ticket's internally-inconsistent `60 + (Agility-60)` after `AskUserQuestion` confirmation), and `ChiPool` (see above). Full detail: `.claude/skills/additive-attribute-formulas.md` and `.claude/skills/mind-pillar-traits-and-values.md`.

### Formulas reference page — cross-repo mandatory sync (rpgc-17, 2026-07-04)

`keynor-rpg-client` maintains an in-app, hand-maintained "Formulas" reference page (`src/lib/formulasReferenceData.ts`) mirroring every formula, coefficient, and input in this file's own additive-standard sections above. **Any change to a `PlayableCharacter` formula, a `BodyCoefficients` field, or an input's range/neutral/default must be reflected there in the same delta of work** — flag it to Gaemes if you don't have write access to `keynor-rpg-client` yourself. See that project's `.claude/skills/formulas-reference-page.md` for the full rule.

---

## REST API

### Character sheet (implemented — restructured in rpg-8, 2026-06-30; contract changed again in rpg-11/rpg-13/rpg-14/Delta V4/rpg-18)

- `GET /api/v1/characters/{id}` returns a `CharacterResponse` (`id`, `name`, `body`, `mind`, `attributes`, `attributeBreakdowns`, `poolAttributes`, `calculatedValues`, `loadCapacity`) — first REST surface in this project. **`attributes`/`attributeBreakdowns`/`calculatedValues`/`loadCapacity` moved up from nested under `body` to top-level siblings of `body`/`mind` in rpg-18** — they were always a whole-`PlayableCharacter` concern, and now that Mind-driven formulas exist this stopped being a cosmetic-only nesting choice. **`poolAttributes` added rpg-20** — see below.
- **`body` response structure (rpg-18: attributes/etc. moved out, see above):** `body.biomechanics` (genetics + bodyComposition), `body.bodySystems` (bloodSystem + cardiacSystem + pulmonarySystem + neuralSystem + hormonalSystem + digestiveSystem), `body.physicalTraits` (sensorialOrgans + bodyStructure + trainingAndConditioning, rpg-14, extended rpg-20), `body.geneticPoints`, `body.trainingPoints`. **`body.spatialIntelligence` no longer exists (rpg-13)** — its three fields moved into `body.bodySystems.neuralSystem`.
- **`mind` response structure (rpg-18, restructured rpg-19, extended 2026-07-07/rpg-20):** `mind.values` (14 fields, 0-5), `mind.erudition` (`levels: Map<String,int>` keyed by `Knowledge` name, `points: {total,spent,remaining}`), `mind.personality` (`selectedTraits` — set of `Trait` names, new rpg-19), `mind.labours` (same `{levels, points}` shape as `erudition`, new rpg-19), `mind.generalPersonality` (`{vanity, focus}`, new 2026-07-07), `mind.weaponProficiencies` (`levels: Map<String,int>` keyed by `Weapon` name, no `points` field — no shared budget, new rpg-20), `mind.eventPoints` (same `{total, spent, remaining}` shape as `geneticPoints`/`trainingPoints`). **rpg-18's `erudition.selectedTraits`/`freeTraitSlots` no longer exist** — knowledge is now leveled, and trait selection moved to `personality`.
- **`attributes`** (rpg-11, expanded rpg-13/rpg-14/Delta V4/rpg-18/rpg-19/2026-07-07/rpg-20/rpg-21): 84 fields. `strength` is **gone** (Delta V4) — replaced by `pushStrength`, `legDrive`, `gripStrength`, `liftStrength`, `swingPower`, `grapplingSelfLifting`; `sixthSense` **renamed to `mediunity`** (rpg-18, pure rename). `durability` is **gone (rpg-21)** — replaced by `softTissueDurability` (baseline 10, floored) and `boneDurability` (baseline 60). 14 Concern fields (`selfConcern`...`peaceConcern`, rpg-18) and 9 Mind-driven attributes (`survivalSkills`, `animalCaring`, `manipulation`, `behaviorReading`, `discretion`, `bluffing`, `faith`, `illusionResistance` — **renamed from `illusionResistanceSanity` in rpg-19** — `creativity`). rpg-19 added `analysis`, `closeCombat`, `lowRangeCombat`, `longRangeCombat`. 2026-07-07 added `psyquismOutput`, `psyquismDefense`, `charmResistance`, `concentration`, `purity`. rpg-20 added `reactionSpeed`, `hiding`, `sneaking`. rpg-21 added 6 Skills fields — `alchemy`, `machineHandling`, `performance`, `sciencePractice`, `healing`, `hackingAndPrograming`. **`staminaPool`, `mentalHealthPool`, `memoryPool`, `manaPool` moved out of this map in rpg-20** — see `poolAttributes` below. `cardiovascularCapacity`, `fatigueRate`, `energyCost` remain absent (see rpg-11 removal note above). `meanStrength` (hidden engine) is never in this response.
- **`attributeBreakdowns`** (Delta V4, extended rpg-18/rpg-19/rpg-20/rpg-21): `Map<String, AttributeBreakdownResponse>` keyed the same as `attributes` **plus the five pool attributes** (`staminaPool`, `mentalHealthPool`, `memoryPool`, `manaPool`, `chiPool` — the breakdown still describes only the `total` computation), each `{baseline, terms}` where **`terms` is now a list of `{label, value}` objects, not bare numbers (rpg-21)** — backs the frontend's labeled tooltip format. Absent for `swingPower`/`grapplingSelfLifting` (not additive-standard formulas) and for the Load Capacity group (separate DTO, non-linear transform).
- **`poolAttributes`** (new rpg-20): `PoolAttributesResponse`, five fields (`staminaPool`, `mentalHealthPool`, `memoryPool`, `manaPool`, `chiPool`), each `{total, current}` — `current` always equals `total` today (see the domain-model section above). These five are no longer present in the flat `attributes` map.
- **`calculatedValues`**: `symbolicTotalMass` (int), `totalMassKg` (double, **renamed from `displayMassKg` in rpg-19**). **`loadCapacity`**: `lightLoadKg`, `heavyLoadKg`, `maxCapacityKg`, `dragCapacityKg` — all `int` (rpg-12), derived from `liftStrength` (Delta V4).
- **`bodyComposition`** carries `muscleDistribution`, `flexibility`, and (rpg-14) `boneDensity` + `tendonsAndLigaments`. **`genetics` no longer carries `boneDensity` or `skinThickness` (both moved out in rpg-14)** — `genetics` is now just `endomorphy`/`mesomorphy`/`ectomorphy`/`height`/`limbRatio`.
- **`bloodSystem`** carries `bloodThickness` (rpg-13) alongside `oxygenCarryingCapacity`.
- **`neuralSystem`** (renamed from `nervousSystem`, rpg-13) carries all thirteen fields — `neuralDrive`, `neuromuscularEfficiency`, `cerebralCapacity`, `synapsisQuality`, `hippocampus`, `thalamus` (new, Delta V4), `hypothalamus`, `amygdalaAndCingulum`, `immunity`, `agility`, `precision`, `noeticPlexus`, `phaxicCerebelum` (new, 2026-07-07, same arcane-organ shape as `noeticPlexus`).
- **`hormonalSystem`**: `thyroid`, `adrenalGlands`, and (rpg-14) `predominantMorphicHormone`. **`digestiveSystem`** (rpg-13): `digestiveAbsorption` (renamed from `nutrientAbsorption`, Delta V4), `impurityCleaning`, `ketosisQuality`.
- **`physicalTraits`** (rpg-14): `sensorialOrgans` (`eyesSensitivity`, `earsSensitivity`, `noseSensitivity`) and `bodyStructure` (`skinThickness` — moved from `genetics`, `shapeAesthetics`, `cellularHealth`).
- No persistence yet: `GetPlayableCharacterService` always returns `PlayableCharacter("Keynor", Body.humanTemplate(), Mind.humanTemplate())`.
- `AttributesResponse.from(PlayableCharacter)` and `AttributeBreakdownsResponse.from(PlayableCharacter)` are the single points of delegation for both character sheet and preview response.
- `GetPlayableCharacterUseCase` is wired as a `@Bean` in `DomainConfiguration`.

### Character preview (rpg-18: replaces "Biomechanics preview" outright — renamed, not aliased)

- `POST /api/v1/character/preview` (was `/api/v1/biomechanics/preview`) takes a `CharacterPreviewRequest { body: BodyPreviewRequest, mind: MindPreviewRequest }`. `BodyPreviewRequest` (renamed from `BiomechanicsPreviewRequest` — same 4 fields: `genetics`, `bodyComposition`, `bodySystems`, `physicalTraits`). `MindPreviewRequest { values: ValuesInput (14 fields), erudition: EruditionInput (levels: Map<String,int>), personality: PersonalityInput (selectedTraits: Set<String>), labours: LaboursInput (levels: Map<String,int>), generalPersonality: GeneralPersonalityInput (vanity, focus), weaponProficiencies: WeaponProficienciesInput (levels: Map<String,int>) }` — `personality`/`labours` added rpg-19; `generalPersonality` added 2026-07-07; `weaponProficiencies` added rpg-20 (no `points` field — no shared budget); `erudition`'s shape changed from `selectedTraits: Set<String>` to `levels: Map<String,int>` in the rpg-19 delta. All leaf fields are `int`.
- Response: `CharacterPreviewResponse` (renamed from `BiomechanicsPreviewResponse`, same shape) nests `attributes` (84 fields), `attributeBreakdowns`, `poolAttributes` (new rpg-20), `calculatedValues`, and `loadCapacity`.
- **Why this changed:** once `ShortMemory`/`Reasoning`/`Enfactuation`/`Will` started reading `Values` (rpg-18), a Body-only preview could no longer resolve every attribute correctly — the preview contract had to accept both pillars at once. (rpg-19 reverted those specific cross-pillar terms, but the unified endpoint stayed — `Trait`-driven Values-bonus terms now create the same cross-pillar dependency.)
- **Stateless** — no persistence, no character identity. Not the eventual creation contract.
- `PreviewAttributesService` builds a transient `Body.previewTemplate(...)` and `Mind.previewTemplate(values, erudition, personality, labours, generalPersonality, weaponProficiencies)` and wraps both in a `PlayableCharacter` — `BodyCoefficients.defaults()` apply since none of the formulas depend on tuned coefficients for preview purposes.
- `PreviewAttributesUseCase.calculate(...)` now takes 9 parameters (`WeaponProficiencies` added rpg-20, on top of 2026-07-07's `GeneralPersonality` and rpg-19's `Personality`/`Labours` additions) — another breaking signature change.
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
        ├── jung.md            ← Level 2 — database schema (migrations) and data
        ├── clown.md           ← Level 1 (narrower) — character-creation suggester
        └── doraxes.md         ← Level 1 (narrower) — DM assistant and rules authority
```

| Agent | Level | Scope |
|-------|-------|-------|
| Gaemes | 3 — Architect | Both `keynor-rpg` (backend) and `keynor-rpg-client` (frontend) |
| Void | 2 — Developer | Java source: domain, application, infrastructure layers |
| Jung | 2 — Developer | Flyway migrations, seed/maintenance scripts, read-only DB queries — see `jung.md` for the named exception to the workspace's default migration-authorship rule |
| Clown | 1 (narrower) | Suggests character-creation inputs (interactive/template/prompt modes) from `additive-attribute-formulas.md`/`mind-pillar-traits-and-values.md`; never writes a file, never touches code or the API — see `clown.md` |
| Doraxes | 1 (narrower) | Assists the DM — challenge design, combat/interaction pacing, rules adjudication from `game-rules.md`; may draft (not commit) additions to that file — see `doraxes.md` |

No dedicated test agent exists yet for `keynor-rpg` — Gaemes will propose one as implementation work begins, following the pattern established by Judis (`keynor-core`).

---

## Game rules, character creation assistance, and DM assistance

Three new project skills, introduced 2026-07-08, hold gameplay/design content distinct from the domain-model skills above (which describe *how the backend computes stats*, not *how the game is played*):

- `.claude/skills/game-rules.md` — the tabletop-style ruleset: tests, contested tests, combined tests, assistance, the attribute-reference table (what each attribute means narratively), damage resolution (types, materials, recoil), and combat timing. Work in progress — several `**OPEN QUESTION**` markers flag rules that are real but underspecified (most notably: the actual damage-vs-resistance formula, and the action time-cost/stamina-cost tables) and must not be treated as settled until the user resolves them. `Bonds` and `Stress and Sanity` are `*TODO*` placeholders — no content exists yet, don't invent any.
- `.claude/skills/character-creation-questionnaire.md` — the scripted Q&A Clown's interactive mode walks a player through. **Mandatory-sync rule, same discipline as `formulas-reference-page.md`:** whoever ships a new character-creation input or a materially changed formula (Void, Dot, or Gaemes) must update this questionnaire in the same delta.
- Two new Level 1 agents consume these: **Clown** (character-creation suggester — chat-only, never writes a file, never touches code/API/git) and **Doraxes** (DM assistant and rules authority — may draft, not commit, additions to `game-rules.md`; full command of that file is expected, not a contextual skim). See their own agent files for the complete permission model — both are narrower than the workspace's Level 1 baseline, not just Level 1 by default.

**Pending, outside Gaemes' authority:** the workspace-root `.claude/SKILLS.md` "Reading guide by role" table needs Clown and Doraxes columns added, per Skill 06's "Maintaining this table" rule — Gaemes has no repository access to the workspace root itself (see the root `CLAUDE.md`'s Coordination-with-Omnia section), so this has been escalated to Omnia rather than attempted here.

---

## Agent operational rules

Before beginning the task itself — reading project source or task-specific documentation, implementing features, creating branches, running commands, or opening PRs — every agent must:

1. Switch to `main`: `git checkout main`
2. Pull the latest changes: `git pull`

This does not apply to the agent's own fixed mandatory reading (`ARCHITECTURE.md`, the root `CLAUDE.md`, `SKILLS.md`, this file, the agent's own `.md` file, and any Always-tier skill file) — reading those is how an agent learns this very rule, not an action on the project's current state. Sync once the agent moves on to the task itself.

A second pull is not required within the same task session. See workspace `SKILLS.md` — Skill 09.

---

*Last updated: 2026-07-08 (rpg-22: two new Level 1 agents, `Clown` (character-creation suggester — interactive/template/prompt-input modes, chat-only output, never writes a file or touches code/API/git) and `Doraxes` (DM assistant and rules authority — full command of the new `game-rules.md`, may draft but not commit additions to it), both narrower than the workspace's Level 1 baseline. New project skill `.claude/skills/game-rules.md` — first draft of the tabletop ruleset (tests, damage, combat timing), translated from the user's Portuguese input per the workspace's English-only artifact rule, with `**OPEN QUESTION**` markers on every rule that's real but underspecified (most significantly: no formula connects raw damage/material resistance to the Irrelevant/Significant/Irreversible outcome categories, and no table exists mapping any action to a time or stamina cost — both block running combat as written until the user resolves them) — `Bonds`/`Stress and Sanity` sections are `*TODO*`, intentionally empty. New project skill `.claude/skills/character-creation-questionnaire.md` — Clown's interactive-mode script, seeded with the user's own example questions; a new mandatory-sync rule (mirroring `formulas-reference-page.md`'s) requires it to be updated in the same delta as any new character-creation input. See the new "Game rules, character creation assistance, and DM assistance" section above. Previous entry, same day — governance: reduced the testing-depth policy in `.claude/skills/additive-attribute-formulas.md` (new "Testing scope for formulas" section) and `.claude/skills/mind-pillar-traits-and-values.md` (new "Testing scope for domain rules" section) — worst-case-combination tests, extremes tests, and exhaustive per-coefficient sweeps are no longer expected by default for every new formula/rule; a neutral-value test plus a direction-sanity test (formulas) or a happy-path test plus a rule-violation test (domain rules/use cases) are now the baseline. Historical sections describing the old heavier convention were left as-is, since they accurately describe what those past deltas shipped. User-requested change, scoped to the RPG project's own skill docs only — the workspace-wide Skill 04 (Test Coverage) was flagged as out of Gaemes' authority and left untouched, pending the user's decision on whether to also raise it with Omnia. Previous entry, 2026-07-07 — pure rename: `MaxMovementSpeed` → `MovementSpeed` — `getMaxMovementSpeed()`/`getMaxMovementSpeedBreakdown()`, `AttributesResponse.maxMovementSpeed`, and the three `kMaxMovementSpeed*` coefficients all renamed, no formula/weight change. See `.claude/skills/additive-attribute-formulas.md`'s new Rename section. Previous entry, same day — `Mind` gained a fifth data group, `GeneralPersonality` (`vanity`/`focus`, both 1-9 neutral 5, `EVENTFUL`, unrelated to `Personality` despite the name overlap); `NeuralSystem` gained a 13th field, `phaxicCerebelum` (absent/0 on the human template, arcane-organ shape, neutral 6); 5 new attributes (`PsyquismOutput`, `PsyquismDefense`, `CharmResistance`, `Concentration`, `Purity`); `Vanity` gained modifier terms on `Enfactuation`/`Intimidation`; 12 new standalone `Trait` constants (`Trait.values()` 28→40) introduce a "concern-threshold" prerequisite kind (gated by a concern at/above 2 or 4, not the base/advanced pair's exact-default/already-selected checks) across 9 of the 14 `TraitGroup`s — 9 grant a real formula term, 3 (`Egotist`/`Loyalist`/`Retribution Seeker`) are entirely situational; `PreviewAttributesUseCase.calculate(...)` grew to 8 parameters; `attributes` grew to 73 fields. See `.claude/skills/additive-attribute-formulas.md` and `.claude/skills/mind-pillar-traits-and-values.md`'s new sections. Previous entry, 2026-07-05 (rpg-19: `InputNature.IMMUTABLE` renamed `BIRTH`; Knowledge stopped being a boolean `Trait` and became a leveled 0-4 slider (new `Knowledge`/`KnowledgeGroup` enums, `TRAINED`), spending from a 2-point `Erudition` budget; `Trait`/`TraitGroup` repurposed entirely for a new 28-constant Values-linked personality catalog (14 base/advanced pairs per `Values` concern) stored in a new `Personality` Mind data group, gated by real prerequisites and forcing the linked Value to 0 on selection; new `Labours`/`Job` pair mirrors Erudition/Knowledge with no formula effect of its own; 8 of the 9 rpg-18 cross-pillar `Values` terms reverted outright (verified against `PlayableCharacter.java` directly, not the changelog, after the changelog was found to under-report which terms existed) and mostly replaced by Values-trait bonus terms; `IllusionResistanceSanity` renamed `IllusionResistance`, `DisplayMassKg` renamed `TotalMassKg`; 4 new attributes (`Analysis`, `CloseCombat`, `LowRangeCombat`, `LongRangeCombat`); `PreviewAttributesUseCase.calculate(...)` grew to 7 parameters. See `.claude/skills/mind-pillar-traits-and-values.md` (rewritten) and `.claude/skills/additive-attribute-formulas.md`'s new rpg-19 section. Previous entry, 2026-07-04 (rpg-18: Mind pillar implemented — `Mind` (Values + Erudition) added as a sibling of `Body`; new `InputNature` enum (Immutable/Trained/Eventful) and new `Trait` boolean-input type (17 Erudition traits, 7 groups, general-but-unused prerequisite hook); 14 Concern attributes (direct mirrors of each Value, a 4th additive-standard exception); 4 existing attributes (ShortMemory/Reasoning/Enfactuation/Will) gained a Values-driven term, Will decoupled from MentalHealthPool; 9 new attributes (SurvivalSkills, AnimalCaring, Manipulation, BehaviorReading, Discretion, Bluffing, Faith, IllusionResistanceSanity, Creativity); SixthSense renamed Mediunity; REST contract unified — `/api/v1/biomechanics/preview` replaced outright by `/api/v1/character/preview` accepting both Body and Mind, `CharacterResponse` gained `mind` and moved `attributes`/`attributeBreakdowns`/`calculatedValues`/`loadCapacity` up from `body` to top level. See `.claude/skills/mind-pillar-traits-and-values.md` (new) and `.claude/skills/additive-attribute-formulas.md`. Previous entry, 2026-07-03: Previous entry, same day — rpgc-17, keynor-rpg-client: added an in-app "Formulas" reference page mirroring every formula/coefficient/input documented in this file — added a cross-repo mandatory-sync rule (see the new section above and that project's `.claude/skills/formulas-reference-page.md`) requiring this file and that page to be updated together going forward. Previous entry, same day — Delta V4 continued: two pure renames (`ketosisQuality`→`ketosisEfficiency`, `HormonalSystem`→`HormonalGlandularSystem`) and 3 new arcane-organ attributes (`ManaPool`/`ArcaneOutput`/`SixthSense`). Previous entry — Delta V4: old global Strength and its Load Capacity group deleted outright, replaced by hidden meanStrength() engine + 4 specialized strengths (PushStrength/LegDrive/GripStrength/LiftStrength) + 2 averaged combat attributes (SwingPower/GrapplingSelfLifting); Load Capacity re-anchored on LiftStrength; NeuralSystem gained thalamus (11th field) — Hippocampus now memory-only, Thalamus takes over Sight/Hearing/Smell/Balance/Aim; DigestiveSystem.nutrientAbsorption renamed digestiveAbsorption; 3 new attributes (AngerResistance, FearResistance, PainThreshold); Balance and Aim rebuilt; MemoryPool/ShortMemory reweighted; FoodPoisoningAlcoholResistance gained a DigestiveAbsorption penalty term; every additive-standard getter gained a companion getXxxBreakdown(), served via new AttributeBreakdownsResponse alongside the existing flat attributes — backs frontend tooltips without duplicating formula logic client-side. Also this session: rpg-14's original PR (#21) was discovered to have never reached main — merged into an already-landed base branch 24s after that branch itself merged into main — recovered via PR #22; root-cause fix (PR #23) restricts Gaemes/Void to opening PRs against main only, never another task/feat/release branch. See `.claude/skills/additive-attribute-formulas.md`. Previous entries — rpg-11, 2026-07-01: full replacement of every derived-attribute formula — including the same-day rpg-10 Speed redesign — with the additive standard, baseline 60; all Body input scales moved from double/float to discrete int; CardiovascularCapacity and FatigueRate removed, FatigueResistance and StaminaRecovery added; new Load Capacity group; BodyCoefficients rewritten; same-day fix subtracted kLoadCapacityStrengthOffset (25) from Strength for Load Capacity. rpg-12, 2026-07-02: Load Capacity recalibrated again — kLoadCapacityStrengthOffset removed entirely, kMaxCapacityDivisor raised 25→150 to work directly off baseline-60 Strength, LightLoad/HeavyLoad switched from 0.3/0.7 fractions to exact ⅓/⅔ integer division, all four Load Capacity methods now return int. rpg-13, 2026-07-02: SpatialIntelligence merged into NeuralSystem (renamed from NervousSystem, perception→hippocampus); new HormonalSystem and DigestiveSystem groups; bloodThickness added to BloodSystem, skinThickness added to Genetics (both immutable, explicitly provisional); 15 new derived attributes across 4 modules (Cognitive, Sensory/Hormonal/Stress, Biological defense, Metabolic/survival); StaminaPool and FatigueResistance each gained a term. rpg-14, 2026-07-02: new PhysicalTraits Body sibling (SensorialOrgans + BodyStructure); skinThickness moved Genetics→BodyStructure (unchanged), boneDensity moved Genetics→BodyComposition (became mutable); tendonsAndLigaments added to BodyComposition, predominantMorphicHormone added to HormonalSystem driving new Tmod/Pmod hormone modifiers; Sight/Hearing/Smell diverged into 3 independent formulas; Strength/Durability/Balance/MentalHealthPool/3 biological-defense resistances each gained a term; 6 new attributes (zero-baseline FatGainRate/MuscleGainRate, social Intimidation/Diplomacy/Enfactuation/Command))))*
