package com.keynor.rpg.domain.model;

/**
 * A scholarly or practical domain of knowledge a character can invest in — the 17 constants that
 * used to be boolean {@link Trait}s until rpg-19, when knowledge was redesigned as a leveled
 * input: each knowledge is a slider from 0 ("Unknown") to 4 ("Master"), not a checkbox. Every
 * constant is {@link InputNature#TRAINED} (rpg-19 changed this from {@code EVENTFUL} — knowledge
 * is something a character studies, not something that merely happens to them).
 *
 * <p>Where a knowledge previously contributed a flat {@code weight x (hasTrait ? 1 : 0)} term to
 * a formula, it now contributes {@code weight x level} directly — the same coefficient, doubled
 * or tripled by investing further levels instead of being all-or-nothing. See
 * {@link Erudition#getLevel(Knowledge)}.
 */
public enum Knowledge {
    CALLIGRAPHY(KnowledgeGroup.LANGUAGES_AND_COMMUNICATION),
    ECOLOGY(KnowledgeGroup.LIFE_STUDIES),
    BIOLOGY(KnowledgeGroup.LIFE_STUDIES),
    MEDICINE(KnowledgeGroup.LIFE_STUDIES),
    HERBOLOGY(KnowledgeGroup.LIFE_STUDIES),
    ALCHEMY_CHEMISTRY(KnowledgeGroup.MATTER_STUDIES),
    METALLURGY(KnowledgeGroup.MATTER_STUDIES),
    POTTERY(KnowledgeGroup.MATTER_STUDIES),
    COMPUTER_SCIENCE(KnowledgeGroup.MATHEMATICS),
    ENGINEERING(KnowledgeGroup.MATHEMATICS),
    WIZARDRY(KnowledgeGroup.ARCANE_STUDIES),
    SORCERY(KnowledgeGroup.ARCANE_STUDIES),
    ARCHERY(KnowledgeGroup.ATHLETISM_AND_MARTIAL_ARTS),
    HISTORY(KnowledgeGroup.VALKANI_STUDIES),
    PHILOSOPHY(KnowledgeGroup.VALKANI_STUDIES),
    CARTOGRAPHY(KnowledgeGroup.VALKANI_STUDIES),
    ART(KnowledgeGroup.VALKANI_STUDIES);

    /** Minimum slider value ("Unknown"). */
    public static final int MIN_LEVEL = 0;
    /** Maximum slider value ("Master"). */
    public static final int MAX_LEVEL = 4;

    private final KnowledgeGroup group;

    Knowledge(KnowledgeGroup group) {
        this.group = group;
    }

    public KnowledgeGroup getGroup() {
        return group;
    }

    public InputNature getNature() {
        return InputNature.TRAINED;
    }
}
