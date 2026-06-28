package com.keynor.rpg.domain.model;

/**
 * Trainable layer of physical attributes — changes through training/diet over the course
 * of the game, unlike {@link Genetics}. {@code bodyFat} (kg) is intentionally a value the
 * player generally wants to lower, not raise. {@code totalMass} is not stored here — it is
 * derived on {@link Biomechanics}, alongside bone and organ/fluid mass.
 */
public class BodyComposition {

    private double bodyFat;
    private double muscleMass;
    private double dominantFiberType;
    private double neuromuscularEfficiency;

    public BodyComposition(double bodyFat, double muscleMass, double dominantFiberType,
                            double neuromuscularEfficiency) {
        this.bodyFat = bodyFat;
        this.muscleMass = muscleMass;
        this.dominantFiberType = dominantFiberType;
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }

    public static BodyComposition defaults() {
        return new BodyComposition(14, 30, 0.0, 0.5);
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

    public double getNeuromuscularEfficiency() {
        return neuromuscularEfficiency;
    }

    public void setNeuromuscularEfficiency(double neuromuscularEfficiency) {
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }
}
