package com.keynor.rpg.domain.model;

/**
 * New system (rpg-13): trainable digestive/metabolic layer, sibling of {@link CardiacSystem}/
 * {@link PulmonarySystem}/{@link HormonalSystem} inside {@link BodySystems}. All three fields
 * are 1-9, neutral 5.
 *
 * <p>{@code digestiveAbsorption} (renamed from {@code nutrientAbsorption} in Delta V4) feeds
 * {@link PlayableCharacter#getStaminaPool()}, {@link PlayableCharacter#getStarvationResistance()},
 * {@link PlayableCharacter#getFatGainRate()}, {@link PlayableCharacter#getMuscleGainRate()}, and
 * (new, Delta V4) a light negative term on
 * {@link PlayableCharacter#getFoodPoisoningAlcoholResistance()}. {@code impurityCleaning} feeds
 * {@link PlayableCharacter#getFoodPoisoningAlcoholResistance()}. {@code ketosisQuality} feeds
 * {@link PlayableCharacter#getDehydrationResistance()} and
 * {@link PlayableCharacter#getStarvationResistance()}.
 */
public class DigestiveSystem {

    private int digestiveAbsorption;
    private int impurityCleaning;
    private int ketosisQuality;

    public DigestiveSystem(int digestiveAbsorption, int impurityCleaning, int ketosisQuality) {
        this.digestiveAbsorption = digestiveAbsorption;
        this.impurityCleaning = impurityCleaning;
        this.ketosisQuality = ketosisQuality;
    }

    public static DigestiveSystem defaults() {
        return new DigestiveSystem(5, 5, 5);
    }

    public int getDigestiveAbsorption() {
        return digestiveAbsorption;
    }

    public void setDigestiveAbsorption(int digestiveAbsorption) {
        this.digestiveAbsorption = digestiveAbsorption;
    }

    public int getImpurityCleaning() {
        return impurityCleaning;
    }

    public void setImpurityCleaning(int impurityCleaning) {
        this.impurityCleaning = impurityCleaning;
    }

    public int getKetosisQuality() {
        return ketosisQuality;
    }

    public void setKetosisQuality(int ketosisQuality) {
        this.ketosisQuality = ketosisQuality;
    }
}
