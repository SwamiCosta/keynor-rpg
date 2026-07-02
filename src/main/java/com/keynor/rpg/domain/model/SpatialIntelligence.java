package com.keynor.rpg.domain.model;

/**
 * Trainable group of spatial-awareness inputs used to derive the character's perception,
 * mobility, and accuracy attributes in {@link PlayableCharacter}.
 *
 * <p>Additive-standard discrete scale (rpg-11): all three axes are 1-9, neutral 5
 * (formulas use the {@code value - 5} deviation).
 *
 * <ul>
 *   <li>{@code perception} — ability to detect external stimuli.
 *   <li>{@code agility} — efficiency of body movement in any direction.
 *   <li>{@code precision} — ability to make objects move exactly as intended.
 * </ul>
 */
public class SpatialIntelligence {

    private int perception;
    private int agility;
    private int precision;

    public SpatialIntelligence(int perception, int agility, int precision) {
        this.perception = perception;
        this.agility = agility;
        this.precision = precision;
    }

    public static SpatialIntelligence defaults() {
        return new SpatialIntelligence(5, 5, 5);
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }
}
