package com.keynor.rpg.domain.model;

/**
 * Second pillar of {@link PlayableCharacter}, sibling of {@link Body} — composition, not a
 * shared {@code Pilar} interface, matching the same rationale documented on {@code Body}: each
 * pillar's rules differ fundamentally enough that a unifying abstraction would not be reusable.
 *
 * <p>Six data groups: {@link Values}, {@link Erudition} (knowledge levels), {@link Personality}
 * (Values-linked traits, added rpg-19), {@link Labours} (job levels, added rpg-19),
 * {@link GeneralPersonality} (Vanity/Focus, added alongside the Psyquism attributes), and
 * {@link WeaponProficiencies} (weapon levels, the "Physical Techniques" tab's sole content so
 * far). Only {@link Values}, {@link Personality}, and {@link GeneralPersonality} draw from
 * {@link #eventPoints}, a dedicated {@link AttributePointBudget} for {@link InputNature#EVENTFUL}
 * inputs — separate from {@link Body}'s genetic and training pools. Seeded at 20 points to match
 * the illustrative size of the other two pools; per-input costs are deferred the same way
 * genetic/training costs are (see {@link AttributePointBudget}). {@link Erudition} and
 * {@link Labours} are {@link InputNature#TRAINED} and spend from their own small, independent
 * point budgets; {@link WeaponProficiencies} is also {@link InputNature#TRAINED} but has no
 * budget at all — each weapon is independently settable across its own full range.
 */
public class Mind {

    private final Values values;
    private final Erudition erudition;
    private final Personality personality;
    private final Labours labours;
    private final GeneralPersonality generalPersonality;
    private final WeaponProficiencies weaponProficiencies;
    private final AttributePointBudget eventPoints;

    private Mind(Values values, Erudition erudition, Personality personality, Labours labours,
                 GeneralPersonality generalPersonality, WeaponProficiencies weaponProficiencies,
                 AttributePointBudget eventPoints) {
        this.values = values;
        this.erudition = erudition;
        this.personality = personality;
        this.labours = labours;
        this.generalPersonality = generalPersonality;
        this.weaponProficiencies = weaponProficiencies;
        this.eventPoints = eventPoints;
    }

    public static Mind humanTemplate() {
        return fromDataGroups(Values.defaults(), Erudition.defaults(), Personality.defaults(), Labours.defaults(),
                GeneralPersonality.defaults(), WeaponProficiencies.defaults());
    }

    /**
     * Builds a Mind with the provided data groups (for stateless previews and tests) using a
     * fresh event-points budget.
     */
    public static Mind previewTemplate(Values values, Erudition erudition, Personality personality,
                                        Labours labours, GeneralPersonality generalPersonality,
                                        WeaponProficiencies weaponProficiencies) {
        return fromDataGroups(values, erudition, personality, labours, generalPersonality, weaponProficiencies);
    }

    private static Mind fromDataGroups(Values values, Erudition erudition, Personality personality,
                                        Labours labours, GeneralPersonality generalPersonality,
                                        WeaponProficiencies weaponProficiencies) {
        return new Mind(values, erudition, personality, labours, generalPersonality, weaponProficiencies,
                new AttributePointBudget(20));
    }

    /**
     * Rebuilds a Mind from persisted data groups and a persisted event-points budget — used
     * only by the persistence layer when loading a character.
     */
    public static Mind reconstruct(Values values, Erudition erudition, Personality personality,
                                    Labours labours, GeneralPersonality generalPersonality,
                                    WeaponProficiencies weaponProficiencies, AttributePointBudget eventPoints) {
        return new Mind(values, erudition, personality, labours, generalPersonality, weaponProficiencies,
                eventPoints);
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

    public GeneralPersonality getGeneralPersonality() {
        return generalPersonality;
    }

    public WeaponProficiencies getWeaponProficiencies() {
        return weaponProficiencies;
    }

    public AttributePointBudget getEventPoints() {
        return eventPoints;
    }
}
