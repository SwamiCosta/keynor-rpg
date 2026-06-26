package com.keynor.rpg.domain.model;

/**
 * Placeholder for the future nervous system model. {@code neuralDrive} will modulate
 * {@link BodyComposition}'s dominant fiber type once the nervous system is detailed
 * further — not yet wired into any formula. Trainable, unlike {@link BloodSystem}.
 */
public class NervousSystem {

    private double neuralDrive;

    public NervousSystem(double neuralDrive) {
        this.neuralDrive = neuralDrive;
    }

    public static NervousSystem defaults() {
        return new NervousSystem(5);
    }

    public double getNeuralDrive() {
        return neuralDrive;
    }

    public void setNeuralDrive(double neuralDrive) {
        this.neuralDrive = neuralDrive;
    }
}
