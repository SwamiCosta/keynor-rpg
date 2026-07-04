package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MindTest {

    @Test
    void humanTemplate_hasDefaultValuesEmptyEruditionAndTwentyEventPoints() {
        Mind mind = Mind.humanTemplate();

        assertThat(mind.getValues().getEgo()).isEqualTo(1);
        assertThat(mind.getErudition().getSelectedTraits()).isEmpty();
        assertThat(mind.getEventPoints().getTotalPoints()).isEqualTo(20);
        assertThat(mind.getEventPoints().getSpentPoints()).isEqualTo(0);
    }

    @Test
    void previewTemplate_wrapsGivenDataGroups() {
        Values values = Values.defaults();
        values.setKnowledge(5);
        Erudition erudition = new Erudition(java.util.Set.of(Trait.ECOLOGY));

        Mind mind = Mind.previewTemplate(values, erudition);

        assertThat(mind.getValues().getKnowledge()).isEqualTo(5);
        assertThat(mind.getErudition().hasTrait(Trait.ECOLOGY)).isTrue();
    }
}
