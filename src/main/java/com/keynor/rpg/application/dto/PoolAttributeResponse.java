package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PoolAttribute;

public record PoolAttributeResponse(double total, double current) {

    public static PoolAttributeResponse from(PoolAttribute poolAttribute) {
        return new PoolAttributeResponse(poolAttribute.total(), poolAttribute.current());
    }
}
