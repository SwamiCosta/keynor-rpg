package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_hormonal_glandular_system")
public class BodyHormonalGlandularSystemEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "thyroid", nullable = false)
    private int thyroid;

    @Column(name = "adrenal_glands", nullable = false)
    private int adrenalGlands;

    @Column(name = "predominant_morphic_hormone", nullable = false)
    private int predominantMorphicHormone;

    @Column(name = "subtle_epiphyseal_gland", nullable = false)
    private int subtleEpiphysealGland;

    protected BodyHormonalGlandularSystemEntity() {
    }

    public BodyHormonalGlandularSystemEntity(Long characterId, int thyroid, int adrenalGlands,
                                              int predominantMorphicHormone, int subtleEpiphysealGland) {
        this.characterId = characterId;
        this.thyroid = thyroid;
        this.adrenalGlands = adrenalGlands;
        this.predominantMorphicHormone = predominantMorphicHormone;
        this.subtleEpiphysealGland = subtleEpiphysealGland;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getThyroid() {
        return thyroid;
    }

    public int getAdrenalGlands() {
        return adrenalGlands;
    }

    public int getPredominantMorphicHormone() {
        return predominantMorphicHormone;
    }

    public int getSubtleEpiphysealGland() {
        return subtleEpiphysealGland;
    }
}
