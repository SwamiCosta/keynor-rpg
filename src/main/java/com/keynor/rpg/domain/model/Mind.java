package com.keynor.rpg.domain.model;

/**
 * Second pillar of {@link PlayableCharacter}, sibling of {@link Body} — composition, not a
 * shared {@code Pilar} interface, matching the same rationale documented on {@code Body}: each
 * pillar's rules differ fundamentally enough that a unifying abstraction would not be reusable.
 *
 * <p>Four data groups: {@link Values}, {@link Erudition} (knowledge levels), {@link Personality}
 * (Values-linked traits, added rpg-19), and {@link Labours} (job levels, added rpg-19). Only
 * {@link Values} and {@link Personality} draw from {@link #eventPoints}, a dedicated
 * {@link AttributePointBudget} for {@link InputNature#EVENTFUL} inputs — separate from
 * {@link Body}'s genetic and training pools. Seeded at 20 points to match the illustrative size
 * of the other two pools; per-input costs are deferred the same way genetic/training costs are
 * (see {@link AttributePointBudget}). {@link Erudition} and {@link Labours} are
 * {@link InputNature#TRAINED} and spend from their own small, independent point budgets instead.
 */
public class Mind {

    private final Values values;
    private final Erudition erudition;
    private final Personality personality;
    private final Labours labours;
    private final AttributePointBudget eventPoints;

    private Mind(Values values, Erudition erudition, Personality personality, Labours labours,
                 AttributePointBudget eventPoints) {
        this.values = values;
        this.erudition = erudition;
        this.personality = personality;
        this.labours = labours;
        this.eventPoints = eventPoints;
    }

    public static Mind humanTemplate() {
        return fromDataGroups(Values.defaults(), Erudition.defaults(), Personality.defaults(), Labours.defaults());
    }

    /**
     * Builds a Mind with the provided data groups (for stateless previews and tests) using a
     * fresh event-points budget.
     */
    public static Mind previewTemplate(Values values, Erudition erudition, Personality personality,
                                        Labours labours) {
        return fromDataGroups(values, erudition, personality, labours);
    }

    private static Mind fromDataGroups(Values values, Erudition erudition, Personality personality,
                                        Labours labours) {
        return new Mind(values, erudition, personality, labours, new AttributePointBudget(20));
    }

    public Values getValues() {
        return values;
    }

    public Erudition getErudition() {
        return erudition;
    }

    public Personality getPersonality() {
        return personality;
    }

    public Labours getLabours() {
        return labours;
    }

    public AttributePointBudget getEventPoints() {
        return eventPoints;
    }
}
