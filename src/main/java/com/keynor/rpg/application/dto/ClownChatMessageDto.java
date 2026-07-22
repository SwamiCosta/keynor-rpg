package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.clown.ClownChatMessage;

/** {@code role} is {@code "user"} or {@code "assistant"}. */
public record ClownChatMessageDto(String role, String content) {

    public ClownChatMessage toDomain() {
        return new ClownChatMessage(role, content);
    }
}
