package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.BowProfile;

/**
 * Bow attack resolution from the Special Attack Test design (2026-07-20). The player rolls an
 * Aim test (Taim) and a Pull Strength test (Tpst) via {@link SpecialAttackTestResolver}, outside
 * this class.
 */
public class BowAttackResolver {

    private static final double BASE_HIT_DIFFICULTY = 40;

    /**
     * A bow's minimum pull (Tmin) gates whether the character can even draw the string. Fails
     * if {@code Tpst} is not strictly greater than {@code Tmin}.
     */
    public boolean canDrawBow(double tpst, BowProfile profile) {
        return tpst > profile.getMinimumPull();
    }

    /** {@code Taim > 40 + distance}. */
    public boolean resolveHit(double taim, double distanceMeters, BowProfile profile) {
        return taim > BASE_HIT_DIFFICULTY + distanceMeters;
    }

    /**
     * {@code Tpstm = min(Tpst, Tmax)} — a character can never apply more draw force than the
     * bow's own physical limit, regardless of how high their Pull Strength result is.
     */
    public double effectivePullStrength(double tpst, BowProfile profile) {
        return Math.min(tpst, profile.getMaximumPull());
    }

    /**
     * The arrow loses all its energy once distance-based degradation exceeds
     * {@code Tpstm * Mf} — this is the arrow's maximum range.
     */
    public boolean isWithinRange(double tpst, double distanceMeters, BowProfile profile) {
        double tpstm = effectivePullStrength(tpst, profile);
        return profile.getEnergyDegradationPerMeter() * distanceMeters <= tpstm * profile.getForceMultiplier();
    }

    /** {@code Db = (Tpstm * Mf) - (Ded * distance)}, floored at 0 past maximum range. */
    public double resolveRawDamage(double tpst, double distanceMeters, BowProfile profile) {
        double tpstm = effectivePullStrength(tpst, profile);
        double db = tpstm * profile.getForceMultiplier() - profile.getEnergyDegradationPerMeter() * distanceMeters;
        return Math.max(0, db);
    }
}
