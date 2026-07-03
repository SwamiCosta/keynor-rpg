package com.keynor.rpg.domain.model;

import java.util.List;

/**
 * Resolved term-by-term decomposition of one additive-standard attribute formula (Delta V4),
 * backing the frontend's "already-resolved calculation" tooltip format (multiplications and
 * divisions already collapsed into a single number per term). {@code baseline} is usually the
 * shared {@link BodyCoefficients#getBaseline()} (60), except for the four specialized-strength
 * attributes (Push/Leg/Grip/Lift Strength), which use the dynamic, hidden {@code meanStrength()}
 * value as their baseline, and the zero-baseline rate attributes (FatGainRate/MuscleGainRate),
 * which use {@code 0}. {@link #total()} is the same value the corresponding {@code getXxx()}
 * method returns before any floor is applied.
 */
public record AttributeBreakdown(double baseline, List<Double> terms) {

    public double total() {
        double sum = baseline;
        for (double term : terms) {
            sum += term;
        }
        return sum;
    }
}
