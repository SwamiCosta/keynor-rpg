package com.keynor.rpg.domain.model;

/**
 * Trainable layer of physical attributes — changes through training/diet over the course
 * of the game, unlike {@link Genetics}. {@code bodyFatPercentage} is intentionally a value
 * the player generally wants to lower, not raise.
 */
public class BodyComposition {

    private double totalMass;
    private double bodyFatPercentage;
    private double muscleMass;
    private double dominantFiberType;
    private double neuromuscularEfficiency;

    public BodyComposition(double totalMass, double bodyFatPercentage, double muscleMass,
                            double dominantFiberType, double neuromuscularEfficiency) {
        this.totalMass = totalMass;
        this.bodyFatPercentage = bodyFatPercentage;
        this.muscleMass = muscleMass;
        this.dominantFiberType = dominantFiberType;
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }

    public static BodyComposition defaults() {
        return new BodyComposition(70, 0.20, 30, 0.0, 0.5);
    }

    public double getFatMass() {
        return totalMass * bodyFatPercentage;
    }

    public double getTotalMass() {
        return totalMass;
    }

    public void setTotalMass(double totalMass) {
        this.totalMass = totalMass;
    }

    public double getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(double bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
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

    public double getNeuromuscularEfficiency() {
        return neuromuscularEfficiency;
    }

    public void setNeuromuscularEfficiency(double neuromuscularEfficiency) {
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }
}
