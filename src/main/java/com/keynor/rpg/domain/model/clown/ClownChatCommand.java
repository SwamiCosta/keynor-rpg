package com.keynor.rpg.domain.model.clown;

import com.keynor.rpg.domain.model.Language;

import java.util.List;

/**
 * One turn of a Clown chat request: the invocation mode, the app's current UI language (a hint
 * only — Clown may still reply in whatever language the player last wrote in), and the full
 * conversation history including the player's newest message as its last entry.
 */
public record ClownChatCommand(ClownMode mode, Language language, List<ClownChatMessage> history) {
}
