package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.ThrownWeaponProfile;

/**
 * Thrown-weapon attack resolution from the Special Attack Test design (2026-07-20). The player
 * rolls an Aim test (Taim) and a Swing Power test (Tsp) via {@link SpecialAttackTestResolver},
 * outside this class.
 */
public class ThrownAttackResolver {

    private static final double BASE_HIT_DIFFICULTY = 45;

    /** {@code Taim > 45 + distance + Whnd}. */
    public boolean resolveHit(double taim, double distanceMeters, ThrownWeaponProfile profile) {
        return taim > BASE_HIT_DIFFICULTY + distanceMeters + profile.getWeaponHandling();
    }

    /**
     * The thrown object loses all its energy and falls to the ground once distance-based
     * degradation ({@code Ded * distance}) exceeds the attack's own force
     * ({@code (Tsp * Mf) + (Taim * Fp)}) — this is the projectile's maximum range.
     */
    public boolean isWithinRange(double tsp, double taim, double distanceMeters, ThrownWeaponProfile profile) {
        return profile.getEnergyDegradationPerMeter() * distanceMeters
                <= tsp * profile.getForceMultiplier() + taim * profile.getProficiencyFactor();
    }

    /** {@code Db = (Tsp * Mf) + (Taim * Fp) - (Ded * distance)}, floored at 0 past maximum range. */
    public double resolveRawDamage(double tsp, double taim, double distanceMeters, ThrownWeaponProfile profile) {
        double db = tsp * profile.getForceMultiplier() + taim * profile.getProficiencyFactor()
                - profile.getEnergyDegradationPerMeter() * distanceMeters;
        return Math.max(0, db);
    }
}
