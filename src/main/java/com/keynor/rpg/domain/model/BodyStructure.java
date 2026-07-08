package com.keynor.rpg.domain.model;

/**
 * New group (rpg-14): body-shape/skin layer, sibling of {@link SensorialOrgans} inside
 * {@link PhysicalTraits}.
 *
 * <p>{@code skinThickness} (moved from {@link Genetics} in rpg-14 — same field, same
 * immutable/genetic nature, only its owning class changed) is 1-7, neutral 3; the domain
 * accepts the full range for future non-human races even though the frontend currently locks
 * the human character-creation slider to [2-4]. Feeds {@link PlayableCharacter#getThermalResistance()}
 * and {@link PlayableCharacter#getSoftTissueDurability()} (renamed from Durability in rpg-21).
 *
 * <p>{@code shapeAesthetics} (new, trainable, 1-9 neutral 5) spans Repulsive(1) to
 * Attractive(9) — feeds the new social attributes ({@link PlayableCharacter#getIntimidation()},
 * {@link PlayableCharacter#getDiplomacy()}, {@link PlayableCharacter#getEnfactuation()},
 * {@link PlayableCharacter#getCommand()}). {@code cellularHealth} (new, trainable, 1-9
 * neutral 5) feeds the three biological-defense resistances and {@link PlayableCharacter#getFatGainRate()}.
 */
public class BodyStructure {

    private final int skinThickness;
    private int shapeAesthetics;
    private int cellularHealth;

    public BodyStructure(int skinThickness, int shapeAesthetics, int cellularHealth) {
        this.skinThickness = skinThickness;
        this.shapeAesthetics = shapeAesthetics;
        this.cellularHealth = cellularHealth;
    }

    public static BodyStructure defaults() {
        return new BodyStructure(3, 5, 5);
    }

    public int getSkinThickness() {
        return skinThickness;
    }

    public int getShapeAesthetics() {
        return shapeAesthetics;
    }

    public void setShapeAesthetics(int shapeAesthetics) {
        this.shapeAesthetics = shapeAesthetics;
    }

    public int getCellularHealth() {
        return cellularHealth;
    }

    public void setCellularHealth(int cellularHealth) {
        this.cellularHealth = cellularHealth;
    }
}
