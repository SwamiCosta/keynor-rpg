package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BodyTest {

    @Test
    void humanTemplate_hasTenFlatRootComponents() {
        Body body = Body.humanTemplate();

        List<BodyComponent> roots = body.rootComponents();

        assertThat(roots).extracting(BodyComponent::getName)
                .containsExactly("Skull", "Neck", "RightFlank", "LeftFlank", "Torso", "RightHip", "LeftHip",
                        "Genitals", "Buttocks", "LowerBody");
        assertThat(roots).allSatisfy(root -> assertThat(root.getParent()).isNull());
    }

    @Test
    void skull_hasBrainAsProtectedInternalMandibleAsStructuralAndSensesAsAttachedAppendages() {
        BodyComponent skull = Body.humanTemplate().getSkull();

        assertThat(skull.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Brain", "Mandible", "RightEye", "LeftEye", "Nose", "RightEar", "LeftEar");

        BodyComponent brain = skull.getChildren().get(0);
        assertThat(brain.getCascadeRelation()).isEqualTo(CascadeRelation.PROTECTED_INTERNAL);
        assertThat(brain.isVital()).isTrue();

        BodyComponent mandible = skull.getChildren().get(1);
        assertThat(mandible.getCascadeRelation()).isEqualTo(CascadeRelation.NONE);
        assertThat(mandible.isVital()).isFalse();

        assertThat(skull.getChildren().stream().skip(2))
                .allSatisfy(sense -> assertThat(sense.getCascadeRelation()).isEqualTo(CascadeRelation.ATTACHED_APPENDAGE));
    }

    @Test
    void neck_hasCervicalSpineAndEsophagusAsProtectedInternalChildren() {
        BodyComponent neck = Body.humanTemplate().getNeck();

        assertThat(neck.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("CervicalSpine", "Esophagus");
        assertThat(neck.getChildren()).allSatisfy(child -> {
            assertThat(child.getCascadeRelation()).isEqualTo(CascadeRelation.PROTECTED_INTERNAL);
            assertThat(child.isVital()).isTrue();
            assertThat(child.getParent()).isEqualTo(neck);
        });
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
    void torso_nestsChestSolarComplexAndAbdomenAroundSpineSegments() {
        BodyComponent torso = Body.humanTemplate().getTorso();

        assertThat(torso.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Chest", "ThoracicSpine", "SolarComplex", "Abdomen", "LumbarSpine");

        BodyComponent chest = torso.getChildren().get(0);
        assertThat(chest.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Heart", "Lungs");
        assertThat(chest.getChildren()).allSatisfy(organ -> {
            assertThat(organ.getCascadeRelation()).isEqualTo(CascadeRelation.PROTECTED_INTERNAL);
            assertThat(organ.isVital()).isTrue();
        });

        BodyComponent thoracicSpine = torso.getChildren().get(1);
        assertThat(thoracicSpine.getCascadeRelation()).isEqualTo(CascadeRelation.PROTECTED_INTERNAL);
        assertThat(thoracicSpine.isVital()).isTrue();

        BodyComponent solarComplex = torso.getChildren().get(2);
        assertThat(solarComplex.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Liver");
        BodyComponent liver = solarComplex.getChildren().get(0);
        assertThat(liver.getCascadeRelation()).isEqualTo(CascadeRelation.PROTECTED_INTERNAL);
        assertThat(liver.isVital()).isTrue();

        BodyComponent abdomen = torso.getChildren().get(3);
        assertThat(abdomen.getChildren()).extracting(BodyComponent::getName)
                .containsExactly("Stomach", "Kidneys", "Intestine", "Bladder");
        assertThat(abdomen.getChildren()).extracting(BodyComponent::isVital)
                .containsExactly(false, true, false, false);

        BodyComponent lumbarSpine = torso.getChildren().get(4);
        assertThat(lumbarSpine.getCascadeRelation()).isEqualTo(CascadeRelation.PROTECTED_INTERNAL);
        assertThat(lumbarSpine.isVital()).isTrue();
    }

    @Test
    void hips_areStandaloneRootsWithNoChildren() {
        Body body = Body.humanTemplate();

        assertThat(body.getRightHip().getChildren()).isEmpty();
        assertThat(body.getRightHip().getParent()).isNull();
        assertThat(body.getLeftHip().getChildren()).isEmpty();
        assertThat(body.getLeftHip().getParent()).isNull();
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
    void genitalsAndButtocks_areStandaloneRootsWithNoChildren() {
        Body body = Body.humanTemplate();

        assertThat(body.getGenitals().getChildren()).isEmpty();
        assertThat(body.getButtocks().getChildren()).isEmpty();
    }

    @Test
    void humanTemplate_wiresUpAllDataGroupsWithDefaults() {
        Body body = Body.humanTemplate();

        assertThat(body.getBiomechanics()).isNotNull();
        assertThat(body.getBiomechanics().getGenetics()).isNotNull();
        assertThat(body.getBiomechanics().getBodyComposition()).isNotNull();
        assertThat(body.getBodySystems()).isNotNull();
        assertThat(body.getBodySystems().getBloodSystem()).isNotNull();
        assertThat(body.getBodySystems().getNeuralSystem()).isNotNull();
        assertThat(body.getBodySystems().getHormonalSystem()).isNotNull();
        assertThat(body.getBodySystems().getDigestiveSystem()).isNotNull();
        assertThat(body.getCoefficients()).isNotNull();
        assertThat(body.getGeneticPoints().remainingPoints()).isEqualTo(20);
        assertThat(body.getTrainingPoints().remainingPoints()).isEqualTo(20);
    }
}
