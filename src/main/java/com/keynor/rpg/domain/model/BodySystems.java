package com.keynor.rpg.domain.model;

/**
 * Groups the physiological systems that support the body's physical, biological, and (as of
 * rpg-13) cognitive/metabolic performance. Sits alongside {@link Biomechanics} inside
 * {@link Body} — all are accessed by {@link PlayableCharacter}'s attribute computation methods.
 *
 * <p>{@link BloodSystem} is genetic (immutable). {@link CardiacSystem}, {@link PulmonarySystem},
 * {@link NeuralSystem}, {@link HormonalSystem} (added rpg-13), and {@link DigestiveSystem}
 * (added rpg-13) are trainable (mutable setters).
 */
public class BodySystems {

    private final BloodSystem bloodSystem;
    private final CardiacSystem cardiacSystem;
    private final PulmonarySystem pulmonarySystem;
    private final NeuralSystem neuralSystem;
    private final HormonalSystem hormonalSystem;
    private final DigestiveSystem digestiveSystem;

    public BodySystems(BloodSystem bloodSystem, CardiacSystem cardiacSystem, PulmonarySystem pulmonarySystem,
                        NeuralSystem neuralSystem, HormonalSystem hormonalSystem, DigestiveSystem digestiveSystem) {
        this.bloodSystem = bloodSystem;
        this.cardiacSystem = cardiacSystem;
        this.pulmonarySystem = pulmonarySystem;
        this.neuralSystem = neuralSystem;
        this.hormonalSystem = hormonalSystem;
        this.digestiveSystem = digestiveSystem;
    }

    public static BodySystems defaults() {
        return new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(), PulmonarySystem.defaults(),
                NeuralSystem.defaults(), HormonalSystem.defaults(), DigestiveSystem.defaults());
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

    public NeuralSystem getNeuralSystem() {
        return neuralSystem;
    }

    public HormonalSystem getHormonalSystem() {
        return hormonalSystem;
    }

    public DigestiveSystem getDigestiveSystem() {
        return digestiveSystem;
    }
}
