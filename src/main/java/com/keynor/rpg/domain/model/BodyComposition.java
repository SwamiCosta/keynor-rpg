package com.keynor.rpg.domain.model;

/**
 * Trainable layer of physical body composition — changes through training/diet over the
 * course of the game, unlike {@link Genetics}.
 *
 * <p>Additive-standard discrete scales (rpg-11): {@code dominantFiberType},
 * {@code muscleDistribution}, {@code flexibility} are 1-9 with neutral 5 (deviation
 * {@code value - 5}). {@code muscleMass} is 1-15 with neutral 5, representing 10kg-80kg
 * in 5kg steps; {@code bodyFat} is 1-10 with neutral 3, representing 5kg-50kg in 5kg
 * steps — both used directly (not as deviations) inside
 * {@link PlayableCharacter#getSymbolicTotalMass()}/{@link PlayableCharacter#getDisplayMassKg()},
 * and {@code muscleMass} is additionally used as a {@code value - 5} deviation inside
 * {@link PlayableCharacter#getStrength()}/{@link PlayableCharacter#getSpeed()}.
 *
 * <p>{@code muscleDistribution} is a leg-biased (low) to arm-biased (high) axis around the
 * balanced midpoint (5) — trainable, unlike the somatotype axes in {@link Genetics}.
 */
public class BodyComposition {

    private int bodyFat;
    private int muscleMass;
    private int dominantFiberType;
    private int muscleDistribution;
    private int flexibility;

    public BodyComposition(int bodyFat, int muscleMass, int dominantFiberType,
                            int muscleDistribution, int flexibility) {
        this.bodyFat = bodyFat;
        this.muscleMass = muscleMass;
        this.dominantFiberType = dominantFiberType;
        this.muscleDistribution = muscleDistribution;
        this.flexibility = flexibility;
    }

    public static BodyComposition defaults() {
        return new BodyComposition(3, 5, 5, 5, 5);
    }

    public int getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(int bodyFat) {
        this.bodyFat = bodyFat;
    }

    public int getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(int muscleMass) {
        this.muscleMass = muscleMass;
    }

    public int getDominantFiberType() {
        return dominantFiberType;
    }

    public void setDominantFiberType(int dominantFiberType) {
        this.dominantFiberType = dominantFiberType;
    }

    public int getMuscleDistribution() {
        return muscleDistribution;
    }

    public void setMuscleDistribution(int muscleDistribution) {
        this.muscleDistribution = muscleDistribution;
    }

    public int getFlexibility() {
        return flexibility;
    }

    public void setFlexibility(int flexibility) {
        this.flexibility = flexibility;
    }
}
