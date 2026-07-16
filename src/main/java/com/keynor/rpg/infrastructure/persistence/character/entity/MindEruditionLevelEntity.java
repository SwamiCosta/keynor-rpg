package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * One row per {@code Knowledge} constant with a non-default level. The {@code knowledge}
 * column stores the Java enum's {@code name()} as free text — a new {@code Knowledge} constant
 * never requires a schema change, only new rows.
 */
@Entity
@Table(name = "mind_erudition_levels")
@IdClass(MindEruditionLevelId.class)
public class MindEruditionLevelEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Id
    @Column(name = "knowledge")
    private String knowledge;

    @Column(name = "level", nullable = false)
    private int level;

    protected MindEruditionLevelEntity() {
    }

    public MindEruditionLevelEntity(Long characterId, String knowledge, int level) {
        this.characterId = characterId;
        this.knowledge = knowledge;
        this.level = level;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public int getLevel() {
        return level;
    }
}
