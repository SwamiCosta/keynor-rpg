package com.keynor.rpg.infrastructure.external.anthropic;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Builds the JSON Schema for the {@code propose_character_inputs} tool Clown calls when it has
 * concrete input values to suggest. Every field is optional — Clown may propose a partial
 * character, and any field it omits leaves the player's own existing value untouched (see
 * {@code clown/system-prompt.md}). Ranges mirror this project's actual domain model
 * ({@code Genetics}, {@code BodyComposition}, {@code Values}, {@code Knowledge}, {@code Job},
 * {@code Weapon}, ...) — keep this in sync with {@code additive-attribute-formulas.md}/
 * {@code mind-pillar-traits-and-values.md} and the system prompt resource whenever an input's
 * range changes, the same mandatory-sync discipline this project already applies to
 * {@code keynor-rpg-client}'s Formulas reference page.
 */
final class ClownToolSchema {

    private static final JsonNodeFactory NODES = JsonNodeFactory.instance;

    private ClownToolSchema() {
    }

    static ObjectNode buildToolDefinition() {
        ObjectNode tool = NODES.objectNode();
        tool.put("name", "propose_character_inputs");
        tool.put("description", "Propose a partial or full set of Keynor RPG character input "
                + "values, matching the ranges and keys defined in the system prompt. Omit any "
                + "field you have no opinion on.");
        tool.set("input_schema", buildInputSchema());
        return tool;
    }

    private static ObjectNode buildInputSchema() {
        ObjectNode schema = objectSchema();
        ObjectNode properties = (ObjectNode) schema.get("properties");
        properties.set("body", buildBodySchema());
        properties.set("mind", buildMindSchema());
        return schema;
    }

    private static ObjectNode buildBodySchema() {
        ObjectNode body = objectSchema();
        ObjectNode properties = (ObjectNode) body.get("properties");

        ObjectNode genetics = objectSchema();
        putRange(genetics, "endomorphy", 1, 9);
        putRange(genetics, "mesomorphy", 1, 9);
        putRange(genetics, "ectomorphy", 1, 9);
        putRange(genetics, "height", 1, 15);
        putRange(genetics, "limbRatio", 1, 5);
        properties.set("genetics", genetics);

        ObjectNode bodyComposition = objectSchema();
        putRange(bodyComposition, "bodyFat", 1, 10);
        putRange(bodyComposition, "muscleMass", 1, 15);
        putRange(bodyComposition, "dominantFiberType", 1, 9);
        putRange(bodyComposition, "muscleDistribution", 1, 9);
        putRange(bodyComposition, "flexibility", 1, 9);
        putRange(bodyComposition, "boneDensity", 1, 9);
        putRange(bodyComposition, "tendonsAndLigaments", 1, 9);
        properties.set("bodyComposition", bodyComposition);

        properties.set("bodySystems", buildBodySystemsSchema());
        properties.set("physicalTraits", buildPhysicalTraitsSchema());

        return body;
    }

    private static ObjectNode buildBodySystemsSchema() {
        ObjectNode bodySystems = objectSchema();
        ObjectNode properties = (ObjectNode) bodySystems.get("properties");

        ObjectNode bloodSystem = objectSchema();
        putRange(bloodSystem, "oxygenCarryingCapacity", 1, 9);
        putRange(bloodSystem, "bloodThickness", 1, 5);
        properties.set("bloodSystem", bloodSystem);

        ObjectNode cardiacSystem = objectSchema();
        putRange(cardiacSystem, "cardiacOutput", 1, 9);
        putArcaneOrganLock(cardiacSystem, "astralVentriculum");
        putArcaneOrganLock(cardiacSystem, "astralAtrium");
        properties.set("cardiacSystem", cardiacSystem);

        ObjectNode pulmonarySystem = objectSchema();
        putRange(pulmonarySystem, "pulmonaryCapacity", 1, 9);
        properties.set("pulmonarySystem", pulmonarySystem);

        ObjectNode neuralSystem = objectSchema();
        putRange(neuralSystem, "neuralDrive", 1, 9);
        putRange(neuralSystem, "neuromuscularEfficiency", 1, 9);
        putRange(neuralSystem, "cerebralCapacity", 1, 9);
        putRange(neuralSystem, "synapsisQuality", 1, 9);
        putRange(neuralSystem, "hippocampus", 1, 9);
        putRange(neuralSystem, "thalamus", 1, 9);
        putRange(neuralSystem, "hypothalamus", 1, 9);
        putRange(neuralSystem, "amygdalaAndCingulum", 1, 9);
        putRange(neuralSystem, "immunity", 1, 9);
        putRange(neuralSystem, "agility", 1, 9);
        putRange(neuralSystem, "precision", 1, 9);
        putArcaneOrganLock(neuralSystem, "noeticPlexus");
        putArcaneOrganLock(neuralSystem, "phaxicCerebelum");
        properties.set("neuralSystem", neuralSystem);

        ObjectNode hormonalGlandularSystem = objectSchema();
        putRange(hormonalGlandularSystem, "thyroid", 1, 9);
        putRange(hormonalGlandularSystem, "adrenalGlands", 1, 9);
        putRange(hormonalGlandularSystem, "predominantMorphicHormone", 1, 9);
        putArcaneOrganLock(hormonalGlandularSystem, "subtleEpiphysealGland");
        properties.set("hormonalGlandularSystem", hormonalGlandularSystem);

        ObjectNode digestiveSystem = objectSchema();
        putRange(digestiveSystem, "digestiveAbsorption", 1, 9);
        putRange(digestiveSystem, "impurityCleaning", 1, 9);
        putRange(digestiveSystem, "ketosisEfficiency", 1, 9);
        properties.set("digestiveSystem", digestiveSystem);

        return bodySystems;
    }

