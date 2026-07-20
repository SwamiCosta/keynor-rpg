package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.FirearmProfile;

/**
 * Trigger-weapon (crossbow/firearm) attack resolution from the Special Attack Test design
 * (2026-07-20). The player rolls only an Aim test (Taim) via {@link SpecialAttackTestResolver}
 * — no Strength attribute is involved, since the weapon's own mechanism does the work.
 */
public class FirearmAttackResolver {

    private static final double BASE_HIT_DIFFICULTY = 40;

    /** {@code Taim > Whnd + 40 + distance}. */
    public boolean resolveHit(double taim, double distanceMeters, FirearmProfile profile) {
        return taim > profile.getWeaponHandling() + BASE_HIT_DIFFICULTY + distanceMeters;
    }

    /**
     * The shot loses all its energy once distance-based degradation ({@code Ded * distance})
     * exceeds the weapon's own damage ({@code Wdmg}) — this is the maximum range. Modern
     * firearms ({@code energyDegradationPerMeter == 0}) never fail this check.
     */
    public boolean isWithinRange(double distanceMeters, FirearmProfile profile) {
        return profile.getEnergyDegradationPerMeter() * distanceMeters <= profile.getWeaponDamage();
    }

    /** {@code Db = Wdmg - (Ded * distance)}, floored at 0 past maximum range. */
    public double resolveRawDamage(double distanceMeters, FirearmProfile profile) {
        double db = profile.getWeaponDamage() - profile.getEnergyDegradationPerMeter() * distanceMeters;
        return Math.max(0, db);
    }
}
