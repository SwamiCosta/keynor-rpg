package com.keynor.rpg.domain.model;

/**
 * New system (rpg-13): trainable endocrine layer, sibling of {@link CardiacSystem}/
 * {@link PulmonarySystem} inside {@link BodySystems}. Both fields are 1-9, neutral 5.
 *
 * <p>{@code thyroid} feeds {@link PlayableCharacter#getFatigueResistance()} (metabolic rate).
 * {@code adrenalGlands} feeds {@link PlayableCharacter#getStressResistance()} (fight-or-flight
 * response).
 */
public class HormonalSystem {

    private int thyroid;
    private int adrenalGlands;

    public HormonalSystem(int thyroid, int adrenalGlands) {
        this.thyroid = thyroid;
        this.adrenalGlands = adrenalGlands;
    }

    public static HormonalSystem defaults() {
        return new HormonalSystem(5, 5);
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
}
