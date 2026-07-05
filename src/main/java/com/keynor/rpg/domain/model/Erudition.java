package com.keynor.rpg.domain.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Second data group of the {@link Mind} pillar: how far a character has invested in each
 * {@link Knowledge}. Rewritten in rpg-19 — knowledge stopped being a boolean {@link Trait} pick
 * and became a leveled slider (0 "Unknown" to {@link Knowledge#MAX_LEVEL} "Master"), spending
 * from a small shared point budget instead of a free-slot cap: {@link #BASE_POINTS} (2) points
 * total, distributable as 2-in-one or 1-in-two, e.g. Ecology at level 2 or Ecology and Biology
 * each at level 1.
 *
 * <p>The effective budget can be adjusted by a selected {@link Trait} ({@code ILLITERATE} costs a
 * point, {@code ORPHAN_MIND} grants one back) — see
 * {@link Personality#getKnowledgePointsModifier()} — so the cap is computed per-character via
 * {@link #getEffectivePoints(PlayableCharacter)}, not read as a constant.
 */
public class Erudition {

    public static final int BASE_POINTS = 2;

    private final Map<Knowledge, Integer> levels;

    public Erudition(Map<Knowledge, Integer> levels) {
        this.levels = new EnumMap<>(Knowledge.class);
        for (Knowledge knowledge : Knowledge.values()) {
            this.levels.put(knowledge, levels.getOrDefault(knowledge, 0));
        }
    }

    public static Erudition defaults() {
        return new Erudition(Map.of());
    }

    public int getLevel(Knowledge knowledge) {
        return levels.get(knowledge);
    }

    public int getSpentPoints() {
        return levels.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getEffectivePoints(PlayableCharacter character) {
        return BASE_POINTS + character.getMind().getPersonality().getKnowledgePointsModifier();
    }

    public boolean canSetLevel(Knowledge knowledge, int newLevel, PlayableCharacter character) {
        if (newLevel < Knowledge.MIN_LEVEL || newLevel > Knowledge.MAX_LEVEL) {
            return false;
        }
        int spentWithoutThis = getSpentPoints() - getLevel(knowledge);
        return spentWithoutThis + newLevel <= getEffectivePoints(character);
    }

    public void setLevel(Knowledge knowledge, int newLevel, PlayableCharacter character) {
        if (!canSetLevel(knowledge, newLevel, character)) {
            throw new IllegalStateException(
                    "Cannot set " + knowledge + " to " + newLevel + ": exceeds available knowledge points");
        }
        levels.put(knowledge, newLevel);
    }

    public Map<Knowledge, Integer> getLevels() {
        return Map.copyOf(levels);
    }
}
