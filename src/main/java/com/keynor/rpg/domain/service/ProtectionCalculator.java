package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.ArmorProtection;
import com.keynor.rpg.domain.model.DamageType;
import com.keynor.rpg.domain.model.Material;

/**
 * {@code Protection (P) = Vb * Md * Dm} — a struck object's resistance to one incoming hit,
 * from the Special Attack Test design (2026-07-20). {@code Vb} (Material Base Value) and
 * {@code Md} (Material's per-{@link DamageType} modifier) come straight from the existing
 * rpg-21 {@link Material} catalog; {@code Dm} (Material Dimension) is the new arbitrary
 * per-equipment multiplier carried by {@link ArmorProtection}.
 */
public class ProtectionCalculator {

    public double calculate(ArmorProtection armor, DamageType damageType) {
        Material material = armor.getMaterial();
        return material.getBaseDurability() * material.getMultiplier(damageType) * armor.getDimension();
    }
}
