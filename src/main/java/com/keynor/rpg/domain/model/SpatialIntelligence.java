package com.keynor.rpg.domain.model;

/**
 * Trainable group of spatial-awareness inputs used to derive the character's perception,
 * mobility, and accuracy attributes in {@link PlayableCharacter}. All three axes are
 * 0-10 scales; 5 is the balanced human default.
 *
 * <ul>
 *   <li>{@code perception} — ability to detect external stimuli.
 *   <li>{@code agility} — efficiency of body movement in any direction.
 *   <li>{@code precision} — ability to make objects move exactly as intended.
 * </ul>
 */
public class SpatialIntelligence {

    private double perception;
    private double agility;
    private double precision;

    public SpatialIntelligence(double perception, double agility, double precision) {
        this.perception = perception;
        this.agility = agility;
        this.precision = precision;
    }

    public static SpatialIntelligence defaults() {
        return new SpatialIntelligence(5, 5, 5);
    }

    public double getPerception() {
        return perception;
    }

    public void setPerception(double perception) {
        this.perception = perception;
    }

    public double getAgility() {
        return agility;
    }

    public void setAgility(double agility) {
        this.agility = agility;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }
}
