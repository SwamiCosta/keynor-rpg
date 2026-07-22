package com.keynor.rpg.domain.model;

/**
 * The melee weapon/attack-type reference table from the Special Attack Test design (2026-07-20):
 * one constant per (weapon, attack type) combination, since a single weapon can offer several
 * distinct attacks with their own damage type and coefficients (e.g. a longsword can Chop,
 * Thrust, or Slice). Not yet consumed by any use case — reference data for a future attack-
 * resolution endpoint, same "saved, not wired up yet" precedent as {@link Material}.
 *
 * <p>{@code forceAttribute} mapping confirmed by the user (2026-07-20): every "Estocada"
 * (thrust) attack type reads {@link MeleeForceAttribute#UPPER_STRIKE}; every other attack type
 * (chop, slice, crush, tear, lash, etc.) reads {@link MeleeForceAttribute#SWING_POWER}. No entry
 * in this table uses {@link MeleeForceAttribute#LEG_DRIVE}.
 *
 * <p>{@code longHafted} marks the polearm-family weapons (long spear, trident, naginata, war
 * scythe, halberd) whose hit-check threshold is 55 instead of the usual 40 — see
 * {@code special-attack-test.md}'s "Melee attacks" section.
 */
public enum MeleeAttackProfile {

    UNARMED_STRIKE(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 1.0, 0.0, 0.5, false),

    DAGGER_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 1.8, 0.8, 0.6, false),
    DAGGER_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 1.4, 1.0, 0.85, false),

    SHORT_SWORD_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 1.9, 1.0, 0.9, false),
    SHORT_SWORD_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 1.9, 1.2, 0.65, false),
    SHORT_SWORD_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 2.0, 1.0, 1.05, false),

    LONGSWORD_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 2.9, 1.4, 0.7, false),
    LONGSWORD_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 2.8, 1.2, 1.25, false),
    LONGSWORD_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 2.4, 1.6, 1.6, false),

    RAPIER_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 1.8, 1.7, 0.35, false),
    RAPIER_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 1.2, 1.5, 0.8, false),

    SCIMITAR_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 1.6, 2.2, 1.3, false),

    BATTLE_AXE_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 4.0, 0.8, 1.8, false),

    CLUB_CRUSH(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 1.6, 0.5, 1.0, false),

    WAR_HAMMER_CRUSH(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 4.2, 0.6, 1.3, false),

    WHIP_LASH(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 0.5, 1.8, 0.3, false),

    LONG_SPEAR_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 3.5, 1.0, 0.75, true),

    BRASS_KNUCKLES_STRIKE(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 1.3, 0.1, 0.55, false),

    STILETTO_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 1.0, 1.3, 0.25, false),

    ESTOC_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 2.6, 1.0, 0.3, false),

    KAMA_TEAR(DamageType.TEAR, MeleeForceAttribute.SWING_POWER, 2.0, 1.0, 0.8, false),

    SAI_STRIKE(DamageType.PIERCING, MeleeForceAttribute.SWING_POWER, 0.8, 0.6, 0.4, false),

    TONFA_STRIKE(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 1.4, 0.5, 0.7, false),

    NUNCHAKU_STRIKE(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 1.2, 1.0, 0.65, false),

    COMBAT_STAFF_STRIKE(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 2.0, 0.8, 0.9, false),

    KATANA_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 2.0, 1.8, 1.15, false),
    KATANA_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 2.6, 1.0, 1.2, false),

    TRIDENT_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 3.1, 1.0, 0.9, true),

    WAR_PICK_PIERCE(DamageType.PIERCING, MeleeForceAttribute.SWING_POWER, 4.2, 0.5, 0.45, false),
    WAR_PICK_CRUSH(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 4.0, 0.8, 0.9, false),

    NAGINATA_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 2.6, 1.7, 1.35, true),

    WAR_FLAIL_STRIKE(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 3.4, 1.0, 1.4, false),

    WAR_SCYTHE_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 3.0, 1.5, 1.5, true),

    HALBERD_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 3.8, 1.2, 1.6, true),
    HALBERD_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 3.0, 1.2, 0.65, true),

    MACE_CRUSH(DamageType.BLUNT, MeleeForceAttribute.SWING_POWER, 3.4, 0.6, 1.1, false),

    BASTARD_SWORD_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 3.3, 1.1, 0.85, false),
    BASTARD_SWORD_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 3.0, 1.3, 1.15, false),

    GREAT_SWORD_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 3.9, 1.0, 1.5, false),

    FALCHION_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 3.0, 0.9, 1.5, false),

    CUTLASS_SLICE(DamageType.SLICE, MeleeForceAttribute.SWING_POWER, 2.1, 1.9, 1.2, false),
    CUTLASS_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 2.6, 1.1, 1.3, false),

    WAR_SICKLE_TEAR(DamageType.TEAR, MeleeForceAttribute.SWING_POWER, 3.9, 0.8, 1.0, false),

    HATCHET_CHOP(DamageType.CHOP, MeleeForceAttribute.SWING_POWER, 2.6, 0.7, 1.5, false),

    SHORT_SPEAR_THRUST(DamageType.PIERCING, MeleeForceAttribute.UPPER_STRIKE, 2.2, 1.0, 0.7, false);

    private final DamageType damageType;
    private final MeleeForceAttribute forceAttribute;
    private final double forceMultiplier;
    private final double proficiencyFactor;
    private final double areaOfEffect;
    private final boolean longHafted;

    MeleeAttackProfile(DamageType damageType, MeleeForceAttribute forceAttribute, double forceMultiplier,
                        double proficiencyFactor, double areaOfEffect, boolean longHafted) {
        this.damageType = damageType;
        this.forceAttribute = forceAttribute;
        this.forceMultiplier = forceMultiplier;
        this.proficiencyFactor = proficiencyFactor;
        this.areaOfEffect = areaOfEffect;
        this.longHafted = longHafted;
    }

    public DamageType getDamageType() { return damageType; }
    public MeleeForceAttribute getForceAttribute() { return forceAttribute; }
    public double getForceMultiplier() { return forceMultiplier; }
    public double getProficiencyFactor() { return proficiencyFactor; }
    public double getAreaOfEffect() { return areaOfEffect; }
    public boolean isLongHafted() { return longHafted; }
}
