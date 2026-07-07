package com.keynor.rpg.domain.model;

/**
 * Trainable. Additive-standard discrete scale (rpg-11): 1-9, neutral 5 (formulas use the
 * {@code value - 5} deviation).
 *
 * <p>{@code astralVentriculum} ("a fifth muscular chamber capable of pumping magical energy
 * through the body") is a magical organ only magical races possess — absent (0) for the human
 * default template, which locks its slider disabled in the frontend. When present, its neutral
 * point is 6 (not 5) and it feeds {@link PlayableCharacter#getArcaneOutput()} exclusively, using
 * a wider weight (8) than the standard 1-9 traits.
 *
 * <p>{@code astralAtrium} ("a supernatural organ capable of pumping organic energy into the
 * body") is a second, distinct supernatural heart organ, same absent/disabled-on-human shape as
 * {@code astralVentriculum} — a real second field, not a rename of it. It feeds
 * {@link PlayableCharacter#getChiPool()} (neutral point 6, same wide weight 8 as every other
 * arcane-organ attribute) and, unlike the other three arcane organs, also contributes directly
 * (as its own raw value, not a neutral-6 deviation) to {@link PlayableCharacter#getStaminaPool()}.
 */
public class CardiacSystem {

    private int cardiacOutput;
    private int astralVentriculum;
    private int astralAtrium;

    public CardiacSystem(int cardiacOutput, int astralVentriculum, int astralAtrium) {
        this.cardiacOutput = cardiacOutput;
        this.astralVentriculum = astralVentriculum;
        this.astralAtrium = astralAtrium;
    }

    public static CardiacSystem defaults() {
        return new CardiacSystem(5, 0, 0);
    }

    public int getCardiacOutput() {
        return cardiacOutput;
    }

    public void setCardiacOutput(int cardiacOutput) {
        this.cardiacOutput = cardiacOutput;
    }

    public int getAstralVentriculum() {
        return astralVentriculum;
    }

    public void setAstralVentriculum(int astralVentriculum) {
        this.astralVentriculum = astralVentriculum;
    }

    public int getAstralAtrium() {
        return astralAtrium;
    }

    public void setAstralAtrium(int astralAtrium) {
        this.astralAtrium = astralAtrium;
    }
}
