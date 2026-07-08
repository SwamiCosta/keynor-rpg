package com.keynor.rpg.application.dto;

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

    public static AttributeBreakdownsResponse from(PlayableCharacter character) {
        Map<String, AttributeBreakdownResponse> map = new LinkedHashMap<>();
        map.put("pushStrength", AttributeBreakdownResponse.from(character.getPushStrengthBreakdown()));
        map.put("legDrive", AttributeBreakdownResponse.from(character.getLegDriveBreakdown()));
        map.put("gripStrength", AttributeBreakdownResponse.from(character.getGripStrengthBreakdown()));
        map.put("liftStrength", AttributeBreakdownResponse.from(character.getLiftStrengthBreakdown()));
        map.put("speed", AttributeBreakdownResponse.from(character.getSpeedBreakdown()));
        map.put("movementSpeed", AttributeBreakdownResponse.from(character.getMovementSpeedBreakdown()));
        map.put("staminaPool", AttributeBreakdownResponse.from(character.getStaminaPoolBreakdown()));
        map.put("fatigueResistance", AttributeBreakdownResponse.from(character.getFatigueResistanceBreakdown()));
        map.put("staminaRecovery", AttributeBreakdownResponse.from(character.getStaminaRecoveryBreakdown()));
        map.put("softTissueDurability", AttributeBreakdownResponse.from(character.getSoftTissueDurabilityBreakdown()));
        map.put("boneDurability", AttributeBreakdownResponse.from(character.getBoneDurabilityBreakdown()));
        map.put("sight", AttributeBreakdownResponse.from(character.getSightBreakdown()));
        map.put("hearing", AttributeBreakdownResponse.from(character.getHearingBreakdown()));
        map.put("smell", AttributeBreakdownResponse.from(character.getSmellBreakdown()));
        map.put("evasion", AttributeBreakdownResponse.from(character.getEvasionBreakdown()));
        map.put("acrobatics", AttributeBreakdownResponse.from(character.getAcrobaticsBreakdown()));
        map.put("meleeAccuracy", AttributeBreakdownResponse.from(character.getMeleeAccuracyBreakdown()));
        map.put("aim", AttributeBreakdownResponse.from(character.getAimBreakdown()));
        map.put("memoryPool", AttributeBreakdownResponse.from(character.getMemoryPoolBreakdown()));
        map.put("reasoning", AttributeBreakdownResponse.from(character.getReasoningBreakdown()));
        map.put("shortMemory", AttributeBreakdownResponse.from(character.getShortMemoryBreakdown()));
        map.put("mentalHealthPool", AttributeBreakdownResponse.from(character.getMentalHealthPoolBreakdown()));
        map.put("will", AttributeBreakdownResponse.from(character.getWillBreakdown()));
        map.put("balance", AttributeBreakdownResponse.from(character.getBalanceBreakdown()));
        map.put("stressResistance", AttributeBreakdownResponse.from(character.getStressResistanceBreakdown()));
        map.put("angerResistance", AttributeBreakdownResponse.from(character.getAngerResistanceBreakdown()));
        map.put("fearResistance", AttributeBreakdownResponse.from(character.getFearResistanceBreakdown()));
        map.put("painThreshold", AttributeBreakdownResponse.from(character.getPainThresholdBreakdown()));
        map.put("poisonResistance", AttributeBreakdownResponse.from(character.getPoisonResistanceBreakdown()));
        map.put("diseaseResistance", AttributeBreakdownResponse.from(character.getDiseaseResistanceBreakdown()));
        map.put("bleedingResistance", AttributeBreakdownResponse.from(character.getBleedingResistanceBreakdown()));
        map.put("thermalResistance", AttributeBreakdownResponse.from(character.getThermalResistanceBreakdown()));
        map.put("breathOutput", AttributeBreakdownResponse.from(character.getBreathOutputBreakdown()));
        map.put("dehydrationResistance",
                AttributeBreakdownResponse.from(character.getDehydrationResistanceBreakdown()));
        map.put("starvationResistance",
                AttributeBreakdownResponse.from(character.getStarvationResistanceBreakdown()));
        map.put("foodPoisoningAlcoholResistance",
                AttributeBreakdownResponse.from(character.getFoodPoisoningAlcoholResistanceBreakdown()));
        map.put("fatGainRate", AttributeBreakdownResponse.from(character.getFatGainRateBreakdown()));
        map.put("muscleGainRate", AttributeBreakdownResponse.from(character.getMuscleGainRateBreakdown()));
        map.put("intimidation", AttributeBreakdownResponse.from(character.getIntimidationBreakdown()));
        map.put("diplomacy", AttributeBreakdownResponse.from(character.getDiplomacyBreakdown()));
        map.put("enfactuation", AttributeBreakdownResponse.from(character.getEnfactuationBreakdown()));
        map.put("command", AttributeBreakdownResponse.from(character.getCommandBreakdown()));
        map.put("manaPool", AttributeBreakdownResponse.from(character.getManaPoolBreakdown()));
        map.put("arcaneOutput", AttributeBreakdownResponse.from(character.getArcaneOutputBreakdown()));
        map.put("mediunity", AttributeBreakdownResponse.from(character.getMediunityBreakdown()));
        map.put("selfConcern", AttributeBreakdownResponse.from(character.getSelfConcernBreakdown()));
        map.put("friendshipConcern", AttributeBreakdownResponse.from(character.getFriendshipConcernBreakdown()));
        map.put("orderConcern", AttributeBreakdownResponse.from(character.getOrderConcernBreakdown()));
        map.put("freedomConcern", AttributeBreakdownResponse.from(character.getFreedomConcernBreakdown()));
        map.put("patriotismConcern", AttributeBreakdownResponse.from(character.getPatriotismConcernBreakdown()));
        map.put("spiritualConcern", AttributeBreakdownResponse.from(character.getSpiritualConcernBreakdown()));
        map.put("philosophyConcern", AttributeBreakdownResponse.from(character.getPhilosophyConcernBreakdown()));
        map.put("academicConcern", AttributeBreakdownResponse.from(character.getAcademicConcernBreakdown()));
        map.put("environmentalismConcern",
                AttributeBreakdownResponse.from(character.getEnvironmentalismConcernBreakdown()));
        map.put("moralityConcern", AttributeBreakdownResponse.from(character.getMoralityConcernBreakdown()));
        map.put("traditionalismConcern", AttributeBreakdownResponse.from(character.getTraditionalismConcernBreakdown()));
        map.put("justiceConcern", AttributeBreakdownResponse.from(character.getJusticeConcernBreakdown()));
        map.put("progressConcern", AttributeBreakdownResponse.from(character.getProgressConcernBreakdown()));
        map.put("peaceConcern", AttributeBreakdownResponse.from(character.getPeaceConcernBreakdown()));
        map.put("survivalSkills", AttributeBreakdownResponse.from(character.getSurvivalSkillsBreakdown()));
        map.put("animalCaring", AttributeBreakdownResponse.from(character.getAnimalCaringBreakdown()));
        map.put("manipulation", AttributeBreakdownResponse.from(character.getManipulationBreakdown()));
        map.put("behaviorReading", AttributeBreakdownResponse.from(character.getBehaviorReadingBreakdown()));
        map.put("discretion", AttributeBreakdownResponse.from(character.getDiscretionBreakdown()));
        map.put("bluffing", AttributeBreakdownResponse.from(character.getBluffingBreakdown()));
        map.put("faith", AttributeBreakdownResponse.from(character.getFaithBreakdown()));
        map.put("illusionResistance", AttributeBreakdownResponse.from(character.getIllusionResistanceBreakdown()));
        map.put("creativity", AttributeBreakdownResponse.from(character.getCreativityBreakdown()));
        map.put("analysis", AttributeBreakdownResponse.from(character.getAnalysisBreakdown()));
        map.put("closeCombat", AttributeBreakdownResponse.from(character.getCloseCombatBreakdown()));
        map.put("lowRangeCombat", AttributeBreakdownResponse.from(character.getLowRangeCombatBreakdown()));
        map.put("longRangeCombat", AttributeBreakdownResponse.from(character.getLongRangeCombatBreakdown()));
        map.put("psyquismOutput", AttributeBreakdownResponse.from(character.getPsyquismOutputBreakdown()));
        map.put("psyquismDefense", AttributeBreakdownResponse.from(character.getPsyquismDefenseBreakdown()));
        map.put("charmResistance", AttributeBreakdownResponse.from(character.getCharmResistanceBreakdown()));
        map.put("concentration", AttributeBreakdownResponse.from(character.getConcentrationBreakdown()));
        map.put("purity", AttributeBreakdownResponse.from(character.getPurityBreakdown()));
        map.put("chiPool", AttributeBreakdownResponse.from(character.getChiPoolBreakdown()));
        map.put("reactionSpeed", AttributeBreakdownResponse.from(character.getReactionSpeedBreakdown()));
        map.put("hiding", AttributeBreakdownResponse.from(character.getHidingBreakdown()));
        map.put("sneaking", AttributeBreakdownResponse.from(character.getSneakingBreakdown()));
        map.put("alchemy", AttributeBreakdownResponse.from(character.getAlchemyBreakdown()));
        map.put("machineHandling", AttributeBreakdownResponse.from(character.getMachineHandlingBreakdown()));
        map.put("performance", AttributeBreakdownResponse.from(character.getPerformanceBreakdown()));
        map.put("sciencePractice", AttributeBreakdownResponse.from(character.getSciencePracticeBreakdown()));
        map.put("healing", AttributeBreakdownResponse.from(character.getHealingBreakdown()));
        map.put("hackingAndPrograming", AttributeBreakdownResponse.from(character.getHackingAndProgramingBreakdown()));
        return new AttributeBreakdownsResponse(map);
    }
}
