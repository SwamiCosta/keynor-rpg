package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_training_and_conditioning")
public class BodyTrainingAndConditioningEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "vigor", nullable = false)
    private int vigor;

    @Column(name = "reflexes", nullable = false)
    private int reflexes;

    @Column(name = "intensity", nullable = false)
    private int intensity;

    @Column(name = "coordination", nullable = false)
    private int coordination;

    @Column(name = "resilience", nullable = false)
    private int resilience;

    @Column(name = "fighting", nullable = false)
    private int fighting;

    @Column(name = "weapon_practicing", nullable = false)
    private int weaponPracticing;

    @Column(name = "shooting", nullable = false)
    private int shooting;

    protected BodyTrainingAndConditioningEntity() {
    }

    public BodyTrainingAndConditioningEntity(Long characterId, int vigor, int reflexes, int intensity,
                                              int coordination, int resilience, int fighting,
                                              int weaponPracticing, int shooting) {
        this.characterId = characterId;
        this.vigor = vigor;
        this.reflexes = reflexes;
        this.intensity = intensity;
        this.coordination = coordination;
        this.resilience = resilience;
        this.fighting = fighting;
        this.weaponPracticing = weaponPracticing;
        this.shooting = shooting;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getVigor() {
        return vigor;
    }

    public int getReflexes() {
        return reflexes;
    }

    public int getIntensity() {
        return intensity;
    }

    public int getCoordination() {
        return coordination;
    }

    public int getResilience() {
        return resilience;
    }

    public int getFighting() {
        return fighting;
    }

    public int getWeaponPracticing() {
        return weaponPracticing;
    }

    public int getShooting() {
        return shooting;
    }
}
