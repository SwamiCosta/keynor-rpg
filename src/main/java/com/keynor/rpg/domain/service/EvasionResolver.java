package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.EvasionOutcome;

/**
 * The optional Evasion reaction against an attack (2026-07-20 clarification of
 * {@code game-rules.md}'s "Resolving a hit's damage value" section). A target may only attempt
 * this if they are at rest, per the pre-existing "Reactions" rule (their combat clock is at or
 * below the attacker's) — that eligibility check is a combat-timing concern and lives outside
 * this class. If the target evades, they roll a Special Attack Test on their own Evasion
 * attribute ({@link SpecialAttackTestResolver#roll(double)}) and it is compared directly against
 * the attacker's already-resolved attack-test result (Tmd/Taim/Tpst-derived) — not a fresh
 * attacker roll.
 */
public class EvasionResolver {

    public EvasionOutcome resolve(double attackTestResult, double evasionTestResult) {
        if (evasionTestResult > attackTestResult) {
            return EvasionOutcome.EVADED;
        }
        if (evasionTestResult == attackTestResult) {
            return EvasionOutcome.GRAZING_HIT;
        }
        return EvasionOutcome.FULL_HIT;
    }

    /** Applies an {@link EvasionOutcome} to an already-resolved final damage value. */
    public double applyToFinalDamage(double finalDamage, EvasionOutcome outcome) {
        return switch (outcome) {
            case EVADED -> 0;
            case GRAZING_HIT -> finalDamage / 2;
            case FULL_HIT -> finalDamage;
        };
    }
}
