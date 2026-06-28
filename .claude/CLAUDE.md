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

### Biomechanics — genetics, body composition and output formulas (implemented)

- `Biomechanics` lives **inside `Body`** (`Body.getBiomechanics()`), not as a separate member on `PlayableCharacter` — it is the physical-attribute side of the `Corpo` pillar, nested under the same root as the anatomical wound-tracking tree rather than floating alongside it. `Biomechanics.humanDefaults()` is built inside `Body.humanTemplate()` and mirrors its factory pattern.
- Two-layer philosophy, per the user's design notes: a **genetic layer** (`Genetics` + `BloodSystem`) fixed once at character creation, and a **trainable layer** (`BodyComposition` + `NervousSystem` + `CardiacSystem` + `PulmonarySystem`) that changes through training/diet over the course of the game. The genetic layer does not feed the output formulas below directly — it is documented intent to modulate the *rate* at which the trainable layer changes (e.g. mesomorphs gaining muscle faster); that rate formula is not yet implemented.
- **`Genetics`** is immutable (final fields, no setters) — this encodes "genetics cannot change after game start" as a type-level invariant rather than a runtime check. Holds `endomorphy`, `mesomorphy`, `ectomorphy` (three independent 0–10 axes, Heath-Carter inspired — not mutually exclusive categories), `height` (cm), `limbRatio` (~0.85–1.15), `boneDensity` (0–10). Defaults: all somatotype/density axes at 5, height 170cm, limbRatio 1.0.
- **`BodyComposition`** is mutable (plain setters, since it trains over time). Holds `bodyFat` (kg — a value the player generally wants to *lower*, not raise; changed from a 0–1 percentage to kg on 2026-06-28 to simplify the mass formulas below), `muscleMass` (kg), `dominantFiberType` (-1 slow/endurance to +1 fast/power), `neuromuscularEfficiency` (0–1, fraction of theoretical force actually usable — the "technique vs. size" axis). Defaults: 14kg body fat, 30kg muscle mass, fiber type 0 (neutral mix), neuromuscular efficiency 0.5. **`totalMass` is not stored here** — see derived mass formulas below; `BodyComposition` no longer carries it as a player-set field.
- **Derived mass — `getBoneMass()`, `getOrganWaterMass()`, `getTotalMass()` on `Biomechanics` (added 2026-06-28):** `totalMass` stopped being a player-set value and is now fully derived from genetics plus the trainable `bodyFat`/`muscleMass`:
  ```
  BoneMass       = kBoneMass * (height/100)^2 * (1 + kBoneDensity * (boneDensity - 5))
  OrganWaterMass = kOrganWaterMass * (height/100)^2
  TotalMass      = bodyFat + muscleMass + BoneMass + OrganWaterMass
  ```
  `boneDensity` acts as a bounded deviation from the mid-range default (5), not a multiplier — `boneDensity = 0` does not collapse bone mass to zero, and high height + density does not produce "iron bones" (validated by simulation across the full input range before implementation). `OrganWaterMass` exists because `bodyFat + muscleMass + boneMass` alone structurally excludes organs, blood, skin, and water (~25% of real body weight); it scales only with height, since no genetic trait models organ size. `kBoneMass = 2.7`, `kBoneDensity = 0.06`, `kOrganWaterMass = 6.3` are the only three `BiomechanicsBalance` coefficients that don't default to `1.0` — they were calibrated so `getTotalMass()` reproduces the previous hardcoded 70kg human default almost exactly (boneMass ≈ 7.80kg, organWaterMass ≈ 18.21kg, totalMass ≈ 70.01kg at `Genetics.defaults()`). All formulas that previously read `bodyComposition.getTotalMass()` (`getSpeed()`, `getFatigueRate()`, `getEnergyCost()`, `getDurability()`) now call `Biomechanics.getTotalMass()`; `getDurability()`'s fat-cushion term reads `bodyComposition.getBodyFat()` directly (the old `getFatMass()` derived getter was removed, since `bodyFat` is already in kg).
