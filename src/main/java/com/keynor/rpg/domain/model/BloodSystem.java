package com.keynor.rpg.domain.model;

/**
 * Genetic — fixed once the character is created, unlike {@link CardiacSystem} and
 * {@link PulmonarySystem}. No setters: same immutability rule as {@link Genetics}.
 *
 * <p>Additive-standard discrete scale (rpg-11): {@code oxygenCarryingCapacity} is 1-9,
 * neutral 5. {@code bloodThickness} (added rpg-13) is 1-5, neutral 3 — a narrower range
 * than most traits since blood viscosity varies less than other physiological axes.
 * Immutable for now like every other field here; if a future revision needs it trainable,
 * flip it the same way {@link BodyComposition} differs from {@link Genetics} — drop
 * {@code final}, add a setter, no other structural change required.
 */
public class BloodSystem {

    private final int oxygenCarryingCapacity;
    private final int bloodThickness;

    public BloodSystem(int oxygenCarryingCapacity, int bloodThickness) {
        this.oxygenCarryingCapacity = oxygenCarryingCapacity;
        this.bloodThickness = bloodThickness;
    }

    public static BloodSystem defaults() {
        return new BloodSystem(5, 3);
    }

    public int getOxygenCarryingCapacity() {
        return oxygenCarryingCapacity;
    }

    public int getBloodThickness() {
        return bloodThickness;
    }
}
