package com.keynor.rpg.domain.model;

/**
 * Trainable. Additive-standard discrete scale (rpg-11): 1-9, neutral 5 (formulas use the
 * {@code value - 5} deviation).
 */
public class PulmonarySystem {

    private int pulmonaryCapacity;

    public PulmonarySystem(int pulmonaryCapacity) {
        this.pulmonaryCapacity = pulmonaryCapacity;
    }

    public static PulmonarySystem defaults() {
        return new PulmonarySystem(5);
    }

    public int getPulmonaryCapacity() {
        return pulmonaryCapacity;
    }

    public void setPulmonaryCapacity(int pulmonaryCapacity) {
        this.pulmonaryCapacity = pulmonaryCapacity;
    }
}
