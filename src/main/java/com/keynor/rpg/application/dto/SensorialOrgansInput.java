package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.SensorialOrgans;

public record SensorialOrgansInput(int eyesSensitivity, int earsSensitivity, int noseSensitivity) {

    public SensorialOrgans toDomain() {
        return new SensorialOrgans(eyesSensitivity, earsSensitivity, noseSensitivity);
    }
}
