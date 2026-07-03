package com.keynor.rpg.domain.model;

/**
 * Genetic layer of physical attributes — set once at character creation and immutable
 * afterward (no setters), since genetics cannot be changed once the game starts.
 *
 * <p>Additive-standard discrete scales (rpg-11): {@code endomorphy}/{@code mesomorphy}/
 * {@code ectomorphy} are 1-9 with neutral 5 (formulas use the {@code value - 5} deviation).
 * {@code limbRatio} is 1-5 with neutral 3 (deviation {@code value - 3}). {@code height} is
 * 1-15 with neutral 7, used directly (not as a deviation) inside
 * {@link PlayableCharacter#getSymbolicTotalMass()} and {@link PlayableCharacter#getDisplayMassKg()}
 * — it represents 140cm-210cm in 5cm steps, but that real-unit conversion only matters for
 * {@code DisplayMassKg}/UI, never for gameplay formulas directly.
 *
 * <p>{@code boneDensity} (rpg-13) and {@code skinThickness} (rpg-13) no longer live here
 * (rpg-14) — {@code boneDensity} moved to {@link BodyComposition} (now trainable, alongside
 * the new {@code tendonsAndLigaments}) and {@code skinThickness} moved to {@link BodyStructure}
 * (still immutable/genetic, unchanged in nature — only its owning class changed).
 *
 * <p>{@code endomorphy}/{@code ectomorphy} feed the new rpg-14 {@code FatGainRate}/
 * {@code MuscleGainRate} formulas (previously unused by any formula before rpg-14).
 */
public class Genetics {

    private final int endomorphy;
    private final int mesomorphy;
    private final int ectomorphy;
    private final int height;
    private final int limbRatio;

    public Genetics(int endomorphy, int mesomorphy, int ectomorphy, int height, int limbRatio) {
        this.endomorphy = endomorphy;
        this.mesomorphy = mesomorphy;
        this.ectomorphy = ectomorphy;
        this.height = height;
        this.limbRatio = limbRatio;
    }

    public static Genetics defaults() {
        return new Genetics(5, 5, 5, 7, 3);
    }

    public int getEndomorphy() {
        return endomorphy;
    }

    public int getMesomorphy() {
        return mesomorphy;
    }

    public int getEctomorphy() {
        return ectomorphy;
    }

    public int getHeight() {
        return height;
    }

    public int getLimbRatio() {
        return limbRatio;
    }
}
