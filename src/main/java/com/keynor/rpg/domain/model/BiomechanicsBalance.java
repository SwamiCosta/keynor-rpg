package com.keynor.rpg.domain.model;

/**
 * Free, mutable balance coefficients for every {@link Biomechanics} output formula — k1..k9
 * and c match the naming used in the user's design document; kBmr/kActivityCost/kEfficiency
 * are additional coefficients introduced to make the EnergyCost formula concrete, since the
 * document left {@code ActivityCost} and {@code Eficiencia} as named-but-unspecified terms.
 * Most default to 1.0 (neutral multiplier): there is no "scientifically correct" value, only
 * what plays well — same caveat as {@code Body}'s illustrative hit point placeholders.
 * {@code kBoneMass}/{@code kBoneDensity}/{@code kOrganWaterMass} are the exception — their
 * defaults (2.7 / 0.06 / 6.3) were chosen so {@link Biomechanics#getTotalMass()} reproduces
 * the previous hardcoded 70kg human default almost exactly at {@code Genetics.defaults()}.
 */
public class BiomechanicsBalance {

    private double k1 = 1; // Strength
    private double c = 1;  // Strength's leverage term
    private double k2 = 1; // Speed
    private double k3 = 1; // StaminaPool
    private double k4 = 1; // FatigueRate - Kleiber mass term
    private double k5 = 1; // FatigueRate - muscle mass x intensity term
    private double k6 = 1; // FatigueRate - cardiovascular recovery term
    private double kBmr = 1;          // EnergyCost - BMR base (Kleiber)
    private double kActivityCost = 1; // EnergyCost - activity term
    private double kEfficiency = 1;   // EnergyCost - efficiency subtraction
    private double k7 = 1; // Durability - bone density / mesomorphy term
    private double k8 = 1; // Durability - mass inertia term
    private double k9 = 1; // Durability - fat cushion term
    private double kBoneMass = 2.7;        // BoneMass - height^2 base term
    private double kBoneDensity = 0.06;    // BoneMass - density deviation modifier
    private double kOrganWaterMass = 6.3;  // OrganWaterMass - height^2 base term

    public static BiomechanicsBalance defaults() {
        return new BiomechanicsBalance();
    }

    public double getK1() { return k1; }
    public void setK1(double k1) { this.k1 = k1; }

    public double getC() { return c; }
    public void setC(double c) { this.c = c; }

    public double getK2() { return k2; }
    public void setK2(double k2) { this.k2 = k2; }

    public double getK3() { return k3; }
    public void setK3(double k3) { this.k3 = k3; }

    public double getK4() { return k4; }
    public void setK4(double k4) { this.k4 = k4; }

    public double getK5() { return k5; }
    public void setK5(double k5) { this.k5 = k5; }

    public double getK6() { return k6; }
    public void setK6(double k6) { this.k6 = k6; }

    public double getKBmr() { return kBmr; }
    public void setKBmr(double kBmr) { this.kBmr = kBmr; }

    public double getKActivityCost() { return kActivityCost; }
    public void setKActivityCost(double kActivityCost) { this.kActivityCost = kActivityCost; }

    public double getKEfficiency() { return kEfficiency; }
    public void setKEfficiency(double kEfficiency) { this.kEfficiency = kEfficiency; }

    public double getK7() { return k7; }
    public void setK7(double k7) { this.k7 = k7; }

    public double getK8() { return k8; }
    public void setK8(double k8) { this.k8 = k8; }

    public double getK9() { return k9; }
    public void setK9(double k9) { this.k9 = k9; }

    public double getKBoneMass() { return kBoneMass; }
    public void setKBoneMass(double kBoneMass) { this.kBoneMass = kBoneMass; }

    public double getKBoneDensity() { return kBoneDensity; }
    public void setKBoneDensity(double kBoneDensity) { this.kBoneDensity = kBoneDensity; }

    public double getKOrganWaterMass() { return kOrganWaterMass; }
    public void setKOrganWaterMass(double kOrganWaterMass) { this.kOrganWaterMass = kOrganWaterMass; }
}
