package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.AttributePointBudget;

public record PointBudgetResponse(int total, int spent, int remaining) {

    public static PointBudgetResponse from(AttributePointBudget budget) {
        return new PointBudgetResponse(budget.getTotalPoints(), budget.getSpentPoints(), budget.remainingPoints());
    }
}
