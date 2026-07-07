package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.PlayableCharacter;

public record MindResponse(ValuesResponse values, EruditionResponse erudition, PersonalityResponse personality,
                            LaboursResponse labours, GeneralPersonalityResponse generalPersonality,
                            PointBudgetResponse eventPoints) {

    public static MindResponse from(Mind mind, PlayableCharacter character) {
        return new MindResponse(
                ValuesResponse.from(mind.getValues()),
                EruditionResponse.from(mind.getErudition(), character),
                PersonalityResponse.from(mind.getPersonality()),
                LaboursResponse.from(mind.getLabours(), character),
                GeneralPersonalityResponse.from(mind.getGeneralPersonality()),
                PointBudgetResponse.from(mind.getEventPoints()));
    }
}
