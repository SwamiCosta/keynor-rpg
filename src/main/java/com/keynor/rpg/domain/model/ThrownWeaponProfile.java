package com.keynor.rpg.domain.model;

/**
 * Thrown-weapon reference table from the Special Attack Test design (2026-07-20). {@code Ded}
 * (Energy Degradation per Distance) is what makes the attack lose all its energy past a certain
 * range — see {@code ThrownAttackResolver}. Not yet consumed by any use case, same "reference
 * data" precedent as {@link Material}/{@link MeleeAttackProfile}.
 */
public enum ThrownWeaponProfile {

    THROWING_KNIFE(DamageType.PIERCING, 1.2, 0.8, 17.1, 0),
    THROWING_HATCHET(DamageType.CHOP, 2.0, 0.6, 15.6, 1),
    JAVELIN(DamageType.PIERCING, 3.0, 0.8, 11.4, 2),
    THROWN_ROCK(DamageType.BLUNT, 1.0, 0.2, 3.8, 3);

    private final DamageType damageType;
    private final double forceMultiplier;
    private final double proficiencyFactor;
    private final double energyDegradationPerMeter;
    private final double weaponHandling;

    ThrownWeaponProfile(DamageType damageType, double forceMultiplier, double proficiencyFactor,
                         double energyDegradationPerMeter, double weaponHandling) {
        this.damageType = damageType;
        this.forceMultiplier = forceMultiplier;
        this.proficiencyFactor = proficiencyFactor;
        this.energyDegradationPerMeter = energyDegradationPerMeter;
        this.weaponHandling = weaponHandling;
    }

    public DamageType getDamageType() { return damageType; }
    public double getForceMultiplier() { return forceMultiplier; }
    public double getProficiencyFactor() { return proficiencyFactor; }
    public double getEnergyDegradationPerMeter() { return energyDegradationPerMeter; }
    public double getWeaponHandling() { return weaponHandling; }
}
