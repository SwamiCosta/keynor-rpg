package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "body_neural_system")
public class BodyNeuralSystemEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "neural_drive", nullable = false)
    private int neuralDrive;

    @Column(name = "neuromuscular_efficiency", nullable = false)
    private int neuromuscularEfficiency;

    @Column(name = "cerebral_capacity", nullable = false)
    private int cerebralCapacity;

    @Column(name = "synapsis_quality", nullable = false)
    private int synapsisQuality;

    @Column(name = "hippocampus", nullable = false)
    private int hippocampus;

    @Column(name = "thalamus", nullable = false)
    private int thalamus;

    @Column(name = "hypothalamus", nullable = false)
    private int hypothalamus;

    @Column(name = "amygdala_and_cingulum", nullable = false)
    private int amygdalaAndCingulum;

    @Column(name = "immunity", nullable = false)
    private int immunity;

    @Column(name = "agility", nullable = false)
    private int agility;

    @Column(name = "precision", nullable = false)
    private int precision;

    @Column(name = "noetic_plexus", nullable = false)
    private int noeticPlexus;

    @Column(name = "phaxic_cerebelum", nullable = false)
    private int phaxicCerebelum;

    protected BodyNeuralSystemEntity() {
    }

    public BodyNeuralSystemEntity(Long characterId, int neuralDrive, int neuromuscularEfficiency,
                                   int cerebralCapacity, int synapsisQuality, int hippocampus, int thalamus,
                                   int hypothalamus, int amygdalaAndCingulum, int immunity, int agility,
                                   int precision, int noeticPlexus, int phaxicCerebelum) {
        this.characterId = characterId;
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
        this.phaxicCerebelum = phaxicCerebelum;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getNeuralDrive() {
        return neuralDrive;
    }

    public int getNeuromuscularEfficiency() {
        return neuromuscularEfficiency;
    }

    public int getCerebralCapacity() {
        return cerebralCapacity;
    }

    public int getSynapsisQuality() {
        return synapsisQuality;
    }

    public int getHippocampus() {
        return hippocampus;
    }

    public int getThalamus() {
        return thalamus;
    }

    public int getHypothalamus() {
        return hypothalamus;
    }

    public int getAmygdalaAndCingulum() {
        return amygdalaAndCingulum;
    }

    public int getImmunity() {
        return immunity;
    }

    public int getAgility() {
        return agility;
    }

    public int getPrecision() {
        return precision;
    }

    public int getNoeticPlexus() {
        return noeticPlexus;
    }

    public int getPhaxicCerebelum() {
        return phaxicCerebelum;
    }
}
