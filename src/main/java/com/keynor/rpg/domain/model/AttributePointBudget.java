package com.keynor.rpg.domain.model;

/**
 * Generic spend/remaining tracker for a pool of allocatable attribute points (genetic or
 * training). Deliberately unaware of the attributes it funds — the per-attribute cost of
 * moving away from its default (e.g. points per cm of height vs. points per somatotype
 * unit) is a character-creation use-case concern, not yet implemented.
 */
public class AttributePointBudget {

    private final int totalPoints;
    private int spentPoints;

    public AttributePointBudget(int totalPoints) {
        this(totalPoints, 0);
    }

    /**
     * Restores a budget with an already-spent amount — used when reloading a persisted
     * character, where {@code spentPoints} is real stored state rather than a fresh count.
     */
    public AttributePointBudget(int totalPoints, int spentPoints) {
        this.totalPoints = totalPoints;
        this.spentPoints = spentPoints;
    }

    public void spend(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot spend a negative amount of points");
        }
        if (spentPoints + amount > totalPoints) {
            throw new IllegalStateException("Not enough points remaining: requested " + amount
                    + ", only " + remainingPoints() + " available");
        }
        spentPoints += amount;
    }

    public int remainingPoints() {
        return totalPoints - spentPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getSpentPoints() {
        return spentPoints;
    }
}
