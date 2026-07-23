package com.keynor.rpg.domain.port.out;

import com.keynor.rpg.domain.model.clown.ClownChatCommand;
import com.keynor.rpg.domain.model.clown.ClownChatResult;

/**
 * Output port for whatever actually embodies the Clown persona and produces a reply — today, a
 * real Anthropic API call (see {@code infrastructure.external.anthropic.AnthropicClownAssistantAdapter}).
 * Kept as a port, not a direct dependency, so the application layer never depends on the LLM
 * client directly and so tests can substitute a stub instead of ever making a real call — see
 * {@code keynor-rpg/CLAUDE.md}'s human-only-invocation section: no test may invoke the real
 * adapter.
 */
public interface ClownAssistantPort {

    ClownChatResult converse(ClownChatCommand command);
}
