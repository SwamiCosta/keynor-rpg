package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * One row per {@code AttributePointBudget} instance a character has (GENETIC/TRAINING from
 * {@code Body}, EVENT from {@code Mind}) — mirrors the domain class being reused 3x rather than
 * flattening into 6 columns on {@code characters}.
 */
@Entity
@Table(name = "character_point_budgets")
@IdClass(CharacterPointBudgetId.class)
public class CharacterPointBudgetEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Id
    @Column(name = "budget_type")
    private String budgetType;

    @Column(name = "total_points", nullable = false)
    private int totalPoints;

    @Column(name = "spent_points", nullable = false)
    private int spentPoints;

    protected CharacterPointBudgetEntity() {
    }

    public CharacterPointBudgetEntity(Long characterId, String budgetType, int totalPoints, int spentPoints) {
        this.characterId = characterId;
        this.budgetType = budgetType;
        this.totalPoints = totalPoints;
        this.spentPoints = spentPoints;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public String getBudgetType() {
        return budgetType;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getSpentPoints() {
        return spentPoints;
    }
}
