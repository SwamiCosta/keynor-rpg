package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Values;

public record ValuesInput(int ego, int loyalty, int organization, int freedom, int society, int divinity, int truth,
                           int knowledge, int nature, int morality, int tradition, int justice, int progress,
                           int peace) {

    public Values toDomain() {
        return new Values(ego, loyalty, organization, freedom, society, divinity, truth, knowledge, nature,
                morality, tradition, justice, progress, peace);
    }
}
