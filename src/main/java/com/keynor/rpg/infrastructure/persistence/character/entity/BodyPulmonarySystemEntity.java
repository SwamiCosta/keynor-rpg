package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_pulmonary_system")
public class BodyPulmonarySystemEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "pulmonary_capacity", nullable = false)
    private int pulmonaryCapacity;

    protected BodyPulmonarySystemEntity() {
    }

    public BodyPulmonarySystemEntity(Long characterId, int pulmonaryCapacity) {
        this.characterId = characterId;
        this.pulmonaryCapacity = pulmonaryCapacity;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getPulmonaryCapacity() {
        return pulmonaryCapacity;
    }
}
