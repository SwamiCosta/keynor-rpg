package com.keynor.rpg.infrastructure.persistence.character.entity;

import java.io.Serializable;
import java.util.Objects;

/** Composite key for {@link CharacterPointBudgetEntity}: (character_id, budget_type). */
public class CharacterPointBudgetId implements Serializable {

    private Long characterId;
    private String budgetType;

    protected CharacterPointBudgetId() {
    }

    public CharacterPointBudgetId(Long characterId, String budgetType) {
        this.characterId = characterId;
        this.budgetType = budgetType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterPointBudgetId that)) return false;
        return Objects.equals(characterId, that.characterId) && Objects.equals(budgetType, that.budgetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId, budgetType);
    }
}
