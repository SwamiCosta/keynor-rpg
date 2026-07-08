package com.keynor.rpg.domain.model;

/**
 * Third sub-group of {@link PhysicalTraits}, alongside {@link SensorialOrgans} and
 * {@link BodyStructure} — the "Training and Conditioning" group. Every field is trainable,
 * 0-8, default 0 (unlike most Body traits, which default to their scale's midpoint): a
 * character starts with no training investment here, and every formula term that reads one of
 * these fields reads the raw value directly, not a deviation from a neutral point, so a fresh
 * character's derived attributes are unaffected until the player actually trains one up.
 *
 * <p>{@code intensity}, {@code coordination}, {@code resilience}, {@code fighting},
 * {@code weaponPracticing}, {@code shooting} (rpg-21) join {@code vigor}/{@code reflexes} in the
 * same shape.
 */
public class TrainingAndConditioning {

    private int vigor;
    private int reflexes;
    private int intensity;
    private int coordination;
    private int resilience;
    private int fighting;
    private int weaponPracticing;
    private int shooting;

    public TrainingAndConditioning(int vigor, int reflexes, int intensity, int coordination,
                                    int resilience, int fighting, int weaponPracticing, int shooting) {
        this.vigor = vigor;
        this.reflexes = reflexes;
        this.intensity = intensity;
        this.coordination = coordination;
        this.resilience = resilience;
        this.fighting = fighting;
        this.weaponPracticing = weaponPracticing;
        this.shooting = shooting;
    }

    public static TrainingAndConditioning defaults() {
        return new TrainingAndConditioning(0, 0, 0, 0, 0, 0, 0, 0);
    }

    public int getVigor() {
        return vigor;
    }

    public void setVigor(int vigor) {
        this.vigor = vigor;
    }

    public int getReflexes() {
        return reflexes;
    }

    public void setReflexes(int reflexes) {
        this.reflexes = reflexes;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getCoordination() {
        return coordination;
    }

    public void setCoordination(int coordination) {
        this.coordination = coordination;
    }

    public int getResilience() {
        return resilience;
    }

    public void setResilience(int resilience) {
        this.resilience = resilience;
    }

    public int getFighting() {
        return fighting;
    }

    public void setFighting(int fighting) {
        this.fighting = fighting;
    }

    public int getWeaponPracticing() {
        return weaponPracticing;
    }

    public void setWeaponPracticing(int weaponPracticing) {
        this.weaponPracticing = weaponPracticing;
    }

    public int getShooting() {
        return shooting;
    }

    public void setShooting(int shooting) {
        this.shooting = shooting;
    }
}
