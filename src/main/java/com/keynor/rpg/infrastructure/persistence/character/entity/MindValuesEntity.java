package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mind_values")
public class MindValuesEntity {

    @Id
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "ego", nullable = false)
    private int ego;

    @Column(name = "loyalty", nullable = false)
    private int loyalty;

    @Column(name = "organization", nullable = false)
    private int organization;

    @Column(name = "freedom", nullable = false)
    private int freedom;

    @Column(name = "society", nullable = false)
    private int society;

    @Column(name = "divinity", nullable = false)
    private int divinity;

    @Column(name = "truth", nullable = false)
    private int truth;

    @Column(name = "knowledge", nullable = false)
    private int knowledge;

    @Column(name = "nature", nullable = false)
    private int nature;

    @Column(name = "morality", nullable = false)
    private int morality;

    @Column(name = "tradition", nullable = false)
    private int tradition;

    @Column(name = "justice", nullable = false)
    private int justice;

    @Column(name = "progress", nullable = false)
    private int progress;

    @Column(name = "peace", nullable = false)
    private int peace;

    protected MindValuesEntity() {
    }

    public MindValuesEntity(Long characterId, int ego, int loyalty, int organization, int freedom, int society,
                             int divinity, int truth, int knowledge, int nature, int morality, int tradition,
                             int justice, int progress, int peace) {
        this.characterId = characterId;
        this.ego = ego;
        this.loyalty = loyalty;
        this.organization = organization;
        this.freedom = freedom;
        this.society = society;
        this.divinity = divinity;
        this.truth = truth;
        this.knowledge = knowledge;
        this.nature = nature;
        this.morality = morality;
        this.tradition = tradition;
        this.justice = justice;
        this.progress = progress;
        this.peace = peace;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public int getEgo() {
        return ego;
    }

    public int getLoyalty() {
        return loyalty;
    }

    public int getOrganization() {
        return organization;
    }

    public int getFreedom() {
        return freedom;
    }

    public int getSociety() {
        return society;
    }

    public int getDivinity() {
        return divinity;
    }

    public int getTruth() {
        return truth;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public int getNature() {
        return nature;
    }

    public int getMorality() {
        return morality;
    }

    public int getTradition() {
        return tradition;
    }

    public int getJustice() {
        return justice;
    }

    public int getProgress() {
        return progress;
    }

    public int getPeace() {
        return peace;
    }
}
