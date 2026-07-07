package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.GeneralPersonality;

public record GeneralPersonalityResponse(int vanity, int focus) {

    public static GeneralPersonalityResponse from(GeneralPersonality generalPersonality) {
        return new GeneralPersonalityResponse(generalPersonality.getVanity(), generalPersonality.getFocus());
    }
}
