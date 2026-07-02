package com.keynor.rpg.domain.model;

/**
 * Genetic — fixed once the character is created, unlike {@link CardiacSystem} and
 * {@link PulmonarySystem}. No setters: same immutability rule as {@link Genetics}.
 *
 * <p>Additive-standard discrete scale (rpg-11): 1-9, neutral 5 (formulas use the
 * {@code value - 5} deviation).
 */
public class BloodSystem {

    private final int oxygenCarryingCapacity;

    public BloodSystem(int oxygenCarryingCapacity) {
        this.oxygenCarryingCapacity = oxygenCarryingCapacity;
    }

    public static BloodSystem defaults() {
        return new BloodSystem(5);
    }

    public int getOxygenCarryingCapacity() {
        return oxygenCarryingCapacity;
    }
}
