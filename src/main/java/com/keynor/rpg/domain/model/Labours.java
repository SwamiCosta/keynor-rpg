package com.keynor.rpg.domain.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Fourth data group of the {@link Mind} pillar, added in rpg-19: how far a character has invested
 * in each {@link Job}. Structurally identical to {@link Erudition} — a small shared point budget
 * ({@link #BASE_POINTS}, 2), distributable as 2-in-one or 1-in-two across the seven jobs — except
 * jobs carry no formula effect of their own; only the point budget itself matters today.
 *
 * <p>{@code CONSERVATIVE} and {@code LUDDITE} each grant one extra point — see
 * {@link Personality#getLabourPointsModifier()} — so, like Erudition, the cap is computed
 * per-character via {@link #getEffectivePoints(PlayableCharacter)}.
 */
public class Labours {

    public static final int BASE_POINTS = 2;

    private final Map<Job, Integer> levels;

    public Labours(Map<Job, Integer> levels) {
        this.levels = new EnumMap<>(Job.class);
        for (Job job : Job.values()) {
            this.levels.put(job, levels.getOrDefault(job, 0));
        }
    }

    public static Labours defaults() {
        return new Labours(Map.of());
    }

    public int getLevel(Job job) {
        return levels.get(job);
    }

    public int getSpentPoints() {
        return levels.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getEffectivePoints(PlayableCharacter character) {
        return BASE_POINTS + character.getMind().getPersonality().getLabourPointsModifier();
    }

    public boolean canSetLevel(Job job, int newLevel, PlayableCharacter character) {
        if (newLevel < Job.MIN_LEVEL || newLevel > Job.MAX_LEVEL) {
            return false;
        }
        int spentWithoutThis = getSpentPoints() - getLevel(job);
        return spentWithoutThis + newLevel <= getEffectivePoints(character);
    }

    public void setLevel(Job job, int newLevel, PlayableCharacter character) {
        if (!canSetLevel(job, newLevel, character)) {
            throw new IllegalStateException(
                    "Cannot set " + job + " to " + newLevel + ": exceeds available labour points");
        }
        levels.put(job, newLevel);
    }

    public Map<Job, Integer> getLevels() {
        return Map.copyOf(levels);
    }
}
