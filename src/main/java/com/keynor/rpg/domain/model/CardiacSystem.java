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
 */
public class CardiacSystem {

    private int cardiacOutput;
    private int astralVentriculum;

    public CardiacSystem(int cardiacOutput, int astralVentriculum) {
        this.cardiacOutput = cardiacOutput;
        this.astralVentriculum = astralVentriculum;
    }

    public static CardiacSystem defaults() {
        return new CardiacSystem(5, 0);
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
}
