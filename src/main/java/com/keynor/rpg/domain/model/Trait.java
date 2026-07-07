package com.keynor.rpg.domain.model;

/**
 * A selectable character feature (checkbox in the UI) — the "Trait" input type. A trait term is
 * {@code weight x (hasTrait ? 1 : 0)}, a 0/1 input with neutral 0. Every constant is
 * {@link InputNature#EVENTFUL}.
 *
 * <p><b>Rewritten in rpg-19:</b> {@code Trait} no longer holds Erudition knowledge (see
 * {@link Knowledge}, now leveled sliders) — it exclusively holds 14 base/advanced pairs (28
 * constants), one pair per {@link Values} concern. A base trait's prerequisite is always "its
 * linked concern sits at exactly its default value (1)" — representing a character whose history
 * around that concern is unremarkable, not one who has already invested in it via the sliders.
 * An advanced trait's prerequisite is always "the character already has the base trait of the
 * same pair". Selecting a base trait forces its linked {@link Values} field to 0 (see
 * {@link #applyForcedValue(Values)}); deselecting it reverts that field back to 1 (see
 * {@link #revertForcedValue(Values)}) — 0 is only ever a valid value while the trait that forced
 * it remains selected (corrected after an initial delta shipped the revert as a no-op, treating
 * the forced value as a one-way commitment — that was a bug, not the intended design).
 *
 * <p><b>A follow-up delta added a second, simpler kind of trait</b> — 12 standalone "invested"
 * traits (e.g. {@code PROTAGONIST}, {@code RELIABLE}, {@code REALITIC}) added to several existing
 * groups, each gated by its own concern sitting at or above a threshold (2 or 4, e.g. "Self
 * Concern >= 4") rather than the base/advanced pair's exact-default/already-selected checks.
 * These never force a {@link Values} field and have no pair relationship with each other or with
 * the group's existing base/advanced pair — each is independent. A concern can naturally never
 * satisfy both a base trait's "== 1" and an invested trait's ">= N" requirement at once, so no
 * explicit mutual-exclusion rule was needed.
 *
 * <p><b>Effect split (explicit product decision):</b> every <i>passive, unconditional</i> bonus
 * a trait grants (e.g. Self Sacrifice's +4 Fear Resistance) is implemented as a real additive
 * term on the affected attribute's formula in {@link PlayableCharacter} — see that class's
 * "Values-trait bonuses" section. Every <i>situational</i> effect (bonuses that only apply
 * against a specific kind of opponent, or stress relief tied to a specific in-fiction action) has
 * no corresponding mechanic in the domain today (there is no resisted-test or stress-event
 * system) and is captured only in {@link #getDescription()} as narrative/tooltip text, not as a
 * formula term.
 *
 * <p>Two of the 28 traits additionally adjust a sibling data group's point budget rather than an
 * attribute: {@link #getKnowledgePointsModifier()} (Iliterate, Orphan Mind) and
 * {@link #getLabourPointsModifier()} (Conservative, Luddite) — see {@link Erudition} and
 * {@link Labours}.
 */
public enum Trait {

