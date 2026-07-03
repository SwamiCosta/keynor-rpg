package com.keynor.rpg.domain.model;

/**
 * New top-level data group (rpg-14), sibling of {@link Biomechanics} and {@link BodySystems}
 * inside {@link Body}. Groups the two "Physical Traits" sub-groups the user split the new
 * tab into: {@link SensorialOrgans} and {@link BodyStructure}.
 */
public class PhysicalTraits {

    private final SensorialOrgans sensorialOrgans;
    private final BodyStructure bodyStructure;

    public PhysicalTraits(SensorialOrgans sensorialOrgans, BodyStructure bodyStructure) {
        this.sensorialOrgans = sensorialOrgans;
        this.bodyStructure = bodyStructure;
    }

    public static PhysicalTraits defaults() {
        return new PhysicalTraits(SensorialOrgans.defaults(), BodyStructure.defaults());
    }

    public SensorialOrgans getSensorialOrgans() {
        return sensorialOrgans;
    }

    public BodyStructure getBodyStructure() {
        return bodyStructure;
    }
}
