package com.keynor.rpg.domain.model;

/**
 * New system (rpg-13): trainable digestive/metabolic layer, sibling of {@link CardiacSystem}/
 * {@link PulmonarySystem}/{@link HormonalGlandularSystem} inside {@link BodySystems}. All three
 * fields are 1-9, neutral 5.
 *
 * <p>{@code digestiveAbsorption} (renamed from {@code nutrientAbsorption} in Delta V4) feeds
 * {@link PlayableCharacter#getStaminaPool()}, {@link PlayableCharacter#getStarvationResistance()},
 * {@link PlayableCharacter#getFatGainRate()}, {@link PlayableCharacter#getMuscleGainRate()}, and
 * a light negative term on {@link PlayableCharacter#getFoodPoisoningAlcoholResistance()}.
 * {@code impurityCleaning} feeds {@link PlayableCharacter#getFoodPoisoningAlcoholResistance()}.
 * {@code ketosisEfficiency} (renamed from {@code ketosisQuality}) feeds
 * {@link PlayableCharacter#getDehydrationResistance()} and
 * {@link PlayableCharacter#getStarvationResistance()}.
 */
public class DigestiveSystem {

    private int digestiveAbsorption;
    private int impurityCleaning;
    private int ketosisEfficiency;

    public DigestiveSystem(int digestiveAbsorption, int impurityCleaning, int ketosisEfficiency) {
        this.digestiveAbsorption = digestiveAbsorption;
        this.impurityCleaning = impurityCleaning;
        this.ketosisEfficiency = ketosisEfficiency;
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

    public int getKetosisEfficiency() {
        return ketosisEfficiency;
    }

    public void setKetosisEfficiency(int ketosisEfficiency) {
        this.ketosisEfficiency = ketosisEfficiency;
    }
}
