package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.BodyComponent;
import com.keynor.rpg.domain.model.CascadeRelation;
import com.keynor.rpg.domain.port.out.RandomSource;

import java.util.List;
import java.util.Optional;

public class BodyCascadeResolver {

    private final RandomSource randomSource;

    public BodyCascadeResolver(RandomSource randomSource) {
        this.randomSource = randomSource;
    }

    public int resistedDamage(BodyComponent protector, int incomingDamage) {
        return Math.max(0, incomingDamage - protector.getNaturalResistance());
    }

    /**
     * Picks which protected internal child absorbs an untargeted overflow hit, weighted by
     * each candidate's max hit points. Precision attacks against a specific organ skip this
     * pick entirely and resolve {@link #resistedDamage} directly against the declared target.
     */
    public Optional<BodyComponent> pickOverflowTarget(BodyComponent protector) {
        List<BodyComponent> candidates = protector.getChildren().stream()
                .filter(child -> child.getCascadeRelation() == CascadeRelation.PROTECTED_INTERNAL)
                .toList();

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        int totalWeight = candidates.stream().mapToInt(BodyComponent::getMaxHitPoints).sum();
        int roll = randomSource.nextInt(totalWeight);

        int cumulative = 0;
        for (BodyComponent candidate : candidates) {
            cumulative += candidate.getMaxHitPoints();
            if (roll < cumulative) {
                return Optional.of(candidate);
            }
        }
        return Optional.of(candidates.get(candidates.size() - 1));
    }

    public boolean appendageSlips(BodyComponent appendage) {
        return randomSource.nextDouble() < appendage.getSlipChance();
    }

    public int slippedDamage(BodyComponent appendage, int incomingDamage) {
        return Math.round(incomingDamage * (float) appendage.getSlipDamageFraction());
    }
}
