package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_digestive_system")
public class BodyDigestiveSystemEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "digestive_absorption", nullable = false)
    private int digestiveAbsorption;

    @Column(name = "impurity_cleaning", nullable = false)
    private int impurityCleaning;

    @Column(name = "ketosis_efficiency", nullable = false)
    private int ketosisEfficiency;

    protected BodyDigestiveSystemEntity() {
    }

    public BodyDigestiveSystemEntity(Long characterId, int digestiveAbsorption, int impurityCleaning,
                                      int ketosisEfficiency) {
        this.characterId = characterId;
        this.digestiveAbsorption = digestiveAbsorption;
        this.impurityCleaning = impurityCleaning;
        this.ketosisEfficiency = ketosisEfficiency;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getDigestiveAbsorption() {
        return digestiveAbsorption;
    }

    public int getImpurityCleaning() {
        return impurityCleaning;
    }

    public int getKetosisEfficiency() {
        return ketosisEfficiency;
    }
}
