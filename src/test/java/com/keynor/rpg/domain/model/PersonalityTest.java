package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonalityTest {

    private PlayableCharacter characterWithValues(Values values) {
        return new PlayableCharacter("test", Body.humanTemplate(), Mind.previewTemplate(values,
                Erudition.defaults(), Personality.defaults(), Labours.defaults()));
    }

    @Test
    void defaults_hasNoSelectedTraits() {
        assertThat(Personality.defaults().getSelectedTraits()).isEmpty();
    }

    @Test
    void select_appliesForcedValueImmediately() {
        PlayableCharacter character = characterWithValues(Values.defaults());

        character.getMind().getPersonality().select(Trait.SELF_SACRIFICE, character);

        assertThat(character.getMind().getValues().getEgo()).isZero();
        assertThat(character.getMind().getPersonality().hasTrait(Trait.SELF_SACRIFICE)).isTrue();
    }

    @Test
    void select_withoutPrerequisitesMet_throws() {
        Values values = Values.defaults();
        values.setEgo(3);
        PlayableCharacter character = characterWithValues(values);

        assertThatThrownBy(() -> character.getMind().getPersonality().select(Trait.SELF_SACRIFICE, character))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deselect_doesNotRevertTheForcedValue() {
        PlayableCharacter character = characterWithValues(Values.defaults());
        character.getMind().getPersonality().select(Trait.SELF_SACRIFICE, character);

        character.getMind().getPersonality().deselect(Trait.SELF_SACRIFICE);

        assertThat(character.getMind().getPersonality().hasTrait(Trait.SELF_SACRIFICE)).isFalse();
        assertThat(character.getMind().getValues().getEgo()).isZero();
    }

    @Test
    void getKnowledgePointsModifier_sumsSelectedTraits() {
        Values values = Values.defaults();
        values.setKnowledge(1);
        values.setTradition(1);
        PlayableCharacter character = characterWithValues(values);
        Personality personality = character.getMind().getPersonality();

        personality.select(Trait.ILLITERATE, character);
        personality.select(Trait.ORPHAN_MIND, character);

        assertThat(personality.getKnowledgePointsModifier()).isZero(); // -1 + 1
    }

    @Test
    void getLabourPointsModifier_sumsSelectedTraits() {
        Values values = Values.defaults();
        values.setProgress(1);
        PlayableCharacter character = characterWithValues(values);
        Personality personality = character.getMind().getPersonality();

        personality.select(Trait.CONSERVATIVE, character);

        assertThat(personality.getLabourPointsModifier()).isEqualTo(1);
    }
}
