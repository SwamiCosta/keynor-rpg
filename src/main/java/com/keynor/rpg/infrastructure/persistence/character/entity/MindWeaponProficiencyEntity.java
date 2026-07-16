package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/** One row per {@code Weapon} constant with a non-default level — no shared point budget, unlike Erudition/Labours. */
@Entity
@Table(name = "mind_weapon_proficiencies")
@IdClass(MindWeaponProficiencyId.class)
public class MindWeaponProficiencyEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Id
    @Column(name = "weapon")
    private String weapon;

    @Column(name = "level", nullable = false)
    private int level;

    protected MindWeaponProficiencyEntity() {
    }

    public MindWeaponProficiencyEntity(Long characterId, String weapon, int level) {
        this.characterId = characterId;
        this.weapon = weapon;
        this.level = level;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public String getWeapon() {
        return weapon;
    }

    public int getLevel() {
        return level;
    }
}
