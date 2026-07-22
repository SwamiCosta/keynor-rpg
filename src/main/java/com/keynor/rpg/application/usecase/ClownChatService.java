package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.clown.ClownChatCommand;
import com.keynor.rpg.domain.model.clown.ClownChatResult;
import com.keynor.rpg.domain.port.in.ChatWithClownUseCase;
import com.keynor.rpg.domain.port.out.ClownAssistantPort;

/**
 * No business logic of its own today — a thin pass-through to {@link ClownAssistantPort}. Kept as
 * a real use case (rather than calling the port directly from the controller) to match this
 * project's layering convention and to leave room for future concerns (e.g. logging, moderation)
 * without touching the controller or the port.
 */
public class ClownChatService implements ChatWithClownUseCase {

    private final ClownAssistantPort clownAssistantPort;

    public ClownChatService(ClownAssistantPort clownAssistantPort) {
        this.clownAssistantPort = clownAssistantPort;
    }

    @Override
    public ClownChatResult chat(ClownChatCommand command) {
        return clownAssistantPort.converse(command);
    }
}
