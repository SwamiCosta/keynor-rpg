package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Personality;
import com.keynor.rpg.domain.model.Trait;
import java.util.Set;
import java.util.stream.Collectors;

/** New in rpg-19: the Values-linked {@link Trait} selections (moved out of Erudition). */
public record PersonalityInput(Set<String> selectedTraits) {

    public Personality toDomain() {
        Set<Trait> traits = selectedTraits.stream().map(Trait::valueOf).collect(Collectors.toSet());
        return new Personality(traits);
    }
}
