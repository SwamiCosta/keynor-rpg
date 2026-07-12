package com.keynor.rpg.domain.model;

/**
 * A display language for backend-sourced text. Mirrors {@code keynor-core}'s identical
 * {@code Language} enum for cross-project consistency — see that project's own
 * {@code domain.model.shared.Language}. In this project, the only backend-sourced text today is
 * {@link AttributeBreakdown.Term#label()}; see {@link TermLabelTranslations}.
 */
public enum Language {
    EN, PT
}
