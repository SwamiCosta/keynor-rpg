package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Biomechanics;

public record CalculatedValuesResponse(double totalMass, double boneMass, double organWaterMass) {

    public static CalculatedValuesResponse from(Biomechanics biomechanics) {
        return new CalculatedValuesResponse(biomechanics.getTotalMass(), biomechanics.getBoneMass(),
                biomechanics.getOrganWaterMass());
    }
}
