package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_sensorial_organs")
public class BodySensorialOrgansEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "eyes_sensitivity", nullable = false)
    private int eyesSensitivity;

    @Column(name = "ears_sensitivity", nullable = false)
    private int earsSensitivity;

    @Column(name = "nose_sensitivity", nullable = false)
    private int noseSensitivity;

    protected BodySensorialOrgansEntity() {
    }

    public BodySensorialOrgansEntity(Long characterId, int eyesSensitivity, int earsSensitivity,
                                      int noseSensitivity) {
        this.characterId = characterId;
        this.eyesSensitivity = eyesSensitivity;
        this.earsSensitivity = earsSensitivity;
        this.noseSensitivity = noseSensitivity;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getEyesSensitivity() {
        return eyesSensitivity;
    }

    public int getEarsSensitivity() {
        return earsSensitivity;
    }

    public int getNoseSensitivity() {
        return noseSensitivity;
    }
}
