package com.keynor.rpg.domain.model;

/**
 * Third sub-group of {@link PhysicalTraits}, alongside {@link SensorialOrgans} and
 * {@link BodyStructure} — the "Training and Conditioning" group. Both fields are trainable,
 * 0-8, default 0 (unlike most Body traits, which default to their scale's midpoint): a
 * character starts with no training investment here, and every formula term that reads one of
 * these fields reads the raw value directly, not a deviation from a neutral point, so a fresh
 * character's derived attributes are unaffected until the player actually trains one up.
 */
public class TrainingAndConditioning {

    private int vigor;
    private int reflexes;

    public TrainingAndConditioning(int vigor, int reflexes) {
        this.vigor = vigor;
        this.reflexes = reflexes;
    }

    public static TrainingAndConditioning defaults() {
        return new TrainingAndConditioning(0, 0);
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
}
