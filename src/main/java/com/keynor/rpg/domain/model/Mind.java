package com.keynor.rpg.domain.model;

/**
 * Second pillar of {@link PlayableCharacter}, sibling of {@link Body} — composition, not a
 * shared {@code Pilar} interface, matching the same rationale documented on {@code Body}: each
 * pillar's rules differ fundamentally enough that a unifying abstraction would not be reusable.
 *
 * <p>Starts with a single data group, {@link Values}, plus {@link Erudition} (selected knowledge
 * traits). Both draw from {@link #eventPoints}, a dedicated {@link AttributePointBudget} for
 * {@link InputNature#EVENTFUL} inputs — separate from {@link Body}'s genetic and training pools,
 * since an eventful input is acquired through experience, not genetics or training. Seeded at
 * 20 points to match the illustrative size of the other two pools; per-input costs are deferred
 * the same way genetic/training costs are (see {@link AttributePointBudget}).
 */
public class Mind {

    private final Values values;
    private final Erudition erudition;
    private final AttributePointBudget eventPoints;

    private Mind(Values values, Erudition erudition, AttributePointBudget eventPoints) {
        this.values = values;
        this.erudition = erudition;
        this.eventPoints = eventPoints;
    }

    public static Mind humanTemplate() {
        return fromDataGroups(Values.defaults(), Erudition.defaults());
    }

    /**
     * Builds a Mind with the provided data groups (for stateless previews and tests) using a
     * fresh event-points budget.
     */
    public static Mind previewTemplate(Values values, Erudition erudition) {
        return fromDataGroups(values, erudition);
    }

    private static Mind fromDataGroups(Values values, Erudition erudition) {
        return new Mind(values, erudition, new AttributePointBudget(20));
    }

    public Values getValues() {
        return values;
    }

    public Erudition getErudition() {
        return erudition;
    }

    public AttributePointBudget getEventPoints() {
        return eventPoints;
    }
}
