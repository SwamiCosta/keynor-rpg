package com.keynor.rpg.domain.model.clown;

/**
 * One turn of the stateless Clown chat conversation, as resent by the client on every request —
 * same "no persistence, FE resends full history" precedent as {@code /character/preview} and
 * {@code /combat/action-time}. {@code role} is {@code "user"} or {@code "assistant"}.
 */
public record ClownChatMessage(String role, String content) {
}
