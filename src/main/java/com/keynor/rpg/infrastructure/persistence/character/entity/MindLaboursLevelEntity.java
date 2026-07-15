package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** One row per {@code Job} constant with a non-default level — same shape as {@code mind_erudition_levels}. */
@Entity
@Table(name = "mind_labours_levels")
@IdClass(MindLaboursLevelId.class)
public class MindLaboursLevelEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Id
    @Column(name = "job")
    private String job;

    @Column(name = "level", nullable = false)
    private int level;

    protected MindLaboursLevelEntity() {
    }

    public MindLaboursLevelEntity(Long characterId, String job, int level) {
        this.characterId = characterId;
        this.job = job;
        this.level = level;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public String getJob() {
        return job;
    }

    public int getLevel() {
        return level;
    }
}
