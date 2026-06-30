package com.keynor.rpg.domain.model;

/**
 * Trainable layer of physical body composition — changes through training/diet over the
 * course of the game, unlike {@link Genetics}. {@code bodyFat} (kg) is intentionally a
 * value the player generally wants to lower, not raise. {@code totalMass} is not stored
 * here — it is derived on {@link PlayableCharacter}.
 *
 * <p>{@code muscleDistribution} is a leg-biased (low) to arm-biased (high) axis around a
 * balanced midpoint (5). Unlike the somatotype axes in {@link Genetics}, it is trainable —
 * the distribution of muscle mass between upper and lower body changes through targeted
 * training. It feeds {@link PlayableCharacter#getStrength()} and
 * {@link PlayableCharacter#getMaxMovementSpeed()} directly.
 *
 * <p>{@code flexibility} (0-10) models how elastic the muscles are. Higher values increase
 * agility-based outputs but reduce {@link PlayableCharacter#getDurability()} — a trade-off
 * encoded by {@code kFlexibilityDurability} in {@link BodyCoefficients}.
 */
public class BodyComposition {

    private double bodyFat;
    private double muscleMass;
    private double dominantFiberType;
    private double muscleDistribution;
    private double flexibility;

    public BodyComposition(double bodyFat, double muscleMass, double dominantFiberType,
                            double muscleDistribution, double flexibility) {
        this.bodyFat = bodyFat;
        this.muscleMass = muscleMass;
        this.dominantFiberType = dominantFiberType;
        this.muscleDistribution = muscleDistribution;
        this.flexibility = flexibility;
    }

    public static BodyComposition defaults() {
        return new BodyComposition(14, 30, 0.0, 5.0, 5.0);
    }

    public double getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(double bodyFat) {
        this.bodyFat = bodyFat;
    }

    public double getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(double muscleMass) {
        this.muscleMass = muscleMass;
    }

    public double getDominantFiberType() {
        return dominantFiberType;
    }

    public void setDominantFiberType(double dominantFiberType) {
        this.dominantFiberType = dominantFiberType;
    }

    public double getMuscleDistribution() {
        return muscleDistribution;
    }

    public void setMuscleDistribution(double muscleDistribution) {
        this.muscleDistribution = muscleDistribution;
    }

    public double getFlexibility() {
        return flexibility;
    }

    public void setFlexibility(double flexibility) {
        this.flexibility = flexibility;
    }
}
