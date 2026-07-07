package com.keynor.rpg.domain.model;

/**
 * Named category a {@link Trait} belongs to. Rewritten in rpg-19: {@code Trait} no longer holds
 * Erudition knowledge (see {@link KnowledgeGroup} for that, now leveled sliders) — it exclusively
 * holds the new Values-linked personality traits, one base/advanced pair per {@link Values}
 * concern. Carries no formula behavior of its own, used purely for grouping in the UI.
 *
 * <p>A follow-up delta added a second kind of trait to several of these groups: standalone
 * "invested" traits gated by a concern threshold (e.g. "Self Concern >= 4") rather than the
 * base/advanced pair's exact-default/already-selected prerequisites — see {@code Trait}'s
 * PROTAGONIST/EGOTIST/RELIABLE/LOYALIST/CLEAN_VESSEL/RELIGION_PRACTITIONER/REALITIC/PHILOSOPHER/
 * OUTDOOR_LIFESTYLE/RETRIBUTION_SEEKER/INVENTOR/PEACEKEEPER. A group can therefore hold more than
 * two traits — this is expected, not a violation of the "one pair per concern" original design.
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
