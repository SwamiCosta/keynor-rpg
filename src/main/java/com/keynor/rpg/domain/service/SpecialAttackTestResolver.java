package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.port.out.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Special Attack Test mechanic (2026-07-20): {@code result = attribute - 10 + 1d20}. Unlike
 * the ordinary d100 Test ({@code game-rules.md}'s "Tests" section, ">100 succeeds"), a d20 here
 * only ever shifts the attribute by up to 10 points in either direction — there is no separate
 * success/failure threshold baked into this roll itself, and no critical success/failure
 * mechanic, same "raw roll is never special-cased" rule as the ordinary Test. Every attack
 * resolver (melee/thrown/firearm/bow) calls this once per attribute it needs (Tmd, Tstr, Taim,
 * Tsp, Tpst) and interprets the result against its own hit-check threshold.
 */
public class SpecialAttackTestResolver {

    private static final Logger log = LoggerFactory.getLogger(SpecialAttackTestResolver.class);

    private final RandomSource randomSource;

    public SpecialAttackTestResolver(RandomSource randomSource) {
        this.randomSource = randomSource;
    }

    public double roll(double attributeValue) {
        int d20 = randomSource.nextInt(20) + 1;
        double result = attributeValue - 10 + d20;
        log.debug("Special Attack Test: attribute={}, d20={}, result={}", attributeValue, d20, result);
        return result;
    }
}
