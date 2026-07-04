package com.keynor.rpg.domain.model;

/**
 * Trainable endocrine layer (rpg-13, renamed from {@code HormonalSystem} to
 * {@code HormonalGlandularSystem}), sibling of {@link CardiacSystem}/{@link PulmonarySystem}
 * inside {@link BodySystems}. {@code thyroid}/{@code adrenalGlands}/
 * {@code predominantMorphicHormone} are 1-9, neutral 5.
 *
 * <p>{@code thyroid} feeds {@link PlayableCharacter#getFatigueResistance()} (metabolic rate).
 * {@code adrenalGlands} feeds {@link PlayableCharacter#getStressResistance()} (fight-or-flight
 * response).
 *
 * <p>{@code predominantMorphicHormone} (rpg-14) drives the symmetric testosterone/progesterone
 * modifier pair (see {@code PlayableCharacter}'s private {@code testosteroneModifier()}/
 * {@code progesteroneModifier()} helpers) that feeds Sight/Hearing/Smell, MentalHealthPool,
 * MuscleGainRate, Intimidation, Diplomacy, and Enfactuation. Its neutral point (5) is
 * "theoretical" — the frontend requires the player to move it away from 5 before saving a
 * character (see the rpg-14 skill note), since an undecided hormonal predisposition is not a
 * valid finished character, unlike every other neutral-default field in this domain.
 *
 * <p>{@code subtleEpiphysealGland} ("a gland that contains and concentrates magical energy") is
 * a magical organ only magical races possess — absent (0) for the human default template, which
 * locks its slider disabled in the frontend. When present, its neutral point is 6 (not 5) and it
 * feeds {@link PlayableCharacter#getManaPool()} exclusively, using a wider weight (8) than the
 * standard 1-9 traits.
 */
public class HormonalGlandularSystem {

    private int thyroid;
    private int adrenalGlands;
    private int predominantMorphicHormone;
    private int subtleEpiphysealGland;

    public HormonalGlandularSystem(int thyroid, int adrenalGlands, int predominantMorphicHormone,
                                    int subtleEpiphysealGland) {
        this.thyroid = thyroid;
        this.adrenalGlands = adrenalGlands;
        this.predominantMorphicHormone = predominantMorphicHormone;
        this.subtleEpiphysealGland = subtleEpiphysealGland;
    }

    public static HormonalGlandularSystem defaults() {
        return new HormonalGlandularSystem(5, 5, 5, 0);
    }

    public int getThyroid() {
        return thyroid;
    }

    public void setThyroid(int thyroid) {
        this.thyroid = thyroid;
    }

    public int getAdrenalGlands() {
        return adrenalGlands;
    }

    public void setAdrenalGlands(int adrenalGlands) {
        this.adrenalGlands = adrenalGlands;
    }

    public int getPredominantMorphicHormone() {
        return predominantMorphicHormone;
    }

    public void setPredominantMorphicHormone(int predominantMorphicHormone) {
        this.predominantMorphicHormone = predominantMorphicHormone;
    }

    public int getSubtleEpiphysealGland() {
        return subtleEpiphysealGland;
    }

    public void setSubtleEpiphysealGland(int subtleEpiphysealGland) {
        this.subtleEpiphysealGland = subtleEpiphysealGland;
    }
}
