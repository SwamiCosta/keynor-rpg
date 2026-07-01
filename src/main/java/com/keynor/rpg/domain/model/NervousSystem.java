package com.keynor.rpg.domain.model;

/**
 * Trainable neural layer. Feeds the {@link SpatialIntelligence}-derived attributes
 * ({@link PlayableCharacter#getSight()}, {@link PlayableCharacter#getEvasion()}, etc.)
 * and the {@link PlayableCharacter#getStrength()}/{@link PlayableCharacter#getSpeed()}
 * formulas via {@code neuromuscularEfficiency}.
 *
 * <p>Additive-standard discrete scale (rpg-11): both fields are 1-9, neutral 5 (formulas
 * use the {@code value - 5} deviation). {@code neuromuscularEfficiency} moved from a 0-1
 * float to this same 1-9 int scale as part of rpg-11's full-scale standardization.
 */
public class NervousSystem {

    private int neuralDrive;
    private int neuromuscularEfficiency;

    public NervousSystem(int neuralDrive, int neuromuscularEfficiency) {
        this.neuralDrive = neuralDrive;
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }

    public static NervousSystem defaults() {
        return new NervousSystem(5, 5);
    }

    public int getNeuralDrive() {
        return neuralDrive;
    }

    public void setNeuralDrive(int neuralDrive) {
        this.neuralDrive = neuralDrive;
    }

    public int getNeuromuscularEfficiency() {
        return neuromuscularEfficiency;
    }

    public void setNeuromuscularEfficiency(int neuromuscularEfficiency) {
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }
}
