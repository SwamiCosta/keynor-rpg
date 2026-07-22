package com.keynor.rpg.domain.model;

/**
 * Bow reference table from the Special Attack Test design (2026-07-20). {@code minimumPull}
 * (Tmin) gates whether the character can draw the bow at all; {@code maximumPull} (Tmax) caps
 * how much of the character's own Pull Strength actually counts toward damage — see
 * {@code BowAttackResolver}'s {@code Tpstm = min(Tpst, Tmax)} rule (confirmed by the user
 * 2026-07-20, correcting the design doc's internally-inconsistent "highest value" wording — a
 * character can never apply more force than the bow's own physical draw limit). Not yet consumed
 * by any use case, same "reference data" precedent as {@link Material}/{@link MeleeAttackProfile}.
 */
public enum BowProfile {

    SHORT_BOW(DamageType.PIERCING, 50, 62, 3.5, 1.44),
    RECURVE_BOW(DamageType.PIERCING, 54, 68, 4.5, 1.22),
    COMPOSITE_BOW(DamageType.PIERCING, 48, 65, 6.0, 1.22),
    LONGBOW(DamageType.PIERCING, 58, 75, 5.5, 1.1),
    FLIGHT_BOW(DamageType.PIERCING, 40, 55, 3.0, 0.35);

    private final DamageType damageType;
    private final double minimumPull;
    private final double maximumPull;
    private final double forceMultiplier;
    private final double energyDegradationPerMeter;

    BowProfile(DamageType damageType, double minimumPull, double maximumPull, double forceMultiplier,
               double energyDegradationPerMeter) {
        this.damageType = damageType;
        this.minimumPull = minimumPull;
        this.maximumPull = maximumPull;
        this.forceMultiplier = forceMultiplier;
        this.energyDegradationPerMeter = energyDegradationPerMeter;
    }

    public DamageType getDamageType() { return damageType; }
    public double getMinimumPull() { return minimumPull; }
    public double getMaximumPull() { return maximumPull; }
    public double getForceMultiplier() { return forceMultiplier; }
    public double getEnergyDegradationPerMeter() { return energyDegradationPerMeter; }
}
