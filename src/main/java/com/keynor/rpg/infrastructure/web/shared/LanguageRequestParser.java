package com.keynor.rpg.infrastructure.web.shared;

import com.keynor.rpg.domain.model.Language;

/** Mirrors {@code keynor-core}'s identical {@code LanguageRequestParser} for cross-project consistency. */
public final class LanguageRequestParser {

    private LanguageRequestParser() {
    }

    public static Language parse(String rawLanguage) {
        try {
            return Language.valueOf(rawLanguage.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported language '" + rawLanguage + "'. Allowed values: EN, PT");
        }
    }
}
