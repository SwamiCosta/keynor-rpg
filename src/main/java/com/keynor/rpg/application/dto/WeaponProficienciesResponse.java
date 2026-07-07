package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Weapon;
import com.keynor.rpg.domain.model.WeaponProficiencies;
import java.util.LinkedHashMap;
import java.util.Map;

public record WeaponProficienciesResponse(Map<String, Integer> levels) {

    public static WeaponProficienciesResponse from(WeaponProficiencies weaponProficiencies) {
        Map<String, Integer> levels = new LinkedHashMap<>();
        for (Weapon weapon : Weapon.values()) {
            levels.put(weapon.name(), weaponProficiencies.getLevel(weapon));
        }
        return new WeaponProficienciesResponse(levels);
    }
}
