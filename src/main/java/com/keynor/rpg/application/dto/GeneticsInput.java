package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Genetics;

public record GeneticsInput(double endomorphy, double mesomorphy, double ectomorphy, double height,
                             double limbRatio, double boneDensity, double muscleDistribution) {

    public Genetics toDomain() {
        return new Genetics(endomorphy, mesomorphy, ectomorphy, height, limbRatio, boneDensity, muscleDistribution);
    }
}
