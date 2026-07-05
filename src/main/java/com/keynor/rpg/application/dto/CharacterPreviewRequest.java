package com.keynor.rpg.application.dto;

/**
 * Replaces the old, Body-only {@code BiomechanicsPreviewRequest} once the Mind pillar
 * introduced formulas that read both pillars at once (e.g. Reasoning reads both NeuralSystem
 * and Values). A single stateless preview call now needs both groups to resolve every
 * attribute correctly.
 */
public record CharacterPreviewRequest(BodyPreviewRequest body, MindPreviewRequest mind) {
}
