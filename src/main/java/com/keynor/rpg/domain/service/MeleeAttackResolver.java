package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.MeleeAttackProfile;

/**
 * Melee attack resolution from the Special Attack Test design (2026-07-20). The player rolls a
 * Melee Dexterity test (Tmd) and a Strength test (Tstr, using whichever of Leg Drive/Upper
 * Strike/Swing Power the {@link MeleeAttackProfile#getForceAttribute()} calls for) — both via
 * {@link SpecialAttackTestResolver}, outside this class. This class only interprets the two
 * already-rolled results.
 */
public class MeleeAttackResolver {

    private static final double HIT_THRESHOLD = 40;
    private static final double LONG_HAFTED_HIT_THRESHOLD = 55;

    /**
     * A Tmd result at or below the threshold (40, or 55 for long-hafted weapons like spears and
     * halberds) is a miss. This makes it effectively impossible to miss an adjacent target
     * unless severely debilitated.
     */
    public boolean resolveHit(double tmd, MeleeAttackProfile profile) {
        double threshold = profile.isLongHafted() ? LONG_HAFTED_HIT_THRESHOLD : HIT_THRESHOLD;
        return tmd > threshold;
    }

    /** {@code Db = (Tstr * Mf) + (Tmd * Fp)}. */
    public double resolveRawDamage(double tstr, double tmd, MeleeAttackProfile profile) {
        return tstr * profile.getForceMultiplier() + tmd * profile.getProficiencyFactor();
    }
}
