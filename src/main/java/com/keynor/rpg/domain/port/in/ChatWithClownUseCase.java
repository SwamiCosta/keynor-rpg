package com.keynor.rpg.domain.port.in;

import com.keynor.rpg.domain.model.clown.ClownChatCommand;
import com.keynor.rpg.domain.model.clown.ClownChatResult;

/**
 * Input port for one turn of the Clown character-creation chat. See
 * {@code keynor-rpg/CLAUDE.md}'s "Clown chat endpoint — human-only invocation" section: this use
 * case exists to be called from {@code POST /api/v1/clown/chat} by a real human via the UI's
 * consent-checkbox-gated flow — never by an agent, including for testing.
 */
public interface ChatWithClownUseCase {

    ClownChatResult chat(ClownChatCommand command);
}
