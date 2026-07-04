package com.keynor.rpg.domain.model;

/**
 * First data group of the {@link Mind} pillar: fourteen priorities a character holds, each
 * 0-5 with a default/neutral of 1 (unlike every {@link Body} trait, which is neutral at its
 * scale's midpoint — these are neutral at their floor, since 1 is both the default and the
 * lowest value reachable without a specific, not-yet-implemented {@link Trait}). All fourteen
 * are {@link InputNature#EVENTFUL}.
 *
 * <p>Each value feeds a same-named {@code XxxConcern} attribute on {@link PlayableCharacter}
 * (a direct mirror, not a deviation-weighted formula — see the additive-standard skill's
 * "direct mirror" exception) and, for four of them (knowledge, truth, loyalty, morality), an
 * additional term on an existing Body-pillar cognitive/social attribute.
 *
 * <p><b>Creation rule (UI-enforced, not a domain invariant):</b> a finished character must set
 * at least two values away from 1, and its single highest value must not be tied with any
 * other value. This is validated entirely client-side, the same way the hormone-neutral lock
 * validates {@code predominantMorphicHormone} without a backend method — see
 * {@code keynor-rpg-client}'s character-creation skill. It is deliberately not enforced here:
 * a player must be able to pass through invalid intermediate states while still adjusting
 * sliders.
 */
public class Values {

    private int ego;
    private int loyalty;
    private int organization;
    private int freedom;
    private int society;
    private int divinity;
    private int truth;
    private int knowledge;
    private int nature;
    private int morality;
    private int tradition;
    private int justice;
    private int progress;
    private int peace;

    public Values(int ego, int loyalty, int organization, int freedom, int society, int divinity, int truth,
                  int knowledge, int nature, int morality, int tradition, int justice, int progress, int peace) {
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

    public static Values defaults() {
        return new Values(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    }

    public int getEgo() { return ego; }
    public void setEgo(int ego) { this.ego = ego; }

    public int getLoyalty() { return loyalty; }
    public void setLoyalty(int loyalty) { this.loyalty = loyalty; }

    public int getOrganization() { return organization; }
    public void setOrganization(int organization) { this.organization = organization; }

    public int getFreedom() { return freedom; }
    public void setFreedom(int freedom) { this.freedom = freedom; }

    public int getSociety() { return society; }
    public void setSociety(int society) { this.society = society; }

    public int getDivinity() { return divinity; }
    public void setDivinity(int divinity) { this.divinity = divinity; }

    public int getTruth() { return truth; }
    public void setTruth(int truth) { this.truth = truth; }

    public int getKnowledge() { return knowledge; }
    public void setKnowledge(int knowledge) { this.knowledge = knowledge; }

    public int getNature() { return nature; }
    public void setNature(int nature) { this.nature = nature; }

    public int getMorality() { return morality; }
    public void setMorality(int morality) { this.morality = morality; }

    public int getTradition() { return tradition; }
    public void setTradition(int tradition) { this.tradition = tradition; }

    public int getJustice() { return justice; }
    public void setJustice(int justice) { this.justice = justice; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getPeace() { return peace; }
    public void setPeace(int peace) { this.peace = peace; }
}
