package com.keynor.rpg.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BodyComponent {

    private final String name;
    private final int maxHitPoints;
    private final int naturalResistance;
    private final boolean vital;
    private final int hitDifficulty;
    private final CascadeRelation cascadeRelation;
    private final double slipChance;
    private final double slipDamageFraction;

    private int currentHitPoints;
    private int irreversibleDamage;

    private BodyComponent parent;
    private final List<BodyComponent> children = new ArrayList<>();

    private BodyComponent(String name, int maxHitPoints, int naturalResistance, boolean vital, int hitDifficulty,
                           CascadeRelation cascadeRelation, double slipChance, double slipDamageFraction) {
        this.name = name;
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
        this.naturalResistance = naturalResistance;
        this.vital = vital;
        this.hitDifficulty = hitDifficulty;
        this.cascadeRelation = cascadeRelation;
        this.slipChance = slipChance;
        this.slipDamageFraction = slipDamageFraction;
    }

    public static BodyComponent structural(String name, int maxHitPoints, int naturalResistance, boolean vital,
                                            int hitDifficulty) {
        return new BodyComponent(name, maxHitPoints, naturalResistance, vital, hitDifficulty,
                CascadeRelation.NONE, 0, 0);
    }

    public static BodyComponent protectedInternal(String name, int maxHitPoints, int naturalResistance,
                                                    boolean vital, int hitDifficulty) {
        return new BodyComponent(name, maxHitPoints, naturalResistance, vital, hitDifficulty,
                CascadeRelation.PROTECTED_INTERNAL, 0, 0);
    }

    public static BodyComponent attachedAppendage(String name, int maxHitPoints, int naturalResistance,
                                                    boolean vital, int hitDifficulty,
                                                    double slipChance, double slipDamageFraction) {
        return new BodyComponent(name, maxHitPoints, naturalResistance, vital, hitDifficulty,
                CascadeRelation.ATTACHED_APPENDAGE, slipChance, slipDamageFraction);
    }

    public void addChild(BodyComponent child) {
        child.parent = this;
        children.add(child);
    }

    public void applyDamage(int amount, boolean irreversible) {
        currentHitPoints = Math.max(0, currentHitPoints - amount);
        if (irreversible) {
            irreversibleDamage = Math.min(maxHitPoints, irreversibleDamage + amount);
        }
    }

    public int getReversibleDamage() {
        return (maxHitPoints - irreversibleDamage) - currentHitPoints;
    }

    public String getName() {
        return name;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public int getNaturalResistance() {
        return naturalResistance;
    }

    public boolean isVital() {
        return vital;
    }

    public int getHitDifficulty() {
        return hitDifficulty;
    }

    public CascadeRelation getCascadeRelation() {
        return cascadeRelation;
    }

    public double getSlipChance() {
        return slipChance;
    }

    public double getSlipDamageFraction() {
        return slipDamageFraction;
    }

    public int getIrreversibleDamage() {
        return irreversibleDamage;
    }

    public BodyComponent getParent() {
        return parent;
    }

    public List<BodyComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
