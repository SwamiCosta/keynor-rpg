package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Genetics;

public record GeneticsResponse(double endomorphy, double mesomorphy, double ectomorphy, double height,
                                double limbRatio, double boneDensity, double muscleDistribution) {

    public static GeneticsResponse from(Genetics genetics) {
        return new GeneticsResponse(genetics.getEndomorphy(), genetics.getMesomorphy(), genetics.getEctomorphy(),
                genetics.getHeight(), genetics.getLimbRatio(), genetics.getBoneDensity(),
                genetics.getMuscleDistribution());
    }
}
