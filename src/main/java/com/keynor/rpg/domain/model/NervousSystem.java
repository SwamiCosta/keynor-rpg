package com.keynor.rpg.domain.model;

/**
 * Trainable neural layer. {@code neuralDrive} will modulate {@link BodyComposition}'s
 * dominant fiber type once the nervous system is detailed further — not yet wired into
 * body-composition rate formulas. It does feed the {@link SpatialIntelligence} derived
 * attributes ({@link PlayableCharacter#getSight()}, {@link PlayableCharacter#getEvasion()},
 * etc.) and the {@link PlayableCharacter#getStrength()} formula via
 * {@code neuromuscularEfficiency}.
 *
 * <p>{@code neuromuscularEfficiency} (0-1) is the fraction of theoretical force actually
 * usable — the "technique vs. size" axis. Trainable, unlike {@link BloodSystem}.
 */
public class NervousSystem {

    private double neuralDrive;
    private double neuromuscularEfficiency;

    public NervousSystem(double neuralDrive, double neuromuscularEfficiency) {
        this.neuralDrive = neuralDrive;
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }

    public static NervousSystem defaults() {
        return new NervousSystem(5, 0.5);
    }

    public double getNeuralDrive() {
        return neuralDrive;
    }

    public void setNeuralDrive(double neuralDrive) {
        this.neuralDrive = neuralDrive;
    }

    public double getNeuromuscularEfficiency() {
        return neuromuscularEfficiency;
    }

    public void setNeuromuscularEfficiency(double neuromuscularEfficiency) {
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }
}
