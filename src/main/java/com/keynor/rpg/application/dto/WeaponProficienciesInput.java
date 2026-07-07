package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Weapon;
import com.keynor.rpg.domain.model.WeaponProficiencies;
import java.util.Map;
import java.util.stream.Collectors;

/** Sixth Mind data group, added alongside the "Physical Techniques" tab. No point budget. */
public record WeaponProficienciesInput(Map<String, Integer> levels) {

    public WeaponProficiencies toDomain() {
        Map<Weapon, Integer> parsed = levels.entrySet().stream()
                .collect(Collectors.toMap(entry -> Weapon.valueOf(entry.getKey()), Map.Entry::getValue));
        return new WeaponProficiencies(parsed);
    }
}
