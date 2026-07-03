package com.keynor.rpg.domain.model;

/**
 * New group (rpg-14): trainable sensory-organ layer, sibling of {@link BodyStructure} inside
 * {@link PhysicalTraits}. All three fields are 1-9, neutral 5.
 *
 * <p>Each field now drives its own sense independently — {@code eyesSensitivity} feeds
 * {@link PlayableCharacter#getSight()}, {@code earsSensitivity} feeds
 * {@link PlayableCharacter#getHearing()}, {@code noseSensitivity} feeds
 * {@link PlayableCharacter#getSmell()} — replacing the pre-rpg-14 shared formula where all
 * three senses read the same {@code Hippocampus}/{@code NeuralDrive} inputs only.
 */
public class SensorialOrgans {

    private int eyesSensitivity;
    private int earsSensitivity;
    private int noseSensitivity;

    public SensorialOrgans(int eyesSensitivity, int earsSensitivity, int noseSensitivity) {
        this.eyesSensitivity = eyesSensitivity;
        this.earsSensitivity = earsSensitivity;
        this.noseSensitivity = noseSensitivity;
    }

    public static SensorialOrgans defaults() {
        return new SensorialOrgans(5, 5, 5);
    }

    public int getEyesSensitivity() {
        return eyesSensitivity;
    }

    public void setEyesSensitivity(int eyesSensitivity) {
        this.eyesSensitivity = eyesSensitivity;
    }

    public int getEarsSensitivity() {
        return earsSensitivity;
    }

    public void setEarsSensitivity(int earsSensitivity) {
        this.earsSensitivity = earsSensitivity;
    }

    public int getNoseSensitivity() {
        return noseSensitivity;
    }

    public void setNoseSensitivity(int noseSensitivity) {
        this.noseSensitivity = noseSensitivity;
    }
}