    private static ObjectNode buildPhysicalTraitsSchema() {
        ObjectNode physicalTraits = objectSchema();
        ObjectNode properties = (ObjectNode) physicalTraits.get("properties");

        ObjectNode sensorialOrgans = objectSchema();
        putRange(sensorialOrgans, "eyesSensitivity", 1, 9);
        putRange(sensorialOrgans, "earsSensitivity", 1, 9);
        putRange(sensorialOrgans, "noseSensitivity", 1, 9);
        properties.set("sensorialOrgans", sensorialOrgans);

        ObjectNode bodyStructure = objectSchema();
        putRangeWithDescription(bodyStructure, "skinThickness", 2, 4,
                "Human range is 2-4 — never suggest outside it for a human character.");
        putRange(bodyStructure, "shapeAesthetics", 1, 9);
        putRange(bodyStructure, "cellularHealth", 1, 9);
        properties.set("bodyStructure", bodyStructure);

        ObjectNode trainingAndConditioning = objectSchema();
        putRange(trainingAndConditioning, "vigor", 0, 8);
        putRange(trainingAndConditioning, "reflexes", 0, 8);
        putRange(trainingAndConditioning, "intensity", 0, 8);
        putRange(trainingAndConditioning, "coordination", 0, 8);
        putRange(trainingAndConditioning, "resilience", 0, 8);
        putRange(trainingAndConditioning, "fighting", 0, 8);
        putRange(trainingAndConditioning, "weaponPracticing", 0, 8);
        putRange(trainingAndConditioning, "shooting", 0, 8);
        properties.set("trainingAndConditioning", trainingAndConditioning);

        return physicalTraits;
    }

    private static ObjectNode buildMindSchema() {
        ObjectNode mind = objectSchema();
        ObjectNode properties = (ObjectNode) mind.get("properties");

        ObjectNode values = objectSchema();
        String[] valueFields = {"ego", "loyalty", "organization", "freedom", "society", "divinity", "truth",
                "knowledge", "nature", "morality", "tradition", "justice", "progress", "peace"};
        for (String field : valueFields) {
            putRange(values, field, 0, 5);
        }
        properties.set("values", values);

        ObjectNode erudition = objectSchema();
        ((ObjectNode) erudition.get("properties")).set("levels",
                levelMap(0, 4, "Knowledge", "At most 2 points spent in total across all keys, unless a "
                        + "selected trait changes the budget."));
        properties.set("erudition", erudition);

        ObjectNode labours = objectSchema();
        ((ObjectNode) labours.get("properties")).set("levels",
                levelMap(0, 4, "Job", "At most 2 points spent in total across all keys, unless a selected "
                        + "trait changes the budget."));
        properties.set("labours", labours);

        ObjectNode weaponProficiencies = objectSchema();
        ((ObjectNode) weaponProficiencies.get("properties")).set("levels",
                levelMap(0, 3, "Weapon", "No shared budget — each weapon is independent."));
        properties.set("weaponProficiencies", weaponProficiencies);

        ObjectNode personality = objectSchema();
        ObjectNode selectedTraits = NODES.objectNode();
        selectedTraits.put("type", "array");
        selectedTraits.put("description", "Trait keys to select, matching the catalog and prerequisites "
                + "in the system prompt exactly. Use sparingly per the balance philosophy.");
        ObjectNode traitItem = NODES.objectNode();
        traitItem.put("type", "string");
        selectedTraits.set("items", traitItem);
        ((ObjectNode) personality.get("properties")).set("selectedTraits", selectedTraits);
        properties.set("personality", personality);

        ObjectNode generalPersonality = objectSchema();
        putRange(generalPersonality, "vanity", 1, 9);
        putRange(generalPersonality, "focus", 1, 9);
        properties.set("generalPersonality", generalPersonality);

        return mind;
    }

    private static ObjectNode objectSchema() {
        ObjectNode node = NODES.objectNode();
        node.put("type", "object");
        node.set("properties", NODES.objectNode());
        return node;
    }

    private static void putRange(ObjectNode parent, String field, int min, int max) {
        ObjectNode properties = (ObjectNode) parent.get("properties");
        ObjectNode fieldSchema = NODES.objectNode();
        fieldSchema.put("type", "integer");
        fieldSchema.put("minimum", min);
        fieldSchema.put("maximum", max);
        properties.set(field, fieldSchema);
    }

    private static void putRangeWithDescription(ObjectNode parent, String field, int min, int max,
                                                 String description) {
        ObjectNode properties = (ObjectNode) parent.get("properties");
        ObjectNode fieldSchema = NODES.objectNode();
        fieldSchema.put("type", "integer");
        fieldSchema.put("minimum", min);
        fieldSchema.put("maximum", max);
        fieldSchema.put("description", description);
        properties.set(field, fieldSchema);
    }

    private static void putArcaneOrganLock(ObjectNode parent, String field) {
        ObjectNode properties = (ObjectNode) parent.get("properties");
        ObjectNode fieldSchema = NODES.objectNode();
        fieldSchema.put("type", "integer");
        fieldSchema.put("const", 0);
        fieldSchema.put("description", "Magical-race-only organ, absent on every human character — "
                + "always 0.");
        properties.set(field, fieldSchema);
    }

    private static ObjectNode levelMap(int min, int max, String keyCatalogName, String description) {
        ObjectNode map = NODES.objectNode();
        map.put("type", "object");
        map.put("description", "Keys must be exact " + keyCatalogName + " enum names from the system "
                + "prompt. " + description);
        ObjectNode valueSchema = NODES.objectNode();
        valueSchema.put("type", "integer");
        valueSchema.put("minimum", min);
        valueSchema.put("maximum", max);
        map.set("additionalProperties", valueSchema);
        return map;
    }
}
