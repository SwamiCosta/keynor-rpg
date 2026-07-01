package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Genetics;

public record GeneticsResponse(int endomorphy, int mesomorphy, int ectomorphy, int height,
                                int limbRatio, int boneDensity) {

    public static GeneticsResponse from(Genetics genetics) {
        return new GeneticsResponse(genetics.getEndomorphy(), genetics.getMesomorphy(), genetics.getEctomorphy(),
                genetics.getHeight(), genetics.getLimbRatio(), genetics.getBoneDensity());
    }
}
