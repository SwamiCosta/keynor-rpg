package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_composition")
public class BodyCompositionEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "body_fat", nullable = false)
    private int bodyFat;

    @Column(name = "muscle_mass", nullable = false)
    private int muscleMass;

    @Column(name = "dominant_fiber_type", nullable = false)
    private int dominantFiberType;

    @Column(name = "muscle_distribution", nullable = false)
    private int muscleDistribution;

    @Column(name = "flexibility", nullable = false)
    private int flexibility;

    @Column(name = "bone_density", nullable = false)
    private int boneDensity;

    @Column(name = "tendons_and_ligaments", nullable = false)
    private int tendonsAndLigaments;

    protected BodyCompositionEntity() {
    }

    public BodyCompositionEntity(Long characterId, int bodyFat, int muscleMass, int dominantFiberType,
                                  int muscleDistribution, int flexibility, int boneDensity,
                                  int tendonsAndLigaments) {
        this.characterId = characterId;
        this.bodyFat = bodyFat;
        this.muscleMass = muscleMass;
        this.dominantFiberType = dominantFiberType;
        this.muscleDistribution = muscleDistribution;
        this.flexibility = flexibility;
        this.boneDensity = boneDensity;
        this.tendonsAndLigaments = tendonsAndLigaments;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getBodyFat() {
        return bodyFat;
    }

    public int getMuscleMass() {
        return muscleMass;
    }

    public int getDominantFiberType() {
        return dominantFiberType;
    }

    public int getMuscleDistribution() {
        return muscleDistribution;
    }

    public int getFlexibility() {
        return flexibility;
    }

    public int getBoneDensity() {
        return boneDensity;
    }

    public int getTendonsAndLigaments() {
        return tendonsAndLigaments;
    }
}
