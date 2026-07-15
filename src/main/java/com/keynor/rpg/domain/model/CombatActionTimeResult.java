package com.keynor.rpg.domain.model;

/**
 * {@code ut} is the final, whole-UT cost ({@code max(1, floor(utBase × (60 / score)))});
 * {@code score} is the resolved attribute score (S) it was derived from — exposed for future
 * frontend tooltip use, mirroring why {@link AttributeBreakdown} exposes its resolved terms
 * rather than just the final attribute value.
 */
public record CombatActionTimeResult(int ut, double score) {
}
