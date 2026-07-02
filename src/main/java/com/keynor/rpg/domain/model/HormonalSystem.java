package com.keynor.rpg.domain.model;

/**
 * Trainable endocrine layer (rpg-13), sibling of {@link CardiacSystem}/{@link PulmonarySystem}
 * inside {@link BodySystems}. All fields are 1-9, neutral 5.
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
 */
public class HormonalSystem {

    private int thyroid;
    private int adrenalGlands;
    private int predominantMorphicHormone;

    public HormonalSystem(int thyroid, int adrenalGlands, int predominantMorphicHormone) {
        this.thyroid = thyroid;
        this.adrenalGlands = adrenalGlands;
        this.predominantMorphicHormone = predominantMorphicHormone;
    }

    public static HormonalSystem defaults() {
        return new HormonalSystem(5, 5, 5);
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
}
