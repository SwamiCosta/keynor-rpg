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
- **`BodyCoefficients`** (renamed from `BiomechanicsBalance` in rpg-8, 2026-06-30): holds every free coefficient as a mutable, independently settable field — `k1`..`k9`, `c`, `kBmr`/`kActivityCost`/`kEfficiency`, plus new spatial-intelligence and movement coefficients. Most default to `1.0` (neutral multiplier). **Exceptions to 1.0 default:** (1) `kBoneMass` (2.7), `kBoneDensity` (0.06), `kOrganWaterMass` (6.3) — mass calibration. (2) `kMuscleDistributionStrength` (0.02), `kMuscleDistributionSpeed` (0.04) — scale a −5..+5 deviation; 1.0 would swing ±500%. (3) `kEvasionNeural` (0.1), `kEvasionFlex` (0.1) — scale 0–10 raw inputs inside a `(1 + k × x)` modifier; 1.0 would allow up to 11× multipliers. `kMuscleDistributionSpeed` is twice `kMuscleDistributionStrength` to match the asymmetric-magnitude design intent. `kLimbRatioSpeed` (added rpg-10, 2026-07-01, default 1.0) scales `(limbRatio − 1)` in `getMaxMovementSpeed()` — the deviation is small (~±0.15), so 1.0 is a safe default.

---

## REST API

### Character sheet (implemented — restructured in rpg-8, 2026-06-30)

- `GET /api/v1/characters/{id}` returns a `CharacterResponse` (`id`, `name`, nested `body`) — first REST surface in this project.
- **`body` response structure** (restructured in rpg-8): `body.biomechanics` (genetics + bodyComposition), `body.bodySystems` (bloodSystem + cardiacSystem + pulmonarySystem + nervousSystem), `body.spatialIntelligence` (perception + agility + precision), `body.attributes` (all derived attrs — see below), `body.calculatedValues` (totalMass, boneMass, organWaterMass), `body.geneticPoints`, `body.trainingPoints`. **Note:** `attributes` and `calculatedValues` previously nested inside `body.biomechanics.*` — they now sit directly under `body.*`. The FE must be updated in rpgc-4 to match.
- `body.attributes` now includes 14 derived values: `cardiovascularCapacity`, `strength`, `speed`, `maxMovementSpeed`, `staminaPool`, `fatigueRate`, `durability`, `sight`, `hearing`, `smell`, `evasion`, `acrobatics`, `meleeAccuracy`, `aim`. `fatigueRate` is computed at baseline `intensity = 1.0`. `energyCost` is excluded (real-time activity, not a static trait).
- **`bodyComposition`** now carries `muscleDistribution` and `flexibility` (moved/added in rpg-8). `genetics` no longer carries `muscleDistribution`.
- **`nervousSystem`** now carries both `neuralDrive` and `neuromuscularEfficiency` (moved from `bodyComposition` in rpg-8).
- No persistence yet: `GetPlayableCharacterService` always returns `PlayableCharacter("Keynor", Body.humanTemplate())`.
- `AttributesResponse.from(PlayableCharacter)` is the single point of delegation for both character sheet and preview response.
- `GetPlayableCharacterUseCase` is wired as a `@Bean` in `DomainConfiguration`.

### Biomechanics preview (implemented — restructured in rpg-8, 2026-06-30)

- `POST /api/v1/biomechanics/preview` takes a `BiomechanicsPreviewRequest` with **4 groups**: `genetics`, `bodyComposition`, `bodySystems` (bloodSystem + cardiacSystem + pulmonarySystem + nervousSystem), `spatialIntelligence` (perception + agility + precision). Previously took 6 flat groups; `bodySystems` now consolidates them. `NervousSystemInput` now carries both `neuralDrive` and `neuromuscularEfficiency`.
- Response: `BiomechanicsPreviewResponse` nests `attributes` (all 14 derived values) and `calculatedValues` (`totalMass`, `boneMass`, `organWaterMass`).
- **Stateless** — no persistence, no character identity. Not the eventual creation contract.
- `PreviewAttributesService` builds a transient `Body.previewTemplate(biomechanics, bodySystems, spatialIntelligence)` and wraps it in a `PlayableCharacter` — `BodyCoefficients.defaults()` apply since none of the formulas depend on tuned coefficients for preview purposes.
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

*Last updated: 2026-07-01 (rpg-10: Speed formula redesigned as pure power-to-weight, independent of Strength and LimbRatio; MaxMovementSpeed extended with kLimbRatioSpeed stride modifier; kLimbRatioSpeed added to BodyCoefficients)*