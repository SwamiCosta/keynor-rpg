package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.GeneralPersonality;

public record GeneralPersonalityInput(int vanity, int focus) {

    public GeneralPersonality toDomain() {
        return new GeneralPersonality(vanity, focus);
    }
}
