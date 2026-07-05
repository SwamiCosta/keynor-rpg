package com.keynor.rpg.domain.model;

/**
 * Named category a {@link Trait} belongs to. Rewritten in rpg-19: {@code Trait} no longer holds
 * Erudition knowledge (see {@link KnowledgeGroup} for that, now leveled sliders) — it exclusively
 * holds the new Values-linked personality traits, one base/advanced pair per {@link Values}
 * concern. Carries no formula behavior of its own, used purely for grouping in the UI.
 */
public enum TraitGroup {
    SELF,
    FRIENDSHIP,
    ORDER,
    FREEDOM,
    PATRIOTISM,
    SPIRITUAL,
    PHILOSOPHY,
    ACADEMIC,
    ENVIRONMENTALISM,
    MORALITY,
    TRADITIONALISM,
    JUSTICE,
    PROGRESS,
    PEACE
}