- **`Biomechanics.getCardiovascularCapacity()` is a derived getter, not a stored field** — the live average of `BloodSystem.oxygenCarryingCapacity`, `CardiacSystem.cardiacOutput`, and `PulmonarySystem.pulmonaryCapacity`, per the user's instruction to treat it as a resultant aggregate of the other three systems.
  - **`BloodSystem`** (`oxygenCarryingCapacity`, 0–10) — genetic, immutable (no setters), like `Genetics`.
  - **`CardiacSystem`** (`cardiacOutput`, 0–10) and **`PulmonarySystem`** (`pulmonaryCapacity`, 0–10) — both trainable (mutable setters).
  - All three default to 5 (mid-range).
- **`NervousSystem`** (`neuralDrive`, 0–10, trainable) is a placeholder for a nervous-system model the user intends to detail later; `neuralDrive` is documented to eventually modulate `BodyComposition.dominantFiberType`, but is **not yet wired into any formula** — same "documented intent only" pattern as `Body`'s unimplemented side effects.
- **`AttributePointBudget`** is a generic, reusable spend/remaining tracker (`totalPoints`, `spentPoints`, `spend(amount)` throwing on overspend or negative amounts, `remainingPoints()`). `Biomechanics` holds one instance for the genetic pool and one for the training pool, both seeded at 20 points in `humanDefaults()` — illustrative placeholders, same "not balanced game data" caveat as `Body`'s hit point values. The budget is deliberately unaware of which attribute it funds: **the per-attribute point cost of moving away from a default (e.g. points per cm of height vs. points per somatotype unit) is deferred to the character-creation use case, which does not exist yet.**
- **Output formulas are implemented as plain instance methods on `Biomechanics`**, each usable at any time off the character's current state: `getStrength()`, `getSpeed()`, `getStaminaPool()`, `getFatigueRate(intensity)`, `getEnergyCost(intensity)`, `getDurability()`, plus `getCardiovascularCapacity()` above. `intensity` is a method parameter rather than stored state, since it represents the current activity, not a character trait.
  - `getStrength()` / `getSpeed()` / `getStaminaPool()` / `getDurability()` follow the doc's formulas directly (²⁄₃-power square-cube law for Strength, Kleiber's ¾ law for the mass terms, log/sqrt for Durability's inertia/fat-cushion terms).
  - `getEnergyCost(intensity)`'s `ActivityCost` and `Eficiencia` terms were left in the doc as named-but-unspecified functions of `(M, intensity)` / `(CV, FT)`; this implementation concretizes them as `kActivityCost × M × intensity` and `kEfficiency × CV × (1 − 0.3 × FT)` (the latter mirroring `getStaminaPool()`'s fiber-type shape) — flagged here since they are this implementation's own interpretation, not lifted directly from the doc.
- **`BiomechanicsBalance`** holds every free coefficient as a mutable, independently settable field — `k1`..`k9` and `c`, matching the doc's own naming, plus `kBmr`/`kActivityCost`/`kEfficiency` for the three terms invented for `getEnergyCost`. Most default to `1.0` (neutral multiplier) — same "not balanced game data" caveat as everywhere else in this domain; tune by playing, not by deriving a "correct" value. **Exception:** `kBoneMass` (2.7), `kBoneDensity` (0.06), `kOrganWaterMass` (6.3) default to the calibrated values above instead of `1.0`, since the mass formulas need to reconcile with a believable human body weight out of the box, not just stay neutral.

---

## REST API

### Character sheet (implemented — `task/character-sheet-endpoint`)

