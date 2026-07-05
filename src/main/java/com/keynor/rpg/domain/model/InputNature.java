package com.keynor.rpg.domain.model;

/**
 * Classifies how a character-creation input was or can be acquired — introduced alongside the
 * Mind pillar so every new input declares its nature going forward, not only Mind's own fields.
 *
 * <ul>
 *   <li>{@link #BIRTH} — fixed at creation, never changes afterward (e.g. {@link Genetics}
 *       fields, {@link BloodSystem}). Renamed from {@code IMMUTABLE} (rpg-19).</li>
 *   <li>{@link #TRAINED} — starts at a default and changes through training over time (e.g.
 *       {@link BodyComposition}, most {@link BodySystems} fields, and, since rpg-19, every
 *       {@link Knowledge} and {@link Labours} slider).</li>
 *   <li>{@link #EVENTFUL} — acquired through a natural or social event, not genetics or
 *       training, and draws from its own point pool ({@link Mind#getEventPoints()}) rather than
 *       the genetic or training pools. {@link Values} and every {@link Trait} are eventful.</li>
 * </ul>
 *
 * <p>This enum carries no behavior of its own — it exists so the frontend can render a
 * consistent badge per input and so a new input's nature is a deliberate choice recorded once,
 * not left implicit. Per-attribute point costs for the eventful pool are not implemented yet,
 * matching the same deferred-cost precedent already established for the genetic and training
 * pools (see {@link AttributePointBudget}).
 */
public enum InputNature {
    BIRTH,
    TRAINED,
    EVENTFUL
}
