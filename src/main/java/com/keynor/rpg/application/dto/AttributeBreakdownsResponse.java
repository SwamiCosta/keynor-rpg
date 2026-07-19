package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Language;
import com.keynor.rpg.domain.model.PlayableCharacter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Term-by-term resolved breakdown for every additive-standard attribute in
 * {@link AttributesResponse} (Delta V4) — keyed by the same field name, so the frontend can
 * render a tooltip like "60 + 4 + 0 + 0 = 64" without duplicating any formula logic client-side.
 * {@code swingPower} and {@code grapplingSelfLifting} are averages of two already-resolved
 * attributes (not additive-standard formulas) and are intentionally absent from this map.
 */
public record AttributeBreakdownsResponse(Map<String, AttributeBreakdownResponse> breakdowns) {

    public static AttributeBreakdownsResponse from(PlayableCharacter character, Language language) {
        Map<String, AttributeBreakdownResponse> map = new LinkedHashMap<>();
        map.put("pushStrength", AttributeBreakdownResponse.from(character.getPushStrengthBreakdown(), language));
        map.put("legDrive", AttributeBreakdownResponse.from(character.getLegDriveBreakdown(), language));
        map.put("gripStrength", AttributeBreakdownResponse.from(character.getGripStrengthBreakdown(), language));
        map.put("liftStrength", AttributeBreakdownResponse.from(character.getLiftStrengthBreakdown(), language));
        map.put("speed", AttributeBreakdownResponse.from(character.getSpeedBreakdown(), language));
        map.put("movementSpeed", AttributeBreakdownResponse.from(character.getMovementSpeedBreakdown(), language));
        map.put("staminaPool", AttributeBreakdownResponse.from(character.getStaminaPoolBreakdown(), language));
        map.put("fatigueResistance", AttributeBreakdownResponse.from(character.getFatigueResistanceBreakdown(), language));
        map.put("staminaRecovery", AttributeBreakdownResponse.from(character.getStaminaRecoveryBreakdown(), language));
        map.put("softTissueDurability", AttributeBreakdownResponse.from(character.getSoftTissueDurabilityBreakdown(), language));
        map.put("boneDurability", AttributeBreakdownResponse.from(character.getBoneDurabilityBreakdown(), language));
        map.put("sight", AttributeBreakdownResponse.from(character.getSightBreakdown(), language));
        map.put("hearing", AttributeBreakdownResponse.from(character.getHearingBreakdown(), language));
        map.put("smell", AttributeBreakdownResponse.from(character.getSmellBreakdown(), language));
        map.put("evasion", AttributeBreakdownResponse.from(character.getEvasionBreakdown(), language));
        map.put("acrobatics", AttributeBreakdownResponse.from(character.getAcrobaticsBreakdown(), language));
        map.put("meleeAccuracy", AttributeBreakdownResponse.from(character.getMeleeAccuracyBreakdown(), language));
        map.put("aim", AttributeBreakdownResponse.from(character.getAimBreakdown(), language));
        map.put("memoryPool", AttributeBreakdownResponse.from(character.getMemoryPoolBreakdown(), language));
        map.put("reasoning", AttributeBreakdownResponse.from(character.getReasoningBreakdown(), language));
        map.put("shortMemory", AttributeBreakdownResponse.from(character.getShortMemoryBreakdown(), language));
        map.put("mentalHealthPool", AttributeBreakdownResponse.from(character.getMentalHealthPoolBreakdown(), language));
        map.put("will", AttributeBreakdownResponse.from(character.getWillBreakdown(), language));
        map.put("balance", AttributeBreakdownResponse.from(character.getBalanceBreakdown(), language));
        map.put("stressResistance", AttributeBreakdownResponse.from(character.getStressResistanceBreakdown(), language));
        map.put("angerResistance", AttributeBreakdownResponse.from(character.getAngerResistanceBreakdown(), language));
        map.put("fearResistance", AttributeBreakdownResponse.from(character.getFearResistanceBreakdown(), language));
        map.put("painThreshold", AttributeBreakdownResponse.from(character.getPainThresholdBreakdown(), language));
        map.put("poisonResistance", AttributeBreakdownResponse.from(character.getPoisonResistanceBreakdown(), language));
        map.put("diseaseResistance", AttributeBreakdownResponse.from(character.getDiseaseResistanceBreakdown(), language));
        map.put("bleedingResistance", AttributeBreakdownResponse.from(character.getBleedingResistanceBreakdown(), language));
        map.put("thermalResistance", AttributeBreakdownResponse.from(character.getThermalResistanceBreakdown(), language));
        map.put("breathOutput", AttributeBreakdownResponse.from(character.getBreathOutputBreakdown(), language));
        map.put("dehydrationResistance",
                AttributeBreakdownResponse.from(character.getDehydrationResistanceBreakdown(), language));
        map.put("starvationResistance",
                AttributeBreakdownResponse.from(character.getStarvationResistanceBreakdown(), language));
        map.put("foodPoisoningAlcoholResistance",
                AttributeBreakdownResponse.from(character.getFoodPoisoningAlcoholResistanceBreakdown(), language));
        map.put("fatGainRate", AttributeBreakdownResponse.from(character.getFatGainRateBreakdown(), language));
        map.put("muscleGainRate", AttributeBreakdownResponse.from(character.getMuscleGainRateBreakdown(), language));
        map.put("intimidation", AttributeBreakdownResponse.from(character.getIntimidationBreakdown(), language));
        map.put("diplomacy", AttributeBreakdownResponse.from(character.getDiplomacyBreakdown(), language));
        map.put("enfactuation", AttributeBreakdownResponse.from(character.getEnfactuationBreakdown(), language));
        map.put("command", AttributeBreakdownResponse.from(character.getCommandBreakdown(), language));
        map.put("manaPool", AttributeBreakdownResponse.from(character.getManaPoolBreakdown(), language));
        map.put("arcaneOutput", AttributeBreakdownResponse.from(character.getArcaneOutputBreakdown(), language));
        map.put("mediunity", AttributeBreakdownResponse.from(character.getMediunityBreakdown(), language));
        map.put("selfConcern", AttributeBreakdownResponse.from(character.getSelfConcernBreakdown(), language));
        map.put("friendshipConcern", AttributeBreakdownResponse.from(character.getFriendshipConcernBreakdown(), language));
        map.put("orderConcern", AttributeBreakdownResponse.from(character.getOrderConcernBreakdown(), language));
        map.put("freedomConcern", AttributeBreakdownResponse.from(character.getFreedomConcernBreakdown(), language));
        map.put("patriotismConcern", AttributeBreakdownResponse.from(character.getPatriotismConcernBreakdown(), language));
        map.put("spiritualConcern", AttributeBreakdownResponse.from(character.getSpiritualConcernBreakdown(), language));
        map.put("philosophyConcern", AttributeBreakdownResponse.from(character.getPhilosophyConcernBreakdown(), language));
        map.put("academicConcern", AttributeBreakdownResponse.from(character.getAcademicConcernBreakdown(), language));
        map.put("environmentalismConcern",
                AttributeBreakdownResponse.from(character.getEnvironmentalismConcernBreakdown(), language));
        map.put("moralityConcern", AttributeBreakdownResponse.from(character.getMoralityConcernBreakdown(), language));
        map.put("traditionalismConcern", AttributeBreakdownResponse.from(character.getTraditionalismConcernBreakdown(), language));
        map.put("justiceConcern", AttributeBreakdownResponse.from(character.getJusticeConcernBreakdown(), language));
        map.put("progressConcern", AttributeBreakdownResponse.from(character.getProgressConcernBreakdown(), language));
        map.put("peaceConcern", AttributeBreakdownResponse.from(character.getPeaceConcernBreakdown(), language));
        map.put("survivalSkills", AttributeBreakdownResponse.from(character.getSurvivalSkillsBreakdown(), language));
        map.put("animalCaring", AttributeBreakdownResponse.from(character.getAnimalCaringBreakdown(), language));
        map.put("manipulation", AttributeBreakdownResponse.from(character.getManipulationBreakdown(), language));
        map.put("behaviorReading", AttributeBreakdownResponse.from(character.getBehaviorReadingBreakdown(), language));
        map.put("discretion", AttributeBreakdownResponse.from(character.getDiscretionBreakdown(), language));
        map.put("bluffing", AttributeBreakdownResponse.from(character.getBluffingBreakdown(), language));
        map.put("faith", AttributeBreakdownResponse.from(character.getFaithBreakdown(), language));
        map.put("illusionResistance", AttributeBreakdownResponse.from(character.getIllusionResistanceBreakdown(), language));
        map.put("creativity", AttributeBreakdownResponse.from(character.getCreativityBreakdown(), language));
        map.put("analysis", AttributeBreakdownResponse.from(character.getAnalysisBreakdown(), language));
        map.put("closeCombat", AttributeBreakdownResponse.from(character.getCloseCombatBreakdown(), language));
        map.put("lowRangeCombat", AttributeBreakdownResponse.from(character.getLowRangeCombatBreakdown(), language));
        map.put("valor", AttributeBreakdownResponse.from(character.getValorBreakdown(), language));
        map.put("psyquismOutput", AttributeBreakdownResponse.from(character.getPsyquismOutputBreakdown(), language));
        map.put("psyquismDefense", AttributeBreakdownResponse.from(character.getPsyquismDefenseBreakdown(), language));
        map.put("charmResistance", AttributeBreakdownResponse.from(character.getCharmResistanceBreakdown(), language));
        map.put("concentration", AttributeBreakdownResponse.from(character.getConcentrationBreakdown(), language));
        map.put("purity", AttributeBreakdownResponse.from(character.getPurityBreakdown(), language));
        map.put("chiPool", AttributeBreakdownResponse.from(character.getChiPoolBreakdown(), language));
        map.put("cognitiveSpeed", AttributeBreakdownResponse.from(character.getCognitiveSpeedBreakdown(), language));
        map.put("hiding", AttributeBreakdownResponse.from(character.getHidingBreakdown(), language));
        map.put("sneaking", AttributeBreakdownResponse.from(character.getSneakingBreakdown(), language));
        map.put("alchemy", AttributeBreakdownResponse.from(character.getAlchemyBreakdown(), language));
        map.put("machineHandling", AttributeBreakdownResponse.from(character.getMachineHandlingBreakdown(), language));
        map.put("performance", AttributeBreakdownResponse.from(character.getPerformanceBreakdown(), language));
        map.put("sciencePractice", AttributeBreakdownResponse.from(character.getSciencePracticeBreakdown(), language));
        map.put("healing", AttributeBreakdownResponse.from(character.getHealingBreakdown(), language));
        map.put("hackingAndPrograming", AttributeBreakdownResponse.from(character.getHackingAndProgramingBreakdown(), language));
        return new AttributeBreakdownsResponse(map);
    }
}
