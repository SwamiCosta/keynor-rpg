package com.keynor.rpg.domain.model.clown;

/**
 * The three invocation modes Clown supports — see {@code clown.md}'s "Three invocation modes"
 * section and the mirrored description baked into the system prompt resource
 * ({@code clown/system-prompt.md}). The mode does not change the API contract, only the
 * instructions Clown follows within a single, shared conversational flow.
 */
public enum ClownMode {
    INTERACTIVE, TEMPLATE, PROMPT
}
