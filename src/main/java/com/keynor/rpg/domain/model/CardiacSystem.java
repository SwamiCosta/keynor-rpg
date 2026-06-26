package com.keynor.rpg.domain.model;

/**
 * Trainable. Together with {@link PulmonarySystem} and {@link BloodSystem}, will be the
 * basis for a future derived cardiovascular capacity formula — not yet implemented.
 */
public class CardiacSystem {

    private double cardiacOutput;

    public CardiacSystem(double cardiacOutput) {
        this.cardiacOutput = cardiacOutput;
    }

    public static CardiacSystem defaults() {
        return new CardiacSystem(5);
    }

    public double getCardiacOutput() {
        return cardiacOutput;
    }

    public void setCardiacOutput(double cardiacOutput) {
        this.cardiacOutput = cardiacOutput;
    }
}
