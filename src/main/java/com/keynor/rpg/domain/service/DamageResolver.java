package com.keynor.rpg.domain.service;

/**
 * The final step of the Special Attack Test design (2026-07-20), shared by every attack type
 * once its own raw damage (Db) and the target's protection (P) are known:
 * {@code FinalDamage = (Db - P) * Ae} if {@code Db > P}, otherwise {@code 0}. {@code Ae} (Area of
 * Effect) measures the severity of the wound once protection is overcome, and comes from the
 * weapon/attack profile, not the target.
 */
public class DamageResolver {

    public double finalDamage(double rawDamage, double protection, double areaOfEffect) {
        if (rawDamage <= protection) {
            return 0;
        }
        return (rawDamage - protection) * areaOfEffect;
    }
}
