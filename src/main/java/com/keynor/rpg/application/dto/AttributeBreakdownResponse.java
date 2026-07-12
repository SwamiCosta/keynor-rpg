package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.AttributeBreakdown;
import com.keynor.rpg.domain.model.Language;
import java.util.List;

public record AttributeBreakdownResponse(double baseline, List<TermResponse> terms) {

    public record TermResponse(String label, double value) {

        public static TermResponse from(AttributeBreakdown.Term term, Language language) {
            return new TermResponse(TermLabelTranslations.translate(term.label(), language), term.value());
        }
    }

    public static AttributeBreakdownResponse from(AttributeBreakdown breakdown, Language language) {
        return new AttributeBreakdownResponse(breakdown.baseline(),
                breakdown.terms().stream().map(term -> TermResponse.from(term, language)).toList());
    }
}
