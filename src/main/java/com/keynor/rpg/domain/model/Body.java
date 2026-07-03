package com.keynor.rpg.domain.model;

import java.util.List;

public class Body {

    private final BodyComponent skull;
    private final BodyComponent neck;
    private final BodyComponent rightFlank;
    private final BodyComponent leftFlank;
    private final BodyComponent torso;
    private final BodyComponent rightHip;
    private final BodyComponent leftHip;
    private final BodyComponent genitals;
    private final BodyComponent buttocks;
    private final BodyComponent lowerBody;
    private final Biomechanics biomechanics;
    private final BodySystems bodySystems;
    private final BodyCoefficients coefficients;
    private final AttributePointBudget geneticPoints;
    private final AttributePointBudget trainingPoints;

    private Body(BodyComponent skull, BodyComponent neck, BodyComponent rightFlank, BodyComponent leftFlank,
                  BodyComponent torso, BodyComponent rightHip, BodyComponent leftHip, BodyComponent genitals,
                  BodyComponent buttocks, BodyComponent lowerBody, Biomechanics biomechanics,
                  BodySystems bodySystems, BodyCoefficients coefficients, AttributePointBudget geneticPoints,
                  AttributePointBudget trainingPoints) {
        this.skull = skull;
        this.neck = neck;
        this.rightFlank = rightFlank;
        this.leftFlank = leftFlank;
        this.torso = torso;
        this.rightHip = rightHip;
        this.leftHip = leftHip;
        this.genitals = genitals;
        this.buttocks = buttocks;
        this.lowerBody = lowerBody;
        this.biomechanics = biomechanics;
        this.bodySystems = bodySystems;
        this.coefficients = coefficients;
        this.geneticPoints = geneticPoints;
        this.trainingPoints = trainingPoints;
    }

    /**
     * Full human template with all data groups at their defaults and the standard
     * anatomical wound tree.
     */
    public static Body humanTemplate() {
        return fromDataGroups(Biomechanics.defaults(), BodySystems.defaults());
    }

    /**
     * Builds a body with the provided data groups (for stateless previews and tests) using
     * the standard anatomical wound tree and default coefficients and point budgets.
     */
    public static Body previewTemplate(Biomechanics biomechanics, BodySystems bodySystems) {
        return fromDataGroups(biomechanics, bodySystems);
    }

    private static Body fromDataGroups(Biomechanics biomechanics, BodySystems bodySystems) {
        BodyComponent skull = buildSkull();
        BodyComponent neck = buildNeck();
        BodyComponent rightFlank = buildFlank("Right");
        BodyComponent leftFlank = buildFlank("Left");
        BodyComponent torso = buildTorso();
        BodyComponent rightHip = BodyComponent.structural("RightHip", 14, 3, false, 10);
        BodyComponent leftHip = BodyComponent.structural("LeftHip", 14, 3, false, 10);
        BodyComponent genitals = BodyComponent.structural("Genitals", 6, 1, false, 14);
        BodyComponent buttocks = BodyComponent.structural("Buttocks", 14, 3, false, 10);
        BodyComponent lowerBody = buildLowerBody();

        return new Body(skull, neck, rightFlank, leftFlank, torso, rightHip, leftHip, genitals, buttocks,
                lowerBody, biomechanics, bodySystems,
                BodyCoefficients.defaults(), new AttributePointBudget(20), new AttributePointBudget(20));
    }

    private static BodyComponent buildSkull() {
        BodyComponent skull = BodyComponent.structural("Skull", 20, 5, true, 8);
        skull.addChild(BodyComponent.protectedInternal("Brain", 10, 5, true, 18));
        skull.addChild(BodyComponent.structural("Mandible", 7, 3, false, 13));
        skull.addChild(BodyComponent.attachedAppendage("RightEye", 3, 1, false, 16, 0.05, 0.5));
        skull.addChild(BodyComponent.attachedAppendage("LeftEye", 3, 1, false, 16, 0.05, 0.5));
        skull.addChild(BodyComponent.attachedAppendage("Nose", 4, 1, false, 14, 0.08, 0.4));
        skull.addChild(BodyComponent.attachedAppendage("RightEar", 3, 1, false, 16, 0.05, 0.4));
        skull.addChild(BodyComponent.attachedAppendage("LeftEar", 3, 1, false, 16, 0.05, 0.4));
        return skull;
    }

    private static BodyComponent buildNeck() {
        BodyComponent neck = BodyComponent.structural("Neck", 12, 2, true, 12);
        neck.addChild(BodyComponent.protectedInternal("CervicalSpine", 8, 5, true, 18));
        neck.addChild(BodyComponent.protectedInternal("Esophagus", 6, 4, true, 17));
        return neck;
    }

