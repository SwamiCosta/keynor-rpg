package com.keynor.rpg.domain.model;

/**
 * Groups the four physiological systems that support the body's physical performance.
 * Sits alongside {@link Biomechanics} inside {@link Body} — both are accessed by
 * {@link PlayableCharacter}'s attribute computation methods.
 *
 * <p>{@link BloodSystem} is genetic (immutable). {@link CardiacSystem},
 * {@link PulmonarySystem}, and {@link NervousSystem} are trainable (mutable setters).
 */
public class BodySystems {

    private final BloodSystem bloodSystem;
    private final CardiacSystem cardiacSystem;
    private final PulmonarySystem pulmonarySystem;
    private final NervousSystem nervousSystem;

    public BodySystems(BloodSystem bloodSystem, CardiacSystem cardiacSystem,
                        PulmonarySystem pulmonarySystem, NervousSystem nervousSystem) {
        this.bloodSystem = bloodSystem;
        this.cardiacSystem = cardiacSystem;
        this.pulmonarySystem = pulmonarySystem;
        this.nervousSystem = nervousSystem;
    }

    public static BodySystems defaults() {
        return new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), NervousSystem.defaults());
    }

    public BloodSystem getBloodSystem() {
        return bloodSystem;
    }

    public CardiacSystem getCardiacSystem() {
        return cardiacSystem;
    }

    public PulmonarySystem getPulmonarySystem() {
        return pulmonarySystem;
    }

    public NervousSystem getNervousSystem() {
        return nervousSystem;
    }
}
