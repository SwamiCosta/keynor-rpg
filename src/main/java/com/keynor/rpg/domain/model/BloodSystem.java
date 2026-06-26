package com.keynor.rpg.domain.model;

/**
 * Genetic — fixed once the character is created, unlike {@link CardiacSystem} and
 * {@link PulmonarySystem}. No setters: same immutability rule as {@link Genetics}.
 */
public class BloodSystem {

    private final double oxygenCarryingCapacity;

    public BloodSystem(double oxygenCarryingCapacity) {
        this.oxygenCarryingCapacity = oxygenCarryingCapacity;
    }

    public static BloodSystem defaults() {
        return new BloodSystem(5);
    }

    public double getOxygenCarryingCapacity() {
        return oxygenCarryingCapacity;
    }
}
