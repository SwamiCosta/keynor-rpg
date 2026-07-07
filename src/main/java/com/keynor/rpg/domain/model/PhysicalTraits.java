package com.keynor.rpg.domain.model;

/**
 * New top-level data group (rpg-14), sibling of {@link Biomechanics} and {@link BodySystems}
 * inside {@link Body}. Groups the "Physical Traits" ("Physical Characteristics" in the UI)
 * sub-groups: {@link SensorialOrgans}, {@link BodyStructure}, and (added later)
 * {@link TrainingAndConditioning}.
 */
public class PhysicalTraits {

    private final SensorialOrgans sensorialOrgans;
    private final BodyStructure bodyStructure;
    private final TrainingAndConditioning trainingAndConditioning;

    public PhysicalTraits(SensorialOrgans sensorialOrgans, BodyStructure bodyStructure,
                           TrainingAndConditioning trainingAndConditioning) {
        this.sensorialOrgans = sensorialOrgans;
        this.bodyStructure = bodyStructure;
        this.trainingAndConditioning = trainingAndConditioning;
    }

    public static PhysicalTraits defaults() {
        return new PhysicalTraits(SensorialOrgans.defaults(), BodyStructure.defaults(),
                TrainingAndConditioning.defaults());
    }

    public SensorialOrgans getSensorialOrgans() {
        return sensorialOrgans;
    }

    public BodyStructure getBodyStructure() {
        return bodyStructure;
    }

    public TrainingAndConditioning getTrainingAndConditioning() {
        return trainingAndConditioning;
    }
}
