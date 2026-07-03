package com.keynor.rpg.domain.model;

/**
 * Pure data holder for the genetic and body-composition layers of a {@link Body}'s
 * physical make-up. All attribute computation (getStrength, getSpeed, etc.) has moved
 * to {@link PlayableCharacter}, which combines inputs from this class and
 * {@link BodySystems} (whose {@link NeuralSystem} absorbed the former SpatialIntelligence
 * group in rpg-13).
 */
public class Biomechanics {

    private final Genetics genetics;
    private final BodyComposition bodyComposition;

    public Biomechanics(Genetics genetics, BodyComposition bodyComposition) {
        this.genetics = genetics;
        this.bodyComposition = bodyComposition;
    }

    public static Biomechanics defaults() {
        return new Biomechanics(Genetics.defaults(), BodyComposition.defaults());
    }

    public Genetics getGenetics() {
        return genetics;
    }

    public BodyComposition getBodyComposition() {
        return bodyComposition;
    }
}
