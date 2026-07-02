package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Genetics;

public record GeneticsInput(int endomorphy, int mesomorphy, int ectomorphy, int height,
                             int limbRatio, int boneDensity, int skinThickness) {

    public Genetics toDomain() {
        return new Genetics(endomorphy, mesomorphy, ectomorphy, height, limbRatio, boneDensity, skinThickness);
    }
}
