package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.BodyComponent;
import com.keynor.rpg.domain.model.CascadeRelation;
import com.keynor.rpg.domain.port.out.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BodyCascadeResolver {

    private static final Logger log = LoggerFactory.getLogger(BodyCascadeResolver.class);

    private final RandomSource randomSource;

    public BodyCascadeResolver(RandomSource randomSource) {
        this.randomSource = randomSource;
    }

    public int resistedDamage(BodyComponent protector, int incomingDamage) {
        int resisted = Math.max(0, incomingDamage - protector.getNaturalResistance());
        log.debug("{} resisted {} of {} incoming damage, {} got through", protector.getName(),
                incomingDamage - resisted, incomingDamage, resisted);
        return resisted;
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
            log.debug("{} has no protected internal children to overflow into", protector.getName());
            return Optional.empty();
        }

        int totalWeight = candidates.stream().mapToInt(BodyComponent::getMaxHitPoints).sum();
        int roll = randomSource.nextInt(totalWeight);

        int cumulative = 0;
        for (BodyComponent candidate : candidates) {
            cumulative += candidate.getMaxHitPoints();
            if (roll < cumulative) {
                log.debug("{} overflow picked {} (roll {} of {})", protector.getName(), candidate.getName(),
                        roll, totalWeight);
                return Optional.of(candidate);
            }
        }
        BodyComponent fallback = candidates.get(candidates.size() - 1);
        log.debug("{} overflow fell back to {} (roll {} of {})", protector.getName(), fallback.getName(),
                roll, totalWeight);
        return Optional.of(fallback);
    }

    public boolean appendageSlips(BodyComponent appendage) {
        double roll = randomSource.nextDouble();
        boolean slipped = roll < appendage.getSlipChance();
        log.debug("{} slip check: roll {} vs chance {} -> {}", appendage.getName(), roll,
                appendage.getSlipChance(), slipped);
        return slipped;
    }

    public int slippedDamage(BodyComponent appendage, int incomingDamage) {
        int damage = Math.round(incomingDamage * (float) appendage.getSlipDamageFraction());
        log.debug("{} took {} slipped damage out of {} incoming", appendage.getName(), damage, incomingDamage);
        return damage;
    }
}
