package com.keynor.rpg.application.dto;

/** Same Body/Mind shape as {@link CharacterPreviewRequest}, plus the new character's name. */
public record CreateCharacterRequest(String name, BodyPreviewRequest body, MindPreviewRequest mind) {
}
