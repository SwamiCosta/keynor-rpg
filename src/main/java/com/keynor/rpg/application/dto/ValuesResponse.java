package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Values;

public record ValuesResponse(int ego, int loyalty, int organization, int freedom, int society, int divinity,
                              int truth, int knowledge, int nature, int morality, int tradition, int justice,
                              int progress, int peace) {

    public static ValuesResponse from(Values values) {
        return new ValuesResponse(values.getEgo(), values.getLoyalty(), values.getOrganization(),
                values.getFreedom(), values.getSociety(), values.getDivinity(), values.getTruth(),
                values.getKnowledge(), values.getNature(), values.getMorality(), values.getTradition(),
                values.getJustice(), values.getProgress(), values.getPeace());
    }
}
