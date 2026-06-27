package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.CardiacSystem;

public record CardiacSystemResponse(double cardiacOutput) {

    public static CardiacSystemResponse from(CardiacSystem cardiacSystem) {
        return new CardiacSystemResponse(cardiacSystem.getCardiacOutput());
    }
}
