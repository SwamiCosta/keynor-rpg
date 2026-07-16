-- keynor-rpg — character persistence schema (living reference document)
--
-- NOT a Flyway migration and NOT auto-applied. Per explicit product decision (2026-07-15),
-- this project does not use schema migrations while PlayableCharacter is still evolving
-- quickly. Every CREATE TABLE/ALTER TABLE statement here is run manually by the user against
-- their own local database — this file is kept in sync afterward, as a standing record of the
-- schema's current shape, not as an executable script. Flyway/migration files remain reserved
-- for later, once the schema has stabilized.
--
-- Derived/computed attributes (the ~84 formula-based getters on PlayableCharacter, and the
-- `total` side of every Pool Attribute) are intentionally NOT persisted anywhere in this
-- schema — they are recalculated from the raw inputs below on every read. Only genuinely
-- independent runtime state is stored: wound-tree current/irreversible damage, point-budget
-- spent counts, and the `current` column on pool attributes (reserved for a future spend/
-- damage/rest mechanic — always equal to the recalculated total today).

-- =============================================================================================
-- Root
-- =============================================================================================

CREATE TABLE characters (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    lore_reference  VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- AttributePointBudget, used 3x per character (Body.geneticPoints, Body.trainingPoints,
-- Mind.eventPoints). total_points is stored (not hardcoded 20) in case a future race/event
-- ever grants a different total. remaining_points is derived (total - spent), not stored.
CREATE TABLE character_point_budgets (
    character_id  BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    budget_type   VARCHAR(16) NOT NULL CHECK (budget_type IN ('GENETIC', 'TRAINING', 'EVENT')),
    total_points  INTEGER NOT NULL,
    spent_points  INTEGER NOT NULL,
    PRIMARY KEY (character_id, budget_type)
);

-- =============================================================================================
-- Body — fixed-field groups (1:1 with character, one table per Java class)
-- =============================================================================================

CREATE TABLE body_genetics (
    character_id  BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    endomorphy    INTEGER NOT NULL,
    mesomorphy    INTEGER NOT NULL,
    ectomorphy    INTEGER NOT NULL,
    height        INTEGER NOT NULL,
    limb_ratio    INTEGER NOT NULL
);

CREATE TABLE body_composition (
    character_id           BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    body_fat                INTEGER NOT NULL,
    muscle_mass             INTEGER NOT NULL,
    dominant_fiber_type     INTEGER NOT NULL,
    muscle_distribution     INTEGER NOT NULL,
    flexibility             INTEGER NOT NULL,
    bone_density             INTEGER NOT NULL,
    tendons_and_ligaments    INTEGER NOT NULL
);

CREATE TABLE body_blood_system (
    character_id             BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    oxygen_carrying_capacity INTEGER NOT NULL,
    blood_thickness          INTEGER NOT NULL
);

CREATE TABLE body_cardiac_system (
    character_id       BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    cardiac_output      INTEGER NOT NULL,
    astral_ventriculum  INTEGER NOT NULL,
    astral_atrium       INTEGER NOT NULL
);

CREATE TABLE body_pulmonary_system (
    character_id       BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    pulmonary_capacity  INTEGER NOT NULL
);

CREATE TABLE body_neural_system (
    character_id               BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    neural_drive                INTEGER NOT NULL,
    neuromuscular_efficiency    INTEGER NOT NULL,
    cerebral_capacity           INTEGER NOT NULL,
    synapsis_quality            INTEGER NOT NULL,
    hippocampus                 INTEGER NOT NULL,
    thalamus                    INTEGER NOT NULL,
    hypothalamus                INTEGER NOT NULL,
    amygdala_and_cingulum       INTEGER NOT NULL,
    immunity                    INTEGER NOT NULL,
    agility                     INTEGER NOT NULL,
    precision                   INTEGER NOT NULL,
    noetic_plexus                INTEGER NOT NULL,
    phaxic_cerebelum             INTEGER NOT NULL
);

CREATE TABLE body_hormonal_glandular_system (
    character_id                   BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    thyroid                         INTEGER NOT NULL,
    adrenal_glands                  INTEGER NOT NULL,
    predominant_morphic_hormone     INTEGER NOT NULL,
    subtle_epiphyseal_gland         INTEGER NOT NULL
);

CREATE TABLE body_digestive_system (
    character_id          BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    digestive_absorption   INTEGER NOT NULL,
    impurity_cleaning      INTEGER NOT NULL,
    ketosis_efficiency     INTEGER NOT NULL
);

CREATE TABLE body_sensorial_organs (
    character_id       BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    eyes_sensitivity     INTEGER NOT NULL,
    ears_sensitivity     INTEGER NOT NULL,
    nose_sensitivity     INTEGER NOT NULL
);

CREATE TABLE body_structure (
    character_id       BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    skin_thickness       INTEGER NOT NULL,
    shape_aesthetics      INTEGER NOT NULL,
    cellular_health       INTEGER NOT NULL
);

CREATE TABLE body_training_and_conditioning (
    character_id        BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    vigor                 INTEGER NOT NULL,
    reflexes              INTEGER NOT NULL,
    intensity             INTEGER NOT NULL,
    coordination          INTEGER NOT NULL,
    resilience            INTEGER NOT NULL,
    fighting               INTEGER NOT NULL,
    weapon_practicing      INTEGER NOT NULL,
    shooting               INTEGER NOT NULL
);

-- =============================================================================================
-- Body — wound tree (one row per BodyComponent node, ~45-50 rows/character)
--
-- Only current_hit_points/irreversible_damage are persisted. Every structural field
-- (max_hit_points, natural_resistance, vital, hit_difficulty, cascade_relation, slip_chance,
-- slip_damage_fraction, parent/child shape) is NOT stored here — Body.reconstruct() always
-- rebuilds the anatomical tree procedurally from the human template in code, then overlays
-- these two dynamic fields onto each node, matched by name. Storing the structural fields
-- would be pure redundant duplication (identical for every human character today) with no
-- functional benefit, since nothing reads them back from this table.
-- =============================================================================================

CREATE TABLE body_components (
    id                     BIGSERIAL PRIMARY KEY,
    character_id           BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    name                   VARCHAR(64) NOT NULL,
    current_hit_points     INTEGER NOT NULL,
    irreversible_damage    INTEGER NOT NULL,
    UNIQUE (character_id, name)
);

CREATE INDEX idx_body_components_character_id ON body_components(character_id);

-- =============================================================================================
-- Mind — fixed-field groups
-- =============================================================================================

CREATE TABLE mind_values (
    character_id    BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    ego              INTEGER NOT NULL,
    loyalty          INTEGER NOT NULL,
    organization     INTEGER NOT NULL,
    freedom          INTEGER NOT NULL,
    society          INTEGER NOT NULL,
    divinity         INTEGER NOT NULL,
    truth            INTEGER NOT NULL,
    knowledge        INTEGER NOT NULL,
    nature           INTEGER NOT NULL,
    morality         INTEGER NOT NULL,
    tradition        INTEGER NOT NULL,
    justice          INTEGER NOT NULL,
    progress         INTEGER NOT NULL,
    peace            INTEGER NOT NULL
);

CREATE TABLE mind_general_personality (
    character_id  BIGINT PRIMARY KEY REFERENCES characters(id) ON DELETE CASCADE,
    vanity         INTEGER NOT NULL,
    focus          INTEGER NOT NULL
);

-- =============================================================================================
-- Mind — open enum catalogs (key-value tables, one row per selected/leveled item)
--
-- Deliberately NOT one column per Knowledge/Job/Weapon/Trait constant: these are exactly the
-- catalogs that grow every few days (see keynor-rpg/CLAUDE.md's changelog). A new catalog
-- constant only ever needs new rows here, never a schema change — the app validates the
-- key column (knowledge/job/weapon/trait name) against the current Java enum, not the DB.
-- =============================================================================================

CREATE TABLE mind_erudition_levels (
    character_id  BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    knowledge      VARCHAR(64) NOT NULL,
    level          INTEGER NOT NULL,
    PRIMARY KEY (character_id, knowledge)
);

CREATE TABLE mind_labours_levels (
    character_id  BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    job            VARCHAR(64) NOT NULL,
    level          INTEGER NOT NULL,
    PRIMARY KEY (character_id, job)
);

CREATE TABLE mind_weapon_proficiencies (
    character_id  BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    weapon         VARCHAR(64) NOT NULL,
    level          INTEGER NOT NULL,
    PRIMARY KEY (character_id, weapon)
);

CREATE TABLE mind_selected_traits (
    character_id  BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    trait          VARCHAR(64) NOT NULL,
    PRIMARY KEY (character_id, trait)
);
