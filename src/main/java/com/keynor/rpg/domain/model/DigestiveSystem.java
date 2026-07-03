package com.keynor.rpg.domain.model;

/**
 * New system (rpg-13): trainable digestive/metabolic layer, sibling of {@link CardiacSystem}/
 * {@link PulmonarySystem}/{@link HormonalSystem} inside {@link BodySystems}. All three fields
 * are 1-9, neutral 5.
 *
 * <p>{@code nutrientAbsorption} feeds {@link PlayableCharacter#getStaminaPool()} and
 * {@link PlayableCharacter#getStarvationResistance()}. {@code impurityCleaning} feeds
 * {@link PlayableCharacter#getFoodPoisoningAlcoholResistance()}. {@code ketosisQuality} feeds
 * {@link PlayableCharacter#getDehydrationResistance()} and
 * {@link PlayableCharacter#getStarvationResistance()}.
 */
public class DigestiveSystem {

    private int nutrientAbsorption;
    private int impurityCleaning;
    private int ketosisQuality;

    public DigestiveSystem(int nutrientAbsorption, int impurityCleaning, int ketosisQuality) {
        this.nutrientAbsorption = nutrientAbsorption;
        this.impurityCleaning = impurityCleaning;
        this.ketosisQuality = ketosisQuality;
    }

    public static DigestiveSystem defaults() {
        return new DigestiveSystem(5, 5, 5);
    }

    public int getNutrientAbsorption() {
        return nutrientAbsorption;
    }

    public void setNutrientAbsorption(int nutrientAbsorption) {
        this.nutrientAbsorption = nutrientAbsorption;
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
