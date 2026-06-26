package com.keynor.rpg.domain.model;

/**
 * Aggregates the genetic and trainable layers of a {@link PlayableCharacter}'s physical
 * attributes. {@code geneticPoints} and {@code trainingPoints} are illustrative placeholder
 * budgets wiring up the points-economy concept — not balanced game data.
 */
public class Biomechanics {

    private final Genetics genetics;
    private final BloodSystem bloodSystem;
    private final BodyComposition bodyComposition;
    private final NervousSystem nervousSystem;
    private final CardiacSystem cardiacSystem;
    private final PulmonarySystem pulmonarySystem;
    private final AttributePointBudget geneticPoints;
    private final AttributePointBudget trainingPoints;

    public Biomechanics(Genetics genetics, BloodSystem bloodSystem, BodyComposition bodyComposition,
                         NervousSystem nervousSystem, CardiacSystem cardiacSystem, PulmonarySystem pulmonarySystem,
                         AttributePointBudget geneticPoints, AttributePointBudget trainingPoints) {
        this.genetics = genetics;
        this.bloodSystem = bloodSystem;
        this.bodyComposition = bodyComposition;
        this.nervousSystem = nervousSystem;
        this.cardiacSystem = cardiacSystem;
        this.pulmonarySystem = pulmonarySystem;
        this.geneticPoints = geneticPoints;
        this.trainingPoints = trainingPoints;
    }

    public static Biomechanics humanDefaults() {
        return new Biomechanics(Genetics.defaults(), BloodSystem.defaults(), BodyComposition.defaults(),
                NervousSystem.defaults(), CardiacSystem.defaults(), PulmonarySystem.defaults(),
                new AttributePointBudget(20), new AttributePointBudget(20));
    }

    public Genetics getGenetics() {
        return genetics;
    }

    public BloodSystem getBloodSystem() {
        return bloodSystem;
    }

    public BodyComposition getBodyComposition() {
        return bodyComposition;
    }

    public NervousSystem getNervousSystem() {
        return nervousSystem;
    }

    public CardiacSystem getCardiacSystem() {
        return cardiacSystem;
    }

    public PulmonarySystem getPulmonarySystem() {
        return pulmonarySystem;
    }

    public AttributePointBudget getGeneticPoints() {
        return geneticPoints;
    }

    public AttributePointBudget getTrainingPoints() {
        return trainingPoints;
    }
}
