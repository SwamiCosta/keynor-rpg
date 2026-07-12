package com.keynor.rpg.domain.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Material Durability catalog (rpg-21) — a reference table of physical materials, each with a
 * base durability and a per-{@link DamageType} multiplier. Not consumed by any use case or
 * combat mechanic yet; saved as domain reference data for a future strikes-against-objects
 * mechanic. Constants are ordered by ascending {@code baseDurability}, matching the design
 * document's own table order.
 */
public enum Material {
    MUSCLE_TISSUE(10,
            0.5, 0.25, 1.5, 0.5, 0.25, 0.5, 0.25, 0.25, 1.0),
    GLASS_OBSIDIAN(20,
            0.25, 2.0, 0.25, 0.5, 1.5, 1.0, 2.0, 2.0, 0.25),
    PADDED_LINEN(25,
            1.0, 0.5, 2.0, 0.5, 0.25, 1.5, 0.5, 1.0, 2.0),
    GOLD(30,
            0.5, 1.0, 0.5, 0.5, 0.5, 1.0, 2.0, 1.0, 0.25),
    SILVER(40,
            0.5, 1.25, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 0.25),
    LEATHER(45,
            1.0, 1.5, 1.5, 1.0, 0.5, 1.0, 0.5, 1.5, 1.5),
    CRYSTAL(50,
            0.5, 2.0, 0.25, 0.5, 1.5, 1.0, 1.5, 2.0, 0.5),
    WOOD(55,
            0.5, 2.0, 1.5, 1.0, 0.25, 1.0, 1.0, 1.0, 0.5),
    BONE(60,
            1.0, 2.0, 0.5, 1.0, 0.5, 1.0, 0.5, 2.0, 0.25),
    ALUMINUM(65,
            1.0, 1.5, 0.5, 0.5, 0.5, 1.0, 0.5, 1.0, 0.5),
    KERATIN_CARAPACE(75,
            1.5, 2.0, 1.0, 1.5, 0.5, 1.0, 0.5, 2.0, 1.0),
    COPPER(80,
            1.0, 1.5, 0.5, 1.0, 0.5, 1.0, 1.0, 1.5, 0.5),
    STONE(90,
            1.5, 2.0, 0.5, 2.0, 2.0, 0.5, 1.0, 2.0, 2.0),
    BRONZE(120,
            1.5, 2.0, 0.5, 1.5, 0.5, 1.0, 1.0, 2.0, 0.5),
    IRON(160,
            1.5, 2.0, 1.0, 1.5, 0.5, 1.0, 0.25, 2.0, 1.0),
    COBALT(200,
            1.5, 2.0, 0.5, 1.5, 1.0, 1.0, 1.0, 2.0, 1.0),
    STEEL(240,
            1.5, 2.0, 0.5, 1.5, 0.5, 1.0, 0.5, 2.0, 1.0),
    TITANIUM(280,
            1.5, 2.0, 1.0, 2.0, 1.0, 1.0, 2.0, 2.0, 1.5),
    MITHRIL(350,
            2.0, 2.0, 1.0, 2.0, 1.5, 1.5, 1.5, 2.0, 1.5),
    DIAMOND(400,
            2.0, 2.0, 0.25, 2.0, 2.0, 2.0, 2.0, 2.0, 0.5),
    ADAMANTINE(500,
            2.0, 2.0, 1.5, 2.0, 1.5, 1.5, 1.5, 2.0, 2.0);

    private final double baseDurability;
    private final Map<DamageType, Double> multipliers;

    Material(double baseDurability, double chop, double slice, double blunt, double piercing,
             double burning, double frost, double corrosive, double tear, double compress) {
        this.baseDurability = baseDurability;
        Map<DamageType, Double> map = new EnumMap<>(DamageType.class);
        map.put(DamageType.CHOP, chop);
        map.put(DamageType.SLICE, slice);
        map.put(DamageType.BLUNT, blunt);
        map.put(DamageType.PIERCING, piercing);
        map.put(DamageType.BURNING, burning);
        map.put(DamageType.FROST, frost);
        map.put(DamageType.CORROSIVE, corrosive);
        map.put(DamageType.TEAR, tear);
        map.put(DamageType.COMPRESS, compress);
        this.multipliers = map;
    }

    public double getBaseDurability() {
        return baseDurability;
    }

    public double getMultiplier(DamageType damageType) {
        return multipliers.get(damageType);
    }
}
