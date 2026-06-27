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
| Database | PostgreSQL — **own instance, separate from keynor-core's** (game data is never stored in the lore database) |
| Build tool | Maven |
| Testing | JUnit 5 + Mockito + Testcontainers |

`pom.xml` was created 2026-06-25 after explicit user authorization — see PR #3. Any future dependency change still requires its own authorization.

---

## Local environment assumptions

`keynor-rpg` expects two things already running before any agent is invoked: its own PostgreSQL instance (separate from `keynor-core`'s — see Boundary with keynor-core below) and the Spring Boot application itself. Agents never start, stop, or restart either, and never provision a disposable substitute (e.g. a one-off container) for a missing one.

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
└── pom.xml                              ← Spring Boot 3.5.0 / Java 21 (created 2026-06-25, PR #3)
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
- **`BodyComposition`** is mutable (plain setters, since it trains over time). Holds `totalMass` (kg), `bodyFatPercentage` (0–1 — a value the player generally wants to *lower*, not raise), `muscleMass` (kg), `dominantFiberType` (-1 slow/endurance to +1 fast/power), `neuromuscularEfficiency` (0–1, fraction of theoretical force actually usable — the "technique vs. size" axis). `getFatMass()` is derived (`totalMass × bodyFatPercentage`). Defaults: 70kg total mass, 20% body fat, 30kg muscle mass, fiber type 0 (neutral mix), neuromuscular efficiency 0.5.
- **`Biomechanics.getCardiovascularCapacity()` is a derived getter, not a stored field** — the live average of `BloodSystem.oxygenCarryingCapacity`, `CardiacSystem.cardiacOutput`, and `PulmonarySystem.pulmonaryCapacity`, per the user's instruction to treat it as a resultant aggregate of the other three systems.
  - **`BloodSystem`** (`oxygenCarryingCapacity`, 0–10) — genetic, immutable (no setters), like `Genetics`.
  - **`CardiacSystem`** (`cardiacOutput`, 0–10) and **`PulmonarySystem`** (`pulmonaryCapacity`, 0–10) — both trainable (mutable setters).
  - All three default to 5 (mid-range).
- **`NervousSystem`** (`neuralDrive`, 0–10, trainable) is a placeholder for a nervous-system model the user intends to detail later; `neuralDrive` is documented to eventually modulate `BodyComposition.dominantFiberType`, but is **not yet wired into any formula** — same "documented intent only" pattern as `Body`'s unimplemented side effects.
- **`AttributePointBudget`** is a generic, reusable spend/remaining tracker (`totalPoints`, `spentPoints`, `spend(amount)` throwing on overspend or negative amounts, `remainingPoints()`). `Biomechanics` holds one instance for the genetic pool and one for the training pool, both seeded at 20 points in `humanDefaults()` — illustrative placeholders, same "not balanced game data" caveat as `Body`'s hit point values. The budget is deliberately unaware of which attribute it funds: **the per-attribute point cost of moving away from a default (e.g. points per cm of height vs. points per somatotype unit) is deferred to the character-creation use case, which does not exist yet.**
- **Output formulas are implemented as plain instance methods on `Biomechanics`**, each usable at any time off the character's current state: `getStrength()`, `getSpeed()`, `getStaminaPool()`, `getFatigueRate(intensity)`, `getEnergyCost(intensity)`, `getDurability()`, plus `getCardiovascularCapacity()` above. `intensity` is a method parameter rather than stored state, since it represents the current activity, not a character trait.
  - `getStrength()` / `getSpeed()` / `getStaminaPool()` / `getDurability()` follow the doc's formulas directly (²⁄₃-power square-cube law for Strength, Kleiber's ¾ law for the mass terms, log/sqrt for Durability's inertia/fat-cushion terms).
  - `getEnergyCost(intensity)`'s `ActivityCost` and `Eficiencia` terms were left in the doc as named-but-unspecified functions of `(M, intensity)` / `(CV, FT)`; this implementation concretizes them as `kActivityCost × M × intensity` and `kEfficiency × CV × (1 − 0.3 × FT)` (the latter mirroring `getStaminaPool()`'s fiber-type shape) — flagged here since they are this implementation's own interpretation, not lifted directly from the doc.
- **`BiomechanicsBalance`** holds every free coefficient as a mutable, independently settable field — `k1`..`k9` and `c`, matching the doc's own naming, plus `kBmr`/`kActivityCost`/`kEfficiency` for the three terms invented for `getEnergyCost`. All default to `1.0` (neutral multiplier) — same "not balanced game data" caveat as everywhere else in this domain; tune by playing, not by deriving a "correct" value.

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

*Last updated: 2026-06-27 (Local environment assumptions added — Skill 13)*
