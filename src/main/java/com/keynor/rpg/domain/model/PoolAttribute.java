package com.keynor.rpg.domain.model;

/**
 * A "pool" attribute — one that carries both a {@code total} capacity and a {@code current}
 * remaining amount, tracked as two genuinely separate numbers rather than a single computed
 * value. {@code total} is always the attribute's own additive-standard formula result (same
 * computation as before this concept existed). {@code current} always equals {@code total}
 * today, since no spend/damage/rest mechanic exists yet to deplete or restore a pool below its
 * total — the two fields are kept distinct in the domain and the REST contract specifically so
 * a future mechanic can make them diverge without another breaking contract change, the same
 * "document intent, not yet implemented" precedent already used elsewhere in this codebase
 * (e.g. {@code BodyComponent}'s reversible-damage regeneration).
 */
public record PoolAttribute(double total, double current) {

    public static PoolAttribute atFull(double total) {
        return new PoolAttribute(total, total);
    }
}
