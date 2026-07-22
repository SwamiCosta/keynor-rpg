package com.keynor.rpg.domain.model;

/**
 * Armor/shield reference table from the Special Attack Test design (2026-07-20) — each entry
 * pairs a {@link Material} (reusing the existing rpg-21 Material Durability catalog for Vb/Md)
 * with a {@code dimension} (Dm), the arbitrary per-equipment multiplier the design calls for:
 * {@code Protection = Material.baseDurability * Material.getMultiplier(damageType) * dimension}
 * — see {@code ProtectionCalculator}. Not yet consumed by any use case, same "reference data"
 * precedent as {@link Material}/{@link MeleeAttackProfile}.
 *
 * <p>{@link #INFANTRY_TOWER_SHIELD}'s source material is listed as "wood with iron" — the
 * {@link Material} catalog has no composite entries, so this uses {@link Material#WOOD} alone;
 * the iron reinforcement is not separately modeled. Revisit if a composite-material mechanic is
 * ever added.
 */
public enum ArmorProtection {

    COMMON_CLOTHES(Material.PADDED_LINEN, 0.2),
    GAMBESON(Material.PADDED_LINEN, 1.2),
    LIGHT_LEATHER_ARMOR(Material.LEATHER, 0.8),
    HARDENED_LEATHER_ARMOR(Material.LEATHER, 1.6),
    CHAINMAIL(Material.IRON, 0.6),
    BRONZE_BREASTPLATE(Material.BRONZE, 1.0),
    STEEL_PLATE_BREASTPLATE(Material.STEEL, 0.8),
    LEGENDARY_PLATE_ARMOR(Material.ADAMANTINE, 0.8),
    WOODEN_BUCKLER(Material.WOOD, 1.5),
    INFANTRY_TOWER_SHIELD(Material.WOOD, 2.5);

    private final Material material;
    private final double dimension;

    ArmorProtection(Material material, double dimension) {
        this.material = material;
        this.dimension = dimension;
    }

    public Material getMaterial() { return material; }
    public double getDimension() { return dimension; }
}
