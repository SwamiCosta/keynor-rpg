package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_structure")
public class BodyStructureEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "skin_thickness", nullable = false)
    private int skinThickness;

    @Column(name = "shape_aesthetics", nullable = false)
    private int shapeAesthetics;

    @Column(name = "cellular_health", nullable = false)
    private int cellularHealth;

    protected BodyStructureEntity() {
    }

    public BodyStructureEntity(Long characterId, int skinThickness, int shapeAesthetics, int cellularHealth) {
        this.characterId = characterId;
        this.skinThickness = skinThickness;
        this.shapeAesthetics = shapeAesthetics;
        this.cellularHealth = cellularHealth;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getSkinThickness() {
        return skinThickness;
    }

    public int getShapeAesthetics() {
        return shapeAesthetics;
    }

    public int getCellularHealth() {
        return cellularHealth;
    }
}
