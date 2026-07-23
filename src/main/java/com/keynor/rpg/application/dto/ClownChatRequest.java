package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Language;
import com.keynor.rpg.domain.model.clown.ClownChatCommand;
import com.keynor.rpg.domain.model.clown.ClownChatMessage;
import com.keynor.rpg.domain.model.clown.ClownMode;
import com.keynor.rpg.infrastructure.web.shared.LanguageRequestParser;

import java.util.List;

/**
 * {@code mode} is one of {@code INTERACTIVE}/{@code TEMPLATE}/{@code PROMPT} (see
 * {@code ClownMode}). {@code messages} is the full conversation history, including the player's
 * newest message as its last entry — stateless, same "FE resends everything" precedent as
 * {@code /character/preview} and {@code /combat/action-time}. {@code language} defaults to
 * {@code EN} if omitted, same as every other language-aware endpoint in this project.
 */
public record ClownChatRequest(String mode, String language, List<ClownChatMessageDto> messages) {

    public ClownChatCommand toCommand() {
        ClownMode parsedMode = ClownMode.valueOf(mode.toUpperCase());
        Language parsedLanguage = language == null || language.isBlank()
                ? Language.EN
                : LanguageRequestParser.parse(language);
        List<ClownChatMessage> history = messages.stream().map(ClownChatMessageDto::toDomain).toList();
        return new ClownChatCommand(parsedMode, parsedLanguage, history);
    }
}
