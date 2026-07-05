package com.keynor.rpg.domain.model;

/**
 * A manual trade a character can invest in, introduced in rpg-19 as the sole content of the
 * Mind pillar's new "Labours" tab. Structurally identical to {@link Knowledge} — a 0-4 slider,
 * always {@link InputNature#TRAINED} — but jobs carry no formula effect of their own today; a
 * handful of {@link Trait}s grant bonus job points (see {@link Trait#getLabourPointsModifier()}).
 * All seven belong to a single implicit "Jobs" group, so no group enum was added.
 */
public enum Job {
    MASONRY,
    TAILORING,
    CARPENTRY,
    BUILDING,
    BLACKSMITHING,
    BREWING,
    COOKING;

    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 4;

    public InputNature getNature() {
        return InputNature.TRAINED;
    }
}
