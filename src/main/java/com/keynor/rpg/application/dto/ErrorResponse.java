package com.keynor.rpg.application.dto;

/** Response body for every error mapped by {@code GlobalExceptionHandler}. */
public record ErrorResponse(String message) {
}
