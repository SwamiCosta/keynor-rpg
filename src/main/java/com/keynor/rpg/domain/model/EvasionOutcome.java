package com.keynor.rpg.domain.model;

/**
 * The result of a target's optional Evasion reaction against an already-resolved Special Attack
 * Test roll (2026-07-20) — see {@code EvasionResolver}.
 */
public enum EvasionOutcome {
    /** The target's Evasion roll beat the attacker's — the attack misses entirely. */
    EVADED,
    /** A tie — the blow grazes the target; final damage is halved. */
    GRAZING_HIT,
    /** The attacker's roll was higher — the attack lands at full damage. */
    FULL_HIT
}
