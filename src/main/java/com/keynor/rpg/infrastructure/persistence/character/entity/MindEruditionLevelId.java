package com.keynor.rpg.infrastructure.persistence.character.entity;

import java.io.Serializable;
import java.util.Objects;

/** Composite key for {@link MindEruditionLevelEntity}: (character_id, knowledge). */
public class MindEruditionLevelId implements Serializable {

    private Long characterId;
    private String knowledge;

    protected MindEruditionLevelId() {
    }

    public MindEruditionLevelId(Long characterId, String knowledge) {
        this.characterId = characterId;
        this.knowledge = knowledge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MindEruditionLevelId that)) return false;
        return Objects.equals(characterId, that.characterId) && Objects.equals(knowledge, that.knowledge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId, knowledge);
    }
}
