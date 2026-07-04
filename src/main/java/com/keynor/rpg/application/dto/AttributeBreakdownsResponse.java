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
        map.put("maxMovementSpeed", AttributeBreakdownResponse.from(character.getMaxMovementSpeedBreakdown()));
        map.put("staminaPool", AttributeBreakdownResponse.from(character.getStaminaPoolBreakdown()));
        map.put("fatigueResistance", AttributeBreakdownResponse.from(character.getFatigueResistanceBreakdown()));
        map.put("staminaRecovery", AttributeBreakdownResponse.from(character.getStaminaRecoveryBreakdown()));
        map.put("durability", AttributeBreakdownResponse.from(character.getDurabilityBreakdown()));
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
        map.put("sixthSense", AttributeBreakdownResponse.from(character.getSixthSenseBreakdown()));
        return new AttributeBreakdownsResponse(map);
    }
}
