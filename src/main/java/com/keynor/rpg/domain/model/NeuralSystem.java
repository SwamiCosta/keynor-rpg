package com.keynor.rpg.domain.model;

/**
 * Trainable neural layer — renamed from {@code NervousSystem} in rpg-13, which also folded
 * the former {@code SpatialIntelligence} group into this one ({@code hippocampus} was
 * {@code perception}; {@code agility}/{@code precision} moved here unchanged) per the user's
 * "Neural System" consolidation. All ten fields are 1-9, neutral 5 (formulas use the
 * {@code value - 5} deviation), and all are mutable/trainable for now.
 *
 * <p>{@code neuromuscularEfficiency} feeds {@link PlayableCharacter#getStrength()}/
 * {@link PlayableCharacter#getSpeed()}. {@code hippocampus}/{@code neuralDrive} feed the
 * senses ({@link PlayableCharacter#getSight()}, etc.) and {@link PlayableCharacter#getBalance()}.
 * {@code agility}/{@code precision} feed {@link PlayableCharacter#getEvasion()},
 * {@link PlayableCharacter#getAcrobatics()}, {@link PlayableCharacter#getMeleeAccuracy()},
 * {@link PlayableCharacter#getAim()}. {@code cerebralCapacity}/{@code synapsisQuality} feed
 * the new cognitive attributes ({@link PlayableCharacter#getMemoryPool()},
 * {@link PlayableCharacter#getReasoning()}, {@link PlayableCharacter#getShortMemory()}).
 * {@code amygdalaAndCingulum} feeds {@link PlayableCharacter#getMentalHealthPool()} /
 * {@link PlayableCharacter#getWill()} / {@link PlayableCharacter#getStressResistance()} /
 * {@link PlayableCharacter#getDiseaseResistance()}. {@code hypothalamus} feeds several
 * metabolic/survival attributes. {@code immunity} feeds the biological-defense attributes.
 *
 * <p>{@code thalamus} (added Delta V4): split off {@code hippocampus} to isolate external
 * sensory/perception filtering from memory/cognition — {@code hippocampus} now feeds only the
 * memory attributes ({@link PlayableCharacter#getMemoryPool()},
 * {@link PlayableCharacter#getShortMemory()}), while {@code thalamus} feeds the senses
 * ({@link PlayableCharacter#getSight()}, etc.), {@link PlayableCharacter#getAim()}, and
 * {@link PlayableCharacter#getBalance()}.
 *
 * <p>{@code noeticPlexus} ("a network of arcane nerves capable of perceiving and sensing
 * magical signals") is a magical organ only magical races possess — absent (0) for the human
 * default template, which locks its slider disabled in the frontend. When present, its neutral
 * point is 6 (not 5) and it feeds {@link PlayableCharacter#getSixthSense()} exclusively, using a
 * wider weight (8) than the standard 1-9 traits.
 */
public class NeuralSystem {

    private int neuralDrive;
    private int neuromuscularEfficiency;
    private int cerebralCapacity;
    private int synapsisQuality;
    private int hippocampus;
    private int thalamus;
    private int hypothalamus;
    private int amygdalaAndCingulum;
    private int immunity;
    private int agility;
    private int precision;
    private int noeticPlexus;

    public NeuralSystem(int neuralDrive, int neuromuscularEfficiency, int cerebralCapacity, int synapsisQuality,
                         int hippocampus, int thalamus, int hypothalamus, int amygdalaAndCingulum, int immunity,
                         int agility, int precision, int noeticPlexus) {
        this.neuralDrive = neuralDrive;
        this.neuromuscularEfficiency = neuromuscularEfficiency;
        this.cerebralCapacity = cerebralCapacity;
        this.synapsisQuality = synapsisQuality;
        this.hippocampus = hippocampus;
        this.thalamus = thalamus;
        this.hypothalamus = hypothalamus;
        this.amygdalaAndCingulum = amygdalaAndCingulum;
        this.immunity = immunity;
        this.agility = agility;
        this.precision = precision;
        this.noeticPlexus = noeticPlexus;
    }

    public static NeuralSystem defaults() {
        return new NeuralSystem(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0);
    }

    public int getNeuralDrive() {
        return neuralDrive;
    }

    public void setNeuralDrive(int neuralDrive) {
        this.neuralDrive = neuralDrive;
    }

    public int getNeuromuscularEfficiency() {
        return neuromuscularEfficiency;
    }

    public void setNeuromuscularEfficiency(int neuromuscularEfficiency) {
        this.neuromuscularEfficiency = neuromuscularEfficiency;
    }

    public int getCerebralCapacity() {
        return cerebralCapacity;
    }

    public void setCerebralCapacity(int cerebralCapacity) {
        this.cerebralCapacity = cerebralCapacity;
    }

    public int getSynapsisQuality() {
        return synapsisQuality;
    }

    public void setSynapsisQuality(int synapsisQuality) {
        this.synapsisQuality = synapsisQuality;
    }

    public int getHippocampus() {
        return hippocampus;
    }

    public void setHippocampus(int hippocampus) {
        this.hippocampus = hippocampus;
    }

    public int getThalamus() {
        return thalamus;
    }

    public void setThalamus(int thalamus) {
        this.thalamus = thalamus;
    }

    public int getHypothalamus() {
        return hypothalamus;
    }

    public void setHypothalamus(int hypothalamus) {
        this.hypothalamus = hypothalamus;
    }

    public int getAmygdalaAndCingulum() {
        return amygdalaAndCingulum;
    }

    public void setAmygdalaAndCingulum(int amygdalaAndCingulum) {
        this.amygdalaAndCingulum = amygdalaAndCingulum;
    }

    public int getImmunity() {
        return immunity;
    }

    public void setImmunity(int immunity) {
        this.immunity = immunity;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getNoeticPlexus() {
        return noeticPlexus;
    }

    public void setNoeticPlexus(int noeticPlexus) {
        this.noeticPlexus = noeticPlexus;
    }
}
