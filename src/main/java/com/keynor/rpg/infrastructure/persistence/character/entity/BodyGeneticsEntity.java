package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_genetics")
public class BodyGeneticsEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "endomorphy", nullable = false)
    private int endomorphy;

    @Column(name = "mesomorphy", nullable = false)
    private int mesomorphy;

    @Column(name = "ectomorphy", nullable = false)
    private int ectomorphy;

    @Column(name = "height", nullable = false)
    private int height;

    @Column(name = "limb_ratio", nullable = false)
    private int limbRatio;

    protected BodyGeneticsEntity() {
    }

    public BodyGeneticsEntity(Long characterId, int endomorphy, int mesomorphy, int ectomorphy, int height,
                               int limbRatio) {
        this.characterId = characterId;
        this.endomorphy = endomorphy;
        this.mesomorphy = mesomorphy;
        this.ectomorphy = ectomorphy;
        this.height = height;
        this.limbRatio = limbRatio;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getEndomorphy() {
        return endomorphy;
    }

    public int getMesomorphy() {
        return mesomorphy;
    }

    public int getEctomorphy() {
        return ectomorphy;
    }

    public int getHeight() {
        return height;
    }

    public int getLimbRatio() {
        return limbRatio;
    }
}
