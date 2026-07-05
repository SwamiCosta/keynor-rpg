package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Mind;

public record MindResponse(ValuesResponse values, EruditionResponse erudition, PointBudgetResponse eventPoints) {

    public static MindResponse from(Mind mind) {
        return new MindResponse(ValuesResponse.from(mind.getValues()), EruditionResponse.from(mind.getErudition()),
                PointBudgetResponse.from(mind.getEventPoints()));
    }
}
