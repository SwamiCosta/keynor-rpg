package com.keynor.rpg.domain.model;

/**
 * Trainable. Additive-standard discrete scale (rpg-11): 1-9, neutral 5 (formulas use the
 * {@code value - 5} deviation).
 */
public class CardiacSystem {

    private int cardiacOutput;

    public CardiacSystem(int cardiacOutput) {
        this.cardiacOutput = cardiacOutput;
    }

    public static CardiacSystem defaults() {
        return new CardiacSystem(5);
    }

    public int getCardiacOutput() {
        return cardiacOutput;
    }

    public void setCardiacOutput(int cardiacOutput) {
        this.cardiacOutput = cardiacOutput;
    }
}
