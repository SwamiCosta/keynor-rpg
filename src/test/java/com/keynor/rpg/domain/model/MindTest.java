package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MindTest {

    @Test
    void humanTemplate_hasDefaults() {
        Mind mind = Mind.humanTemplate();

        assertThat(mind.getValues().getEgo()).isEqualTo(1);
        assertThat(mind.getErudition().getSpentPoints()).isZero();
        assertThat(mind.getPersonality().getSelectedTraits()).isEmpty();
        assertThat(mind.getLabours().getSpentPoints()).isZero();
        assertThat(mind.getEventPoints().getTotalPoints()).isEqualTo(20);
        assertThat(mind.getEventPoints().getSpentPoints()).isEqualTo(0);
    }

    @Test
    void previewTemplate_wrapsGivenDataGroups() {
        Values values = Values.defaults();
        values.setKnowledge(5);
        Erudition erudition = new Erudition(java.util.Map.of(Knowledge.ECOLOGY, 2));

        Mind mind = Mind.previewTemplate(values, erudition, Personality.defaults(), Labours.defaults());

        assertThat(mind.getValues().getKnowledge()).isEqualTo(5);
        assertThat(mind.getErudition().getLevel(Knowledge.ECOLOGY)).isEqualTo(2);
    }
}
