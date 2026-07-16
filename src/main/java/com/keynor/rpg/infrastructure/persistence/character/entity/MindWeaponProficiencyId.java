package com.keynor.rpg.infrastructure.persistence.character.entity;

import java.io.Serializable;
import java.util.Objects;

/** Composite key for {@link MindWeaponProficiencyEntity}: (character_id, weapon). */
public class MindWeaponProficiencyId implements Serializable {

    private Long characterId;
    private String weapon;

    protected MindWeaponProficiencyId() {
    }

    public MindWeaponProficiencyId(Long characterId, String weapon) {
        this.characterId = characterId;
        this.weapon = weapon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MindWeaponProficiencyId that)) return false;
        return Objects.equals(characterId, that.characterId) && Objects.equals(weapon, that.weapon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId, weapon);
    }
}
