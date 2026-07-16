package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** One row per selected {@code Trait} constant — pure set membership, no level. */
@Entity
@Table(name = "mind_selected_traits")
@IdClass(MindSelectedTraitId.class)
public class MindSelectedTraitEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Id
    @Column(name = "trait")
    private String trait;

    protected MindSelectedTraitEntity() {
    }

    public MindSelectedTraitEntity(Long characterId, String trait) {
        this.characterId = characterId;
        this.trait = trait;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public String getTrait() {
        return trait;
    }
}
