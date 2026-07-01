package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.CardiacSystem;

public record CardiacSystemInput(int cardiacOutput) {

    public CardiacSystem toDomain() {
        return new CardiacSystem(cardiacOutput);
    }
}
