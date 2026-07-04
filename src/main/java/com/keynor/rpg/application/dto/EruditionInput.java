package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.Trait;
import java.util.Set;
import java.util.stream.Collectors;

public record EruditionInput(Set<String> selectedTraits) {

    public Erudition toDomain() {
        Set<Trait> traits = selectedTraits.stream().map(Trait::valueOf).collect(Collectors.toSet());
        return new Erudition(traits);
    }
}
