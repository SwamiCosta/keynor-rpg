package com.keynor.rpg.domain.model;

/**
 * Trigger-weapon reference table from the Special Attack Test design (2026-07-20) — mechanical-
 * release weapons (crossbows and firearms), grouped together because neither rolls a Strength
 * attribute at the moment of firing (unlike bows, which roll Pull Strength every shot). Modern
 * firearms have no energy degradation over distance ({@code energyDegradationPerMeter = 0} for
 * the source table's {@code "-"} entries) — their listed maximum range is "1000+m", effectively
 * unlimited for this game's scale. Not yet consumed by any use case, same "reference data"
 * precedent as {@link Material}/{@link MeleeAttackProfile}/{@link ThrownWeaponProfile}.
 */
public enum FirearmProfile {

    HAND_CROSSBOW(DamageType.PIERCING, 220, 1.0, 2),
    HEAVY_CROSSBOW(DamageType.PIERCING, 450, 1.0, 5),
    BLACK_POWDER_PISTOL(DamageType.PIERCING, 300, 1.67, 4),
    HEAVY_MUSKET(DamageType.PIERCING, 500, 1.85, 8),

    MODERN_9MM_PISTOL(DamageType.PIERCING, 350, 0, 2),
    REVOLVER_357_MAGNUM(DamageType.PIERCING, 480, 0, 3),
    ASSAULT_RIFLE_556(DamageType.PIERCING, 550, 0, 4),
    SNIPER_RIFLE_308(DamageType.PIERCING, 750, 0, 6),
    SHOTGUN_BUCKSHOT(DamageType.PIERCING, 700, 3.68, 5);

    private final DamageType damageType;
    private final double weaponDamage;
    private final double energyDegradationPerMeter;
    private final double weaponHandling;

    FirearmProfile(DamageType damageType, double weaponDamage, double energyDegradationPerMeter,
                   double weaponHandling) {
        this.damageType = damageType;
        this.weaponDamage = weaponDamage;
        this.energyDegradationPerMeter = energyDegradationPerMeter;
        this.weaponHandling = weaponHandling;
    }

    public DamageType getDamageType() { return damageType; }
    public double getWeaponDamage() { return weaponDamage; }
    public double getEnergyDegradationPerMeter() { return energyDegradationPerMeter; }
    public double getWeaponHandling() { return weaponHandling; }
}