    SELF_SACRIFICE(TraitGroup.SELF,
            "Something in this character's past drives them to sacrifice themselves for the "
                    + "greater good. They take on no stress from being wounded.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getEgo() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setEgo(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setEgo(1);
        }
    },
    SUICIDAL(TraitGroup.SELF,
            "The character no longer fears their own death and takes on no stress from coming "
                    + "close to it.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(SELF_SACRIFICE);
        }
    },

    PROTAGONIST(TraitGroup.SELF,
            "The character craves recognition and feels most alive when praised, celebrated, or "
                    + "rewarded — they relieve twice the usual stress from receiving "
                    + "congratulations, flattery, or awards. This same hunger for approval makes "
                    + "them easier to charm and flatter.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getEgo() >= 4;
        }
    },
    EGOTIST(TraitGroup.SELF,
            "The character measures their worth by victory — they relieve twice the usual "
                    + "stress from defeating enemies or succeeding in competitive or high-risk "
                    + "situations.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getEgo() >= 2;
        }
    },

    LONE_WOLF(TraitGroup.FRIENDSHIP,
            "Something in this character's past made them distrustful of allies. They take on no "
                    + "stress when an ally is wounded, and can neither receive nor grant social "
                    + "bonuses or aid to/from allies.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getLoyalty() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setLoyalty(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setLoyalty(1);
        }
    },
    BACKSTABBER(TraitGroup.FRIENDSHIP,
            "The character is actively acting as a double agent, ready to betray their allies. "
                    + "They regain the ability to receive and grant social bonuses/aid, take on "
                    + "no guilt stress from harming an ally, and gain a bonus on any resisted "
                    + "test against an ally or former ally.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(LONE_WOLF);
        }
    },

    RELIABLE(TraitGroup.FRIENDSHIP,
            "The character has built a reputation as someone allies can always count on. They "
                    + "receive and grant a 50% bonus on aid exchanged with allies.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getLoyalty() >= 4;
        }
    },

    REBEL(TraitGroup.ORDER,
            "Something in this character's past made them rebellious, hating orders and "
                    + "institutions. They take on no guilt stress from destroying institutional "
                    + "or group property (though harming innocents as a consequence can still "
                    + "cause guilt), and gain a bonus on resisted tests against executives, "
                    + "guards, or other institution members.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getOrganization() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setOrganization(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setOrganization(1);
        }
    },
    CHAOTIC(TraitGroup.ORDER,
            "The character no longer takes on stress of any kind for committing rule "
                    + "violations.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(REBEL);
        }
    },

    DOMINANT(TraitGroup.FREEDOM,
            "Something in this character's past built a controlling personality that enjoys "
                    + "dominance over others. They take on no guilt stress from blackmailing or "
                    + "manipulating people into acting against their will.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getFreedom() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setFreedom(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setFreedom(1);
        }
    },
    POSSESSIVE(TraitGroup.FREEDOM,
            "The character gains a Will and Mental Health Pool bonus for each servant devoted to "
                    + "them and stripped of their own autonomy (squires, pages, slaves, etc. — "
                    + "paid staff, pets, and general allies do not count). Not implemented as a "
                    + "formula term, since there is no servant-tracking mechanic yet.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(DOMINANT);
        }
    },

    EXPATRIATED(TraitGroup.PATRIOTISM,
            "Something in this character's past made them renounce the idea of community and "
                    + "culture. They take on no shame stress from breaking taboos nor guilt "
                    + "stress from breaking laws, and relieve stress by traveling or spending "
                    + "time away from cities.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getSociety() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setSociety(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setSociety(1);
        }
    },
    ANARCHIST(TraitGroup.PATRIOTISM,
            "The character gains a bonus on resisted tests against politicians, guards, or other "
                    + "servants of the law.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(EXPATRIATED);
        }
    },

    LOYALIST(TraitGroup.PATRIOTISM,
            "The character is considered \"Motivated\" whenever they are on a mission in favor "
                    + "of a social authority.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getSociety() >= 4;
        }
    },

    PAGAN(TraitGroup.SPIRITUAL,
            "Something in this character's past made them hate spiritual devotion. They take on "
                    + "no guilt stress from destroying temple property or attacking/arguing with "
                    + "clergy members (though harming innocent devotees as a consequence can "
                    + "still cause guilt), and gain a bonus on resisted tests against clergy "
                    + "members.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getDivinity() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setDivinity(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setDivinity(1);
        }
    },
    PROFANE(TraitGroup.SPIRITUAL,
            "The character no longer takes on guilt stress from harming innocents as a side "
                    + "effect of destroying religious property or practitioners, and relieves "
                    + "stress by committing destructive acts or theft against temples or clergy. "
                    + "They also gain a Fear Resistance bonus specifically against demons, "
                    + "undead, or other profane creatures, and an Enfactuation bonus specifically "
                    + "toward those same creatures.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(PAGAN);
        }
    },

    CLEAN_VESSEL(TraitGroup.SPIRITUAL,
            "The character has kept their soul untouched by corruption, preserved through "
                    + "spiritual discipline.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getDivinity() >= 4;
        }
    },
    RELIGION_PRACTITIONER(TraitGroup.SPIRITUAL,
            "The character actively practices their faith through ritual and devotion.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getDivinity() >= 2;
        }
    },

    RELATIVIST(TraitGroup.PHILOSOPHY,
            "The character was taught that everything in the world is subjective and there is no "
                    + "absolute truth. They reject philosophy and theoretical discussion under "
                    + "the pretext that everything is a matter of opinion.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getTruth() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setTruth(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setTruth(1);
        }
    },
    PRACTICALIST(TraitGroup.PHILOSOPHY,
            "The character has channeled their relativism into a grounded, practical outlook, "
                    + "shedding the anxiety it once caused.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(RELATIVIST);
        }
    },

    REALITIC(TraitGroup.PHILOSOPHY,
            "The character sees the world exactly as it is, refusing to be fooled by illusions "
                    + "— though this same bluntness makes them a poor liar.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getTruth() >= 4;
        }
    },
    PHILOSOPHER(TraitGroup.PHILOSOPHY,
            "The character has trained their mind through rigorous philosophical study.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getTruth() >= 2;
        }
    },

    ILLITERATE(TraitGroup.ACADEMIC,
            "The character received no schooling and does not value it. They are unable to read "
                    + "or write and have one fewer knowledge point to spend in the Erudition tab. "
                    + "This trait has no selection cost.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getKnowledge() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setKnowledge(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setKnowledge(1);
        }

        @Override
        public int getKnowledgePointsModifier() {
            return -1;
        }
    },
    BOOK_BURNER(TraitGroup.ACADEMIC,
            "The character actively despises schools and academics. They relieve stress by "
                    + "damaging university or school property and gain a bonus on resisted tests "
                    + "against academics.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(ILLITERATE);
        }
    },

    ANTI_NATURALIST(TraitGroup.ENVIRONMENTALISM,
            "The character was raised with no connection to nature and no bond with it. They "
                    + "take on no guilt stress from deforestation or harming animals, and are "
                    + "used to dealing with unsanitary environments.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getNature() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setNature(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setNature(1);
        }
    },
    DEFORESTER(TraitGroup.ENVIRONMENTALISM,
            "The character gains a bonus on resisted tests against animals, indigenous people, "
                    + "plant creatures, or druids. They relieve stress from deforestation or "
                    + "hunting, and gain a bonus from food effects when eating animal meat.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(ANTI_NATURALIST);
        }
    },

    OUTDOOR_LIFESTYLE(TraitGroup.ENVIRONMENTALISM,
            "The character has spent most of their life outdoors, at home in the wild and among "
                    + "animals.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getNature() >= 4;
        }
    },

    RECKLESS(TraitGroup.MORALITY,
            "The character was raised and taught without moral restraint. They resist 5 points "
                    + "of any guilt-stress effect, provided they are acting in favor of some "
                    + "other concern.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getMorality() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setMorality(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setMorality(1);
        }
    },
    NIHILIST(TraitGroup.MORALITY,
            "The character resists 10 points of any guilt-stress effect from committing "
                    + "destructive acts in general, including theft and murder.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(RECKLESS);
        }
    },

    ORPHAN_MIND(TraitGroup.TRADITIONALISM,
            "The character was never encouraged to value their family or the culture of their "
                    + "birthplace. They take on no guilt stress from damaging cultural property "
                    + "or monuments, nor from fighting relatives, and, being unbound by "
                    + "conservatism, have one extra knowledge point to spend in the Erudition "
                    + "tab.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getTradition() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setTradition(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setTradition(1);
        }

        @Override
        public int getKnowledgePointsModifier() {
            return 1;
        }
    },
    PAST_ERASER(TraitGroup.TRADITIONALISM,
            "The character relieves stress by destroying cultural heritage and gains a bonus on "
                    + "resisted tests against clan or family members, including their own "
                    + "relatives.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(ORPHAN_MIND);
        }
    },

    DOG_EAT_DOG(TraitGroup.JUSTICE,
            "The character was raised to disrespect equality and believes the world belongs to "
                    + "the strong. They take on no guilt stress from harming vulnerable people or "
                    + "significantly weaker creatures.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getJustice() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setJustice(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setJustice(1);
        }
    },
    BEYOND_AUTHORITY(TraitGroup.JUSTICE,
            "The character becomes immune to guilt stress from committing any crime.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(DOG_EAT_DOG);
        }
    },

    RETRIBUTION_SEEKER(TraitGroup.JUSTICE,
            "The character is driven to see wrongdoing punished. They gain a +5 bonus on "
                    + "resisted tests against criminals, and relieve stress by capturing or "
                    + "defeating them.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getJustice() >= 4;
        }
    },

    CONSERVATIVE(TraitGroup.PROGRESS,
            "Something in this character's past taught them not to value scientific or "
                    + "technological advancement. They take on no guilt stress from damaging "
                    + "machines, experiment results, or research materials, and gain one extra "
                    + "point to spend in the Labours tab.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getProgress() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setProgress(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setProgress(1);
        }

        @Override
        public int getLabourPointsModifier() {
            return 1;
        }
    },
    LUDDITE(TraitGroup.PROGRESS,
            "The character relieves stress by destroying machines, research materials, or "
                    + "experiment results, gains a bonus on resisted tests against scientists, "
                    + "researchers, experiment-modified creatures, or robots, and gains one more "
                    + "extra point to spend in the Labours tab.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(CONSERVATIVE);
        }

        @Override
        public int getLabourPointsModifier() {
            return 1;
        }
    },

    INVENTOR(TraitGroup.PROGRESS,
            "The character has a natural gift for invention and innovation.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getProgress() >= 2;
        }
    },

    BELLICOSE(TraitGroup.PEACE,
            "Something in this character's past made them aggressive and disillusioned with "
                    + "peace.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getPeace() == 1;
        }

        @Override
        public void applyForcedValue(Values values) {
            values.setPeace(0);
        }

        @Override
        public void revertForcedValue(Values values) {
            values.setPeace(1);
        }
    },
    INSTIGATOR(TraitGroup.PEACE,
            "The character relieves stress by provoking conflict. If attacked by an enemy they "
                    + "provoked, they gain an Evasion and Close Combat bonus against that same "
                    + "enemy.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getPersonality().hasTrait(BELLICOSE);
        }
    },

    PEACEKEEPER(TraitGroup.PEACE,
            "The character seeks harmony above all else, relieving stress by avoiding conflict "
                    + "and resolving situations peacefully — though this gentleness makes them "
                    + "less imposing.") {
        @Override
        public boolean prerequisitesMet(PlayableCharacter character) {
            return character.getMind().getValues().getPeace() >= 4;
        }
    };

    private final TraitGroup group;
    private final String description;

    Trait(TraitGroup group, String description) {
        this.group = group;
        this.description = description;
    }

    public TraitGroup getGroup() {
        return group;
    }

    public InputNature getNature() {
        return InputNature.EVENTFUL;
    }

    /** Narrative/tooltip text — backstory plus any situational (non-formula) effect. */
    public String getDescription() {
        return description;
    }

    /**
     * Whether this trait can currently be selected. Every base trait requires its linked
     * {@link Values} concern to sit at exactly its default (1); every advanced trait requires
     * its pair's base trait to already be selected.
     */
    public boolean prerequisitesMet(PlayableCharacter character) {
        return true;
    }

    /**
     * Applied once, when the trait is selected: forces the linked {@link Values} field to 0.
     * No-op for advanced traits, which don't force a value of their own.
     */
    public void applyForcedValue(Values values) {
        // no-op by default
    }

    /**
     * Applied once, when the trait is deselected: reverts the linked {@link Values} field back
     * to its default (1). No-op for advanced traits. A value of 0 is only ever valid while the
     * base trait that forced it remains selected — see {@link Personality#deselect}.
     */
    public void revertForcedValue(Values values) {
        // no-op by default
    }

    /** Additive adjustment to Erudition's base knowledge-point budget. Zero for most traits. */
    public int getKnowledgePointsModifier() {
        return 0;
    }

    /** Additive adjustment to Labours' base job-point budget. Zero for most traits. */
    public int getLabourPointsModifier() {
        return 0;
    }
}
