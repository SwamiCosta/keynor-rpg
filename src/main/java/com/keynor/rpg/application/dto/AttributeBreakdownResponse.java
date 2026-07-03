package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.AttributeBreakdown;
import java.util.List;

public record AttributeBreakdownResponse(double baseline, List<Double> terms) {

    public static AttributeBreakdownResponse from(AttributeBreakdown breakdown) {
        return new AttributeBreakdownResponse(breakdown.baseline(), breakdown.terms());
    }
}
