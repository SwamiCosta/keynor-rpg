package com.keynor.rpg.domain.model;

import java.util.List;

/**
 * Resolved term-by-term decomposition of one additive-standard attribute formula, backing the
 * frontend's tooltip format ({@code <description> / Affected By: / Base Value: X / <Label>: Y /
 * ...}). {@code baseline} is usually the shared {@link BodyCoefficients#getBaseline()} (60),
 * except for the four specialized-strength attributes (Push/Leg/Grip/Lift Strength), which use
 * the dynamic, hidden {@code meanStrength()} value as their baseline, and the zero-baseline rate
 * attributes (FatGainRate/MuscleGainRate), which use {@code 0}. {@link #total()} is the same
 * value the corresponding {@code getXxx()} method returns before any floor is applied.
 *
 * <p>Every {@link Term} carries a player-facing label (rpg-21) — this is what lets the frontend
 * render each term without a second, hand-maintained positional label map that could drift out
 * of sync with the backend's own term order.
 */
public record AttributeBreakdown(double baseline, List<Term> terms) {

    /** One labeled, already-resolved term in a breakdown (rpg-21). */
    public record Term(String label, double value) {
    }

    public double total() {
        double sum = baseline;
        for (Term term : terms) {
            sum += term.value();
        }
        return sum;
    }
}
