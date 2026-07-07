package com.keynor.rpg.domain.model;

/**
 * A weapon category a character can train proficiency in — the sole content of the Mind
 * pillar's "Physical Techniques" tab, "Weapon Proficiencies" group. A 0-3 slider, always
 * {@link InputNature#TRAINED}, with no shared point budget (unlike {@link Knowledge}/
 * {@link Job}) — each weapon is independently settable across its own full range. Carries no
 * formula effect of its own yet. All thirteen belong to a single implicit group, so no group
 * enum was added, matching the {@link Job} precedent.
 */
public enum Weapon {
    DAGGERS,
    SHORT_SWORDS,
    LONG_SWORDS,
    RAPIERS,
    SABERS,
    SHORT_AXES_HAMMERS,
    LONG_AXES_HAMMERS,
    SPEARS,
    POLE_WEAPONS,
    STAFFS,
    BOWS,
    ONE_HANDED_TRIGGER_WEAPONS,
    TWO_HANDED_TRIGGER_WEAPONS;

    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;

    public InputNature getNature() {
        return InputNature.TRAINED;
    }
}
