package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TraitTest {

    private PlayableCharacter characterWithValues(Values values) {
        return new PlayableCharacter("test", Body.humanTemplate(), Mind.previewTemplate(values,
                Erudition.defaults(), Personality.defaults(), Labours.defaults(), GeneralPersonality.defaults()));
    }

    @Test
    void everyTrait_isEventful() {
        for (Trait trait : Trait.values()) {
            assertThat(trait.getNature()).isEqualTo(InputNature.EVENTFUL);
        }
    }

    @Test
    void everyTrait_isGroupedByItsOwnConcern_everyGroupHasAtLeastTheBaseAdvancedPair() {
        // 14 base/advanced pairs (28) + 12 standalone concern-threshold traits added later.
        assertThat(Trait.values()).hasSize(40);
        for (TraitGroup group : TraitGroup.values()) {
            long count = java.util.Arrays.stream(Trait.values()).filter(trait -> trait.getGroup() == group).count();
            assertThat(count).as("group %s should have at least the original base/advanced pair", group)
                    .isGreaterThanOrEqualTo(2);
        }
    }

    @Test
    void baseTrait_prerequisiteMet_whenLinkedConcernAtDefault() {
        Values values = Values.defaults(); // Ego defaults to 1
        PlayableCharacter character = characterWithValues(values);

        assertThat(Trait.SELF_SACRIFICE.prerequisitesMet(character)).isTrue();
    }

    @Test
    void baseTrait_prerequisiteNotMet_whenLinkedConcernMovedAwayFromDefault() {
        Values values = Values.defaults();
        values.setEgo(3);
        PlayableCharacter character = characterWithValues(values);

        assertThat(Trait.SELF_SACRIFICE.prerequisitesMet(character)).isFalse();
    }

    @Test
    void advancedTrait_prerequisiteNotMet_withoutItsBaseTraitSelected() {
        PlayableCharacter character = characterWithValues(Values.defaults());

        assertThat(Trait.SUICIDAL.prerequisitesMet(character)).isFalse();
    }

    @Test
    void advancedTrait_prerequisiteMet_onceBaseTraitSelected() {
        Values values = Values.defaults();
        PlayableCharacter character = characterWithValues(values);
        character.getMind().getPersonality().select(Trait.SELF_SACRIFICE, character);

        assertThat(Trait.SUICIDAL.prerequisitesMet(character)).isTrue();
    }

    @Test
    void applyForcedValue_forcesTheLinkedConcernToZero() {
        Values values = Values.defaults();

        Trait.SELF_SACRIFICE.applyForcedValue(values);

        assertThat(values.getEgo()).isZero();
    }

    @Test
    void applyForcedValue_isNoOpForAdvancedTraits() {
        Values values = Values.defaults();

        Trait.SUICIDAL.applyForcedValue(values);

        assertThat(values.getEgo()).isEqualTo(1);
    }

    @Test
    void knowledgePointsModifier_onlyIliterateAndOrphanMindAreNonZero() {
        assertThat(Trait.ILLITERATE.getKnowledgePointsModifier()).isEqualTo(-1);
        assertThat(Trait.ORPHAN_MIND.getKnowledgePointsModifier()).isEqualTo(1);
        assertThat(Trait.SELF_SACRIFICE.getKnowledgePointsModifier()).isZero();
    }

    @Test
    void labourPointsModifier_onlyConservativeAndLuddite() {
        assertThat(Trait.CONSERVATIVE.getLabourPointsModifier()).isEqualTo(1);
        assertThat(Trait.LUDDITE.getLabourPointsModifier()).isEqualTo(1);
        assertThat(Trait.SELF_SACRIFICE.getLabourPointsModifier()).isZero();
    }
}
