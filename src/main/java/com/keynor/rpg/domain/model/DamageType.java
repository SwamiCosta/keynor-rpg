package com.keynor.rpg.domain.model;

/**
 * A category of damage a {@link Material} can be struck with. Backs the Material Durability
 * catalog (rpg-21) — not yet consumed by any combat mechanic; reserved as reference data for
 * future strikes against objects.
 */
public enum DamageType {
    CHOP,
    SLICE,
    BLUNT,
    PIERCING,
    BURNING,
    FROST,
    CORROSIVE,
    TEAR,
    COMPRESS
}
