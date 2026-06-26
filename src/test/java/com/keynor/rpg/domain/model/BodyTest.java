package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BodyTest {

    @Test
    void humanTemplate_hasEightFlatRootComponents() {
        Body body = Body.humanTemplate();

        List<BodyComponent> roots = body.rootComponents();

        assertThat(roots).extracting(BodyComponent::getName)
                .containsExactly("Skull", "Neck", "RightFlank", "LeftFlank", "Torso", "Genitals", "Buttocks", "LowerBody");
        assertThat(roots).allSatisfy(root -> assertThat(root.getParent()).isNull());
    }

    @Test
    void skull_hasBrainAsProtectedInternalAndSensesAsAttachedAppendages() {
        BodyComponent skull = Body.humanTemplate().getSkull();

        assertThat(skull.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Brain", "RightEye", "LeftEye", "Nose", "RightEar", "LeftEar");

        BodyComponent brain = skull.getChildren().get(0);
        assertThat(brain.getCascadeRelation()).isEqualTo(CascadeRelation.PROTECTED_INTERNAL);
        assertThat(brain.isVital()).isTrue();

        assertThat(skull.getChildren().stream().skip(1))
                .allSatisfy(sense -> assertThat(sense.getCascadeRelation()).isEqualTo(CascadeRelation.ATTACHED_APPENDAGE));
    }

    @Test
    void flank_handIsChildOfFlankDirectlyNotForearm() {
        BodyComponent rightFlank = Body.humanTemplate().getRightFlank();

        assertThat(rightFlank.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("RightShoulder", "RightArm", "RightForearm", "RightHand");

        BodyComponent hand = rightFlank.getChildren().get(3);
        assertThat(hand.getParent()).isEqualTo(rightFlank);
        assertThat(hand.getCascadeRelation()).isEqualTo(CascadeRelation.ATTACHED_APPENDAGE);

        BodyComponent forearm = rightFlank.getChildren().get(2);
        assertThat(forearm.getChildren()).isEmpty();
    }

    @Test
    void torso_nestsChestAndAbdomenWithVitalOrgansBehindProtection() {
        BodyComponent torso = Body.humanTemplate().getTorso();

        assertThat(torso.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Chest", "Abdomen");

        BodyComponent chest = torso.getChildren().get(0);
        assertThat(chest.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Heart", "Lungs", "Liver");
        assertThat(chest.getChildren()).allSatisfy(organ -> {
            assertThat(organ.getCascadeRelation()).isEqualTo(CascadeRelation.PROTECTED_INTERNAL);
            assertThat(organ.isVital()).isTrue();
        });

        BodyComponent abdomen = torso.getChildren().get(1);
        assertThat(abdomen.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Stomach", "Kidneys", "Intestine", "Bladder");
        assertThat(abdomen.getChildren()).extracting(BodyComponent::isVital)
                .containsExactly(false, true, false, false);
    }

    @Test
    void lowerBody_footIsChildOfLowerBodyDirectlyNotShin() {
        BodyComponent lowerBody = Body.humanTemplate().getLowerBody();

        BodyComponent leftFoot = lowerBody.getChildren().stream()
                .filter(child -> child.getName().equals("LeftFoot"))
                .findFirst()
                .orElseThrow();

        assertThat(leftFoot.getParent()).isEqualTo(lowerBody);
        assertThat(leftFoot.getCascadeRelation()).isEqualTo(CascadeRelation.ATTACHED_APPENDAGE);

        BodyComponent leftShin = lowerBody.getChildren().stream()
                .filter(child -> child.getName().equals("LeftShin"))
                .findFirst()
                .orElseThrow();
        assertThat(leftShin.getChildren()).isEmpty();
    }

    @Test
    void genitalsAndButtocksAndNeck_areStandaloneRootsWithNoChildren() {
        Body body = Body.humanTemplate();

        assertThat(body.getNeck().getChildren()).isEmpty();
        assertThat(body.getGenitals().getChildren()).isEmpty();
        assertThat(body.getButtocks().getChildren()).isEmpty();
    }
}
