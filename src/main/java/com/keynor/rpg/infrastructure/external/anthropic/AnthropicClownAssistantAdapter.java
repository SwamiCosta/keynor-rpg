package com.keynor.rpg.infrastructure.external.anthropic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.keynor.rpg.domain.model.clown.ClownChatCommand;
import com.keynor.rpg.domain.model.clown.ClownChatMessage;
import com.keynor.rpg.domain.model.clown.ClownChatResult;
import com.keynor.rpg.domain.port.out.ClownAssistantPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Calls the real Anthropic Messages API to embody the Clown persona for the in-app character
 * creation chat.
 *
 * <p><b>This adapter must never be invoked by an agent, including for testing or verification —
 * see {@code keynor-rpg/CLAUDE.md}'s "Clown chat endpoint (planned) — human-only invocation"
 * section.</b> Every call spends real, paid Anthropic API credit. Tests covering this class must
 * mock {@link ClownAssistantPort} rather than let a real HTTP request reach the Anthropic API.
 *
 * <p>Uses the JDK's built-in {@link HttpClient} and Jackson (already transitive via
 * {@code spring-boot-starter-web}) instead of the official Anthropic SDK — a deliberate choice to
 * avoid adding a new Maven dependency, which is a protected action requiring separate user
 * authorization (see workspace {@code CLAUDE.md} — Protected Actions).
 */
@Component
public class AnthropicClownAssistantAdapter implements ClownAssistantPort {

    private static final Logger log = LoggerFactory.getLogger(AnthropicClownAssistantAdapter.class);

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final String MODEL = "claude-opus-4-8";
    private static final long MAX_TOKENS = 4096L;
    private static final String TOOL_NAME = "propose_character_inputs";
    private static final String SYSTEM_PROMPT_RESOURCE = "clown/system-prompt.md";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String systemPrompt;

    public AnthropicClownAssistantAdapter() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.systemPrompt = loadSystemPrompt();
    }

    @Override
    public ClownChatResult converse(ClownChatCommand command) {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "ANTHROPIC_API_KEY is not set — see keynor-rpg/CLAUDE.md's Clown chat section. "
                            + "This must be provided by the user's local environment; it is never set by an agent.");
        }

        ObjectNode requestBody = buildRequestBody(command);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(60))
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to reach the Anthropic API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling the Anthropic API", e);
        }

        if (response.statusCode() != 200) {
            log.error("Anthropic API returned {}: {}", response.statusCode(), response.body());
            throw new IllegalStateException("Anthropic API call failed with status " + response.statusCode());
        }

        return parseResponse(response.body());
    }

    private ObjectNode buildRequestBody(ClownChatCommand command) {
        ObjectNode body = JsonNodeFactory.instance.objectNode();
        body.put("model", MODEL);
        body.put("max_tokens", MAX_TOKENS);

        ObjectNode thinking = body.putObject("thinking");
        thinking.put("type", "adaptive");

        ObjectNode outputConfig = body.putObject("output_config");
        outputConfig.put("effort", "medium");

        ArrayNode system = body.putArray("system");
        ObjectNode staticBlock = system.addObject();
        staticBlock.put("type", "text");
        staticBlock.put("text", systemPrompt);
        staticBlock.putObject("cache_control").put("type", "ephemeral");

        ObjectNode dynamicBlock = system.addObject();
        dynamicBlock.put("type", "text");
        dynamicBlock.put("text", "Current mode: " + command.mode() + ". Current app UI language: "
                + command.language() + ".");

        ArrayNode messages = body.putArray("messages");
        for (ClownChatMessage message : command.history()) {
            ObjectNode messageNode = messages.addObject();
            messageNode.put("role", message.role());
            messageNode.put("content", message.content());
        }

        ArrayNode tools = body.putArray("tools");
        tools.add(ClownToolSchema.buildToolDefinition());

        return body;
    }

    private ClownChatResult parseResponse(String responseBody) {
        JsonNode root;
        try {
            root = objectMapper.readTree(responseBody);
        } catch (IOException e) {
            throw new UncheckedIOException("Malformed Anthropic API response", e);
        }

        if (root.path("stop_reason").asText("").equals("refusal")) {
            return new ClownChatResult(
                    "I'm not able to help with that one — let's get back to building your character.", null);
        }

        StringBuilder reply = new StringBuilder();
        String suggestedInputsJson = null;

        for (JsonNode block : root.path("content")) {
            String type = block.path("type").asText("");
            if (type.equals("text")) {
                if (!reply.isEmpty()) {
                    reply.append("\n\n");
                }
                reply.append(block.path("text").asText(""));
            } else if (type.equals("tool_use") && TOOL_NAME.equals(block.path("name").asText(""))) {
                suggestedInputsJson = block.path("input").toString();
            }
        }

        return new ClownChatResult(reply.toString(), suggestedInputsJson);
    }

    private String loadSystemPrompt() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(SYSTEM_PROMPT_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource: " + SYSTEM_PROMPT_RESOURCE);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load Clown system prompt", e);
        }
    }
}
