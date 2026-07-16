package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mind_general_personality")
public class MindGeneralPersonalityEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "vanity", nullable = false)
    private int vanity;

    @Column(name = "focus", nullable = false)
    private int focus;

    protected MindGeneralPersonalityEntity() {
    }

    public MindGeneralPersonalityEntity(Long characterId, int vanity, int focus) {
        this.characterId = characterId;
        this.vanity = vanity;
        this.focus = focus;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getVanity() {
        return vanity;
    }

    public int getFocus() {
        return focus;
    }
}
