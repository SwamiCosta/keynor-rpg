package com.keynor.rpg.domain.model;

/**
 * Named category a {@link Knowledge} belongs to, used purely for grouping in the UI (e.g. the
 * Erudition tab's seven collapsible groups). Carries no formula behavior of its own.
 *
 * <p>Renamed from {@code TraitGroup} in rpg-19, when knowledge stopped being a boolean
 * {@link Trait} and became a leveled slider — {@code TraitGroup} itself was repurposed to group
 * the new Values-linked personality traits instead.
 */
public enum KnowledgeGroup {
    LANGUAGES_AND_COMMUNICATION,
    LIFE_STUDIES,
    MATTER_STUDIES,
    MATHEMATICS,
    ARCANE_STUDIES,
    ATHLETISM_AND_MARTIAL_ARTS,
    VALKANI_STUDIES
}
