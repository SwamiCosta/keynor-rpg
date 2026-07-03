package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.SensorialOrgans;

public record SensorialOrgansResponse(int eyesSensitivity, int earsSensitivity, int noseSensitivity) {

    public static SensorialOrgansResponse from(SensorialOrgans sensorialOrgans) {
        return new SensorialOrgansResponse(sensorialOrgans.getEyesSensitivity(),
                sensorialOrgans.getEarsSensitivity(), sensorialOrgans.getNoseSensitivity());
    }
}
