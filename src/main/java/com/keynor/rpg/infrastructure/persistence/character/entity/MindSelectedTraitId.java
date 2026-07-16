package com.keynor.rpg.infrastructure.persistence.character.entity;

import java.io.Serializable;
import java.util.Objects;

/** Composite key for {@link MindSelectedTraitEntity}: (character_id, trait). */
public class MindSelectedTraitId implements Serializable {

    private Long characterId;
    private String trait;

    protected MindSelectedTraitId() {
    }

    public MindSelectedTraitId(Long characterId, String trait) {
        this.characterId = characterId;
        this.trait = trait;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MindSelectedTraitId that)) return false;
        return Objects.equals(characterId, that.characterId) && Objects.equals(trait, that.trait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId, trait);
    }
}
