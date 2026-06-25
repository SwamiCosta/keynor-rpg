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

## Stack (proposed — to confirm before first implementation)

| Concern | Technology |
|---------|------------|
| Language | Java 21 |
| Framework | Spring Boot (version to match `keynor-core` for consistency) |
| Database | PostgreSQL — **own instance, separate from keynor-core's** (game data is never stored in the lore database) |
| Build tool | Maven |
| Testing | JUnit 5 + Mockito + Testcontainers |

Adding `pom.xml` and its dependencies is a protected action (workspace `CLAUDE.md` — Protected actions) and requires explicit user authorization before it is created.

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
└── pom.xml                              ← not yet created (protected action)
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

Not yet defined. No entities, use cases, or endpoints exist in this project. Gaemes proposes the initial domain model (playable character, attributes, skills, combat, session, campaign) before any implementation begins, following the same planning protocol used by Imaws in `keynor-core`.

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

*Last updated: 2026-06-23*
