package com.keynor.rpg.domain.model;

/**
 * Trainable. Together with {@link CardiacSystem} and {@link BloodSystem}, will be the
 * basis for a future derived cardiovascular capacity formula — not yet implemented.
 */
public class PulmonarySystem {

    private double pulmonaryCapacity;

    public PulmonarySystem(double pulmonaryCapacity) {
        this.pulmonaryCapacity = pulmonaryCapacity;
    }

    public static PulmonarySystem defaults() {
        return new PulmonarySystem(5);
    }

    public double getPulmonaryCapacity() {
        return pulmonaryCapacity;
    }

    public void setPulmonaryCapacity(double pulmonaryCapacity) {
        this.pulmonaryCapacity = pulmonaryCapacity;
    }
}
