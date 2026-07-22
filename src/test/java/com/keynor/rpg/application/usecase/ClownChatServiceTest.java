package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Language;
import com.keynor.rpg.domain.model.clown.ClownChatCommand;
import com.keynor.rpg.domain.model.clown.ClownChatMessage;
import com.keynor.rpg.domain.model.clown.ClownChatResult;
import com.keynor.rpg.domain.model.clown.ClownMode;
import com.keynor.rpg.domain.port.out.ClownAssistantPort;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Uses a mocked {@link ClownAssistantPort} — this project's real adapter calls the paid Anthropic
 * API and must never be exercised by a test, see {@code keynor-rpg/CLAUDE.md}'s human-only-
 * invocation section.
 */
class ClownChatServiceTest {

    @Test
    void chat_delegatesToPortAndReturnsItsResult() {
        ClownAssistantPort port = mock(ClownAssistantPort.class);
        ClownChatCommand command = new ClownChatCommand(ClownMode.TEMPLATE, Language.EN,
                List.of(new ClownChatMessage("user", "Elf Archer")));
        ClownChatResult expected = new ClownChatResult("Here's an elf archer for you!",
                "{\"body\":{\"genetics\":{\"height\":6}}}");
        when(port.converse(command)).thenReturn(expected);

        ClownChatService service = new ClownChatService(port);
        ClownChatResult actual = service.chat(command);

        assertThat(actual).isEqualTo(expected);
        verify(port).converse(command);
    }
}
