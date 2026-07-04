package com.keynor.rpg.domain.model;

/**
 * A selectable character feature (checkbox in the UI), the first concrete instance of the new
 * "Trait" input type. Unlike a slider input, a trait is boolean — present or absent — which
 * fits the additive-standard formula shape without any new exception: a trait term is simply
 * {@code weight x (hasTrait ? 1 : 0)}, a 0/1 input with neutral 0 (see
 * {@code PlayableCharacter#hasTrait}). Every current constant is {@link TraitGroup Erudition}
 * knowledge and is {@link InputNature#EVENTFUL} — the "you have this because something happened
 * to you" nature, not genetic or trained.
 *
 * <p><b>Prerequisites:</b> the general trait mechanism supports gating a trait's selectability
 * on other character state ({@link #prerequisitesMet(PlayableCharacter)}), which the frontend
 * uses to grey out an unavailable trait and to prompt a confirmation modal if a previously met
 * prerequisite is later lost. No trait defined here declares one yet (every constant uses the
 * default, always-available check) — extend a specific constant with an override once a real
 * prerequisite is specified, rather than building a generic predicate/rule engine no current
 * trait needs.
 */
public enum Trait {
    CALLIGRAPHY(TraitGroup.LANGUAGES_AND_COMMUNICATION),
    ECOLOGY(TraitGroup.LIFE_STUDIES),
    BIOLOGY(TraitGroup.LIFE_STUDIES),
    MEDICINE(TraitGroup.LIFE_STUDIES),
    HERBOLOGY(TraitGroup.LIFE_STUDIES),
    ALCHEMY_CHEMISTRY(TraitGroup.MATTER_STUDIES),
    METALLURGY(TraitGroup.MATTER_STUDIES),
    POTTERY(TraitGroup.MATTER_STUDIES),
    COMPUTER_SCIENCE(TraitGroup.MATHEMATICS),
    ENGINEERING(TraitGroup.MATHEMATICS),
    WIZARDRY(TraitGroup.ARCANE_STUDIES),
    SORCERY(TraitGroup.ARCANE_STUDIES),
    ARCHERY(TraitGroup.ATHLETISM_AND_MARTIAL_ARTS),
    HISTORY(TraitGroup.VALKANI_STUDIES),
    PHILOSOPHY(TraitGroup.VALKANI_STUDIES),
    CARTOGRAPHY(TraitGroup.VALKANI_STUDIES),
    ART(TraitGroup.VALKANI_STUDIES);

    private final TraitGroup group;

    Trait(TraitGroup group) {
        this.group = group;
    }

    public TraitGroup getGroup() {
        return group;
    }

    public InputNature getNature() {
        return InputNature.EVENTFUL;
    }

    /**
     * Whether this trait can currently be selected. Defaults to always available; a future
     * trait with a real prerequisite overrides this to inspect the given character's state.
     */
    public boolean prerequisitesMet(PlayableCharacter character) {
        return true;
    }
}
