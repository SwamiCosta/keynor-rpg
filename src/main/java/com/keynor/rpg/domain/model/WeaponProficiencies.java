package com.keynor.rpg.domain.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Sixth data group of the {@link Mind} pillar: how far a character has trained in each
 * {@link Weapon}. Structurally close to {@link Erudition}/{@link Labours} (a leveled map keyed
 * by an enum, defaulting every entry to 0), but with no shared point budget — each weapon level
 * is independently bounds-checked against {@link Weapon#MIN_LEVEL}/{@link Weapon#MAX_LEVEL}
 * only, not against a spent-points cap.
 */
public class WeaponProficiencies {

    private final Map<Weapon, Integer> levels;

    public WeaponProficiencies(Map<Weapon, Integer> levels) {
        this.levels = new EnumMap<>(Weapon.class);
        for (Weapon weapon : Weapon.values()) {
            this.levels.put(weapon, levels.getOrDefault(weapon, 0));
        }
    }

    public static WeaponProficiencies defaults() {
        return new WeaponProficiencies(Map.of());
    }

    public int getLevel(Weapon weapon) {
        return levels.get(weapon);
    }

    public boolean canSetLevel(Weapon weapon, int newLevel) {
        return newLevel >= Weapon.MIN_LEVEL && newLevel <= Weapon.MAX_LEVEL;
    }

    public void setLevel(Weapon weapon, int newLevel) {
        if (!canSetLevel(weapon, newLevel)) {
            throw new IllegalStateException("Cannot set " + weapon + " to " + newLevel + ": out of range");
        }
        levels.put(weapon, newLevel);
    }

    public Map<Weapon, Integer> getLevels() {
        return Map.copyOf(levels);
    }
}
