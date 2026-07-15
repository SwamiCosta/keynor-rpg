package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_cardiac_system")
public class BodyCardiacSystemEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "cardiac_output", nullable = false)
    private int cardiacOutput;

    @Column(name = "astral_ventriculum", nullable = false)
    private int astralVentriculum;

    @Column(name = "astral_atrium", nullable = false)
    private int astralAtrium;

    protected BodyCardiacSystemEntity() {
    }

    public BodyCardiacSystemEntity(Long characterId, int cardiacOutput, int astralVentriculum, int astralAtrium) {
        this.characterId = characterId;
        this.cardiacOutput = cardiacOutput;
        this.astralVentriculum = astralVentriculum;
        this.astralAtrium = astralAtrium;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getCardiacOutput() {
        return cardiacOutput;
    }

    public int getAstralVentriculum() {
        return astralVentriculum;
    }

    public int getAstralAtrium() {
        return astralAtrium;
    }
}
