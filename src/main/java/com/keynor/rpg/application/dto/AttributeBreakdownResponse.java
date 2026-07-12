package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.AttributeBreakdown;
import java.util.List;

public record AttributeBreakdownResponse(double baseline, List<TermResponse> terms) {

    public record TermResponse(String label, double value) {

        public static TermResponse from(AttributeBreakdown.Term term) {
            return new TermResponse(term.label(), term.value());
        }
    }

    public static AttributeBreakdownResponse from(AttributeBreakdown breakdown) {
        return new AttributeBreakdownResponse(breakdown.baseline(),
                breakdown.terms().stream().map(TermResponse::from).toList());
    }
}
