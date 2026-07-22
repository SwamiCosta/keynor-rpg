package com.keynor.rpg.domain.model.clown;

/**
 * Clown's response to one chat turn: {@code reply} is always present (a conversational message —
 * a question, an explanation, a rejection, or the flavor note accompanying a proposal).
 * {@code suggestedInputsJson} is a raw JSON object string, present only when Clown proposed
 * concrete input values this turn, {@code null} otherwise. The domain layer treats it as opaque —
 * it is never parsed or validated here, only forwarded to the client, which merges it into the
 * player's own editable character-creation form. Clown never saves a character; see
 * {@code clown.md}.
 */
public record ClownChatResult(String reply, String suggestedInputsJson) {
}
