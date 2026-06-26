package com.keynor.rpg.domain.model;

/**
 * Genetic layer of physical attributes — set once at character creation and immutable
 * afterward (no setters), since genetics cannot be changed once the game starts.
 * Does not feed combat formulas directly; it will modulate how fast {@link BodyComposition}
 * changes through training (rate formulas not yet implemented).
 */
public class Genetics {

    private final double endomorphy;
    private final double mesomorphy;
    private final double ectomorphy;
    private final double height;
    private final double limbRatio;
    private final double boneDensity;

    public Genetics(double endomorphy, double mesomorphy, double ectomorphy, double height,
                     double limbRatio, double boneDensity) {
        this.endomorphy = endomorphy;
        this.mesomorphy = mesomorphy;
        this.ectomorphy = ectomorphy;
        this.height = height;
        this.limbRatio = limbRatio;
        this.boneDensity = boneDensity;
    }

    public static Genetics defaults() {
        return new Genetics(5, 5, 5, 170, 1.0, 5);
    }

    public double getEndomorphy() {
        return endomorphy;
    }

    public double getMesomorphy() {
        return mesomorphy;
    }

    public double getEctomorphy() {
        return ectomorphy;
    }

    public double getHeight() {
        return height;
    }

    public double getLimbRatio() {
        return limbRatio;
    }

    public double getBoneDensity() {
        return boneDensity;
    }
}