    private static BodyComponent buildFlank(String side) {
        BodyComponent flank = BodyComponent.structural(side + "Flank", 18, 3, false, 8);
        flank.addChild(BodyComponent.structural(side + "Shoulder", 14, 3, false, 10));
        flank.addChild(BodyComponent.structural(side + "Arm", 14, 3, false, 10));
        flank.addChild(BodyComponent.structural(side + "Forearm", 12, 2, false, 11));
        flank.addChild(BodyComponent.attachedAppendage(side + "Hand", 8, 1, false, 13, 0.06, 0.3));
        return flank;
    }

    private static BodyComponent buildTorso() {
        BodyComponent torso = BodyComponent.structural("Torso", 30, 4, false, 6);

        BodyComponent chest = BodyComponent.structural("Chest", 20, 6, false, 8);
        chest.addChild(BodyComponent.protectedInternal("Heart", 12, 6, true, 16));
        chest.addChild(BodyComponent.protectedInternal("Lungs", 14, 6, true, 16));

        BodyComponent solarComplex = BodyComponent.structural("SolarComplex", 14, 5, false, 9);
        solarComplex.addChild(BodyComponent.protectedInternal("Liver", 10, 6, true, 16));

        BodyComponent abdomen = BodyComponent.structural("Abdomen", 18, 4, false, 8);
        abdomen.addChild(BodyComponent.protectedInternal("Stomach", 8, 4, false, 16));
        abdomen.addChild(BodyComponent.protectedInternal("Kidneys", 8, 4, true, 16));
        abdomen.addChild(BodyComponent.protectedInternal("Intestine", 10, 4, false, 16));
        abdomen.addChild(BodyComponent.protectedInternal("Bladder", 6, 4, false, 16));

        torso.addChild(chest);
        torso.addChild(BodyComponent.protectedInternal("ThoracicSpine", 10, 5, true, 18));
        torso.addChild(solarComplex);
        torso.addChild(abdomen);
        torso.addChild(BodyComponent.protectedInternal("LumbarSpine", 10, 5, true, 18));
        return torso;
    }

    private static BodyComponent buildLowerBody() {
        BodyComponent lowerBody = BodyComponent.structural("LowerBody", 16, 3, false, 9);
        lowerBody.addChild(BodyComponent.structural("LeftThigh", 16, 3, false, 9));
        lowerBody.addChild(BodyComponent.structural("RightThigh", 16, 3, false, 9));
        lowerBody.addChild(BodyComponent.structural("LeftShin", 12, 2, false, 10));
        lowerBody.addChild(BodyComponent.structural("RightShin", 12, 2, false, 10));
        lowerBody.addChild(BodyComponent.attachedAppendage("LeftFoot", 8, 1, false, 13, 0.06, 0.3));
        lowerBody.addChild(BodyComponent.attachedAppendage("RightFoot", 8, 1, false, 13, 0.06, 0.3));
        lowerBody.addChild(BodyComponent.structural("LeftHamstring", 10, 2, false, 11));
        lowerBody.addChild(BodyComponent.structural("RightHamstring", 10, 2, false, 11));
        lowerBody.addChild(BodyComponent.structural("LeftCalf", 10, 2, false, 11));
        lowerBody.addChild(BodyComponent.structural("RightCalf", 10, 2, false, 11));
        return lowerBody;
    }

    public List<BodyComponent> rootComponents() {
        return List.of(skull, neck, rightFlank, leftFlank, torso, rightHip, leftHip, genitals, buttocks, lowerBody);
    }

    public BodyComponent getSkull() { return skull; }
    public BodyComponent getNeck() { return neck; }
    public BodyComponent getRightFlank() { return rightFlank; }
    public BodyComponent getLeftFlank() { return leftFlank; }
    public BodyComponent getTorso() { return torso; }
    public BodyComponent getRightHip() { return rightHip; }
    public BodyComponent getLeftHip() { return leftHip; }
    public BodyComponent getGenitals() { return genitals; }
    public BodyComponent getButtocks() { return buttocks; }
    public BodyComponent getLowerBody() { return lowerBody; }
    public Biomechanics getBiomechanics() { return biomechanics; }
    public BodySystems getBodySystems() { return bodySystems; }
    public BodyCoefficients getCoefficients() { return coefficients; }
    public AttributePointBudget getGeneticPoints() { return geneticPoints; }
    public AttributePointBudget getTrainingPoints() { return trainingPoints; }
}
