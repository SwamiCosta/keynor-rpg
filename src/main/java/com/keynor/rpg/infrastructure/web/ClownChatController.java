package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.application.dto.ClownChatRequest;
import com.keynor.rpg.application.dto.ClownChatResponse;
import com.keynor.rpg.domain.port.in.ChatWithClownUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <b>Human-only invocation.</b> This endpoint proxies a real, paid Anthropic API call. No agent
 * may ever call it, under any circumstance — including for testing or verification. Every call
 * must originate from a real human via {@code keynor-rpg-client}'s chat UI, gated by a mandatory
 * consent checkbox. See {@code keynor-rpg/CLAUDE.md}'s "Clown chat endpoint (planned) —
 * human-only invocation" section for the full rule and rationale.
 *
 * <p>Stateless — same precedent as {@code /character/preview} and {@code /combat/action-time}:
 * the client resends the full conversation history on every request, no server-side session.
 */
@RestController
@RequestMapping("/api/v1/clown")
public class ClownChatController {

    private final ChatWithClownUseCase chatWithClownUseCase;

    public ClownChatController(ChatWithClownUseCase chatWithClownUseCase) {
        this.chatWithClownUseCase = chatWithClownUseCase;
    }

    @PostMapping("/chat")
    public ClownChatResponse chat(@RequestBody ClownChatRequest request) {
        return ClownChatResponse.from(chatWithClownUseCase.chat(request.toCommand()));
    }
}