- `GET /api/v1/characters/{id}` returns a `CharacterResponse` (`id`, `name`, nested `body.biomechanics`) exposing the genetics, body composition, blood/cardiac/pulmonary/nervous systems, derived attributes, and both point budgets — the first REST surface in this project (`infrastructure/web/CharacterController.java`).
- `body.biomechanics` carries a `calculatedValues` group (`totalMass`, `boneMass`, `organWaterMass`) alongside `bodyComposition`, separating player-set fields from the fully-derived mass values described above (added 2026-06-28).
- No character persistence or creation flow exists yet: `GetPlayableCharacterService` (the `GetPlayableCharacterUseCase` implementation) ignores the `id` argument and always returns the same in-memory `PlayableCharacter("Keynor", Body.humanTemplate())`. The `id` lookup signature is kept anyway so the response contract does not change once real persistence lands.
- Every nested response type is a `record` in `application/dto/` with a static `from(<domainType>)` factory method, mirroring the domain's own `defaults()`/`humanTemplate()` factory-method convention. `CharacterController` itself does no mapping beyond calling `CharacterResponse.from(id, character)`.
- `attributes.fatigueRate` is included in the sheet, computed at a fixed baseline `intensity = 1.0` — per explicit user direction, it is meant to be visible as a character trait now (it will later feed `energyCost`), even though `Biomechanics.getFatigueRate(intensity)` takes a parameter. `attributes.energyCost` is deliberately excluded: it is intrinsically tied to real-time activity, not a static trait, and is deferred to a future activity/combat API.
- `GetPlayableCharacterUseCase` is wired as a `@Bean` in `DomainConfiguration`, same pattern as `BodyCascadeResolver`.

### Biomechanics preview (implemented — `task/biomechanics-attributes-preview`)

- `POST /api/v1/biomechanics/preview` takes a `BiomechanicsPreviewRequest` (raw `genetics`, `bodyComposition`, `bloodSystem`, `cardiacSystem`, `pulmonarySystem`, `nervousSystem` input objects) and returns a `BiomechanicsPreviewResponse` (`infrastructure/web/BiomechanicsPreviewController.java`).
- `BiomechanicsPreviewResponse` nests `attributes` (the same derived-attributes shape already used by the character sheet) and `calculatedValues` (`totalMass`, `boneMass`, `organWaterMass`) — changed from a flat `AttributesResponse` on 2026-06-28, since the request body only carries `bodyFat`/`muscleMass` and the preview needed a way to also surface the values now derived from them. Nesting under `attributes` mirrors the character sheet's own `body.biomechanics.attributes` convention rather than introducing a second shape.
- **This endpoint is stateless and exists solely to support the `keynor-rpg-client` character-creation simulator's live preview** — it has no persistence, no character identity, and is **not** the eventual character-creation contract. Per the workspace's FE-prototype-before-contract workflow (decided 2026-06-27), the real `POST`/`PUT` creation endpoints and their payload shape are deferred until the user has felt the FE prototype's UX and hands down the actual contract.
- Each of the six input objects is its own record in `application/dto/` (`GeneticsInput`, `BodyCompositionInput`, `BloodSystemInput`, `CardiacSystemInput`, `PulmonarySystemInput`, `NervousSystemInput`), mirroring the corresponding domain class's raw constructor fields one-to-one, each with a `toDomain()` method — the inverse of the existing `*Response.from(domainType)` convention.
- `PreviewAttributesService` (the `PreviewAttributesUseCase` implementation) builds a transient `Biomechanics` from the six converted domain objects plus placeholder point budgets (20/20) and `BiomechanicsBalance.defaults()` — safe because none of `Biomechanics`'s output formulas (`getStrength()`, `getSpeed()`, etc.) read the point budgets or `balance`'s actual values beyond its coefficients, which stay at their neutral `1.0` default either way.
- `PreviewAttributesUseCase` is wired as a `@Bean` in `DomainConfiguration`, same pattern as `GetPlayableCharacterUseCase`.

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

Before taking any action in this project — reading state, implementing features, creating branches, running commands, or opening PRs — every agent must:

1. Switch to `main`: `git checkout main`
2. Pull the latest changes: `git pull`

A second pull is not required within the same task session. See workspace `SKILLS.md` — Skill 09.

---

*Last updated: 2026-06-28* 