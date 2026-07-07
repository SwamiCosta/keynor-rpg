package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Personality;
import com.keynor.rpg.domain.model.Trait;
import java.util.Set;
import java.util.stream.Collectors;

/** New in rpg-19: the Values-linked {@link Trait} selections (moved out of Erudition). */
public record PersonalityResponse(Set<String> selectedTraits) {

    public static PersonalityResponse from(Personality personality) {
        Set<String> names = personality.getSelectedTraits().stream().map(Trait::name).collect(Collectors.toSet());
        return new PersonalityResponse(names);
    }
}
