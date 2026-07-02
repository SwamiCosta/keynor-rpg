package com.keynor.rpg.domain.model;

/**
 * Genetic layer of physical attributes — set once at character creation and immutable
 * afterward (no setters), since genetics cannot be changed once the game starts.
 *
 * <p>Additive-standard discrete scales (rpg-11): {@code endomorphy}/{@code mesomorphy}/
 * {@code ectomorphy}/{@code boneDensity} are 1-9 with neutral 5 (formulas use the
 * {@code value - 5} deviation). {@code limbRatio} is 1-5 with neutral 3 (deviation
 * {@code value - 3}). {@code height} is 1-15 with neutral 7, used directly (not as a
 * deviation) inside {@link PlayableCharacter#getSymbolicTotalMass()} and
 * {@link PlayableCharacter#getDisplayMassKg()} — it represents 140cm-210cm in 5cm steps,
 * but that real-unit conversion only matters for {@code DisplayMassKg}/UI, never for
 * gameplay formulas directly.
 *
 * <p>{@code endomorphy}/{@code ectomorphy} remain unused by any formula (same as before
 * rpg-11) — reserved for future pillars/mechanics.
 */
public class Genetics {

    private final int endomorphy;
    private final int mesomorphy;
    private final int ectomorphy;
    private final int height;
    private final int limbRatio;
    private final int boneDensity;

    public Genetics(int endomorphy, int mesomorphy, int ectomorphy, int height,
                     int limbRatio, int boneDensity) {
        this.endomorphy = endomorphy;
        this.mesomorphy = mesomorphy;
        this.ectomorphy = ectomorphy;
        this.height = height;
        this.limbRatio = limbRatio;
        this.boneDensity = boneDensity;
    }

    public static Genetics defaults() {
        return new Genetics(5, 5, 5, 7, 3, 5);
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

    public int getBoneDensity() {
        return boneDensity;
    }
}
