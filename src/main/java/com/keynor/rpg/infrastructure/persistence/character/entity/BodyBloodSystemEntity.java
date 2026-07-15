package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_blood_system")
public class BodyBloodSystemEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "oxygen_carrying_capacity", nullable = false)
    private int oxygenCarryingCapacity;

    @Column(name = "blood_thickness", nullable = false)
    private int bloodThickness;

    protected BodyBloodSystemEntity() {
    }

    public BodyBloodSystemEntity(Long characterId, int oxygenCarryingCapacity, int bloodThickness) {
        this.characterId = characterId;
        this.oxygenCarryingCapacity = oxygenCarryingCapacity;
        this.bloodThickness = bloodThickness;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getOxygenCarryingCapacity() {
        return oxygenCarryingCapacity;
    }

    public int getBloodThickness() {
        return bloodThickness;
    }
}
