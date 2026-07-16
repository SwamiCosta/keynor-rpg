package com.keynor.rpg.infrastructure.persistence.character.entity;

import java.io.Serializable;
import java.util.Objects;

/** Composite key for {@link MindLaboursLevelEntity}: (character_id, job). */
public class MindLaboursLevelId implements Serializable {

    private Long characterId;
    private String job;

    protected MindLaboursLevelId() {
    }

    public MindLaboursLevelId(Long characterId, String job) {
        this.characterId = characterId;
        this.job = job;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MindLaboursLevelId that)) return false;
        return Objects.equals(characterId, that.characterId) && Objects.equals(job, that.job);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId, job);
    }
}
