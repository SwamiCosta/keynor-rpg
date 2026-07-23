package com.keynor.rpg.application.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.keynor.rpg.domain.model.clown.ClownChatResult;

/**
 * {@code suggestedInputs} is {@code null} when Clown only replied conversationally this turn
 * (a question, an explanation, a rejection) — present as a real JSON object, not a string, only
 * when Clown proposed concrete input values. The frontend merges it into the player's own
 * editable character-creation state; it never applies automatically and Clown never saves.
 */
public record ClownChatResponse(String reply, @JsonRawValue String suggestedInputs) {

    public static ClownChatResponse from(ClownChatResult result) {
        return new ClownChatResponse(result.reply(), result.suggestedInputsJson());
    }
}
