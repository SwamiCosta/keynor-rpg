package com.keynor.rpg.domain.model;

/**
 * Fifth data group of the {@link Mind} pillar: two general personality traits, {@code vanity}
 * and {@code focus}, both 1-9 with neutral/default 5, {@link InputNature#EVENTFUL}. Rendered
 * under Mind's "Personality Traits" tab, "General Personality" group.
 *
 * <p>Despite the similar name, this is unrelated to {@link Personality} (the selected
 * {@link Trait} catalog) — two independent {@link Mind} data groups that happen to share the
 * word "personality" in their name because the ticket that introduced this one used it for the
 * new tab/group label.
 */
public class GeneralPersonality {

    private int vanity;
    private int focus;

    public GeneralPersonality(int vanity, int focus) {
        this.vanity = vanity;
        this.focus = focus;
    }

    public static GeneralPersonality defaults() {
        return new GeneralPersonality(5, 5);
    }

    public int getVanity() {
        return vanity;
    }

    public void setVanity(int vanity) {
        this.vanity = vanity;
    }

    public int getFocus() {
        return focus;
    }

    public void setFocus(int focus) {
        this.focus = focus;
    }
}
