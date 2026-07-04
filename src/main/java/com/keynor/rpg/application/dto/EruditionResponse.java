package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.Trait;
import java.util.Set;
import java.util.stream.Collectors;

public record EruditionResponse(Set<String> selectedTraits, int freeTraitSlots) {

    public static EruditionResponse from(Erudition erudition) {
        Set<String> names = erudition.getSelectedTraits().stream().map(Trait::name).collect(Collectors.toSet());
        return new EruditionResponse(names, Erudition.FREE_TRAIT_SLOTS);
    }
}
