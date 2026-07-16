package com.keynor.rpg.domain.model;

/**
 * Persisted per-node wound state for a single {@link BodyComponent}, keyed by
 * {@link BodyComponent#getName()}. Used by {@link Body#reconstruct} to restore live damage
 * state onto a freshly-built anatomical tree when loading a character from storage.
 */
public record BodyComponentState(String name, int currentHitPoints, int irreversibleDamage) {
}
