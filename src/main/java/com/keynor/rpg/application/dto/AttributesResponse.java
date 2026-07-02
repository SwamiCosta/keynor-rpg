package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record AttributesResponse(double strength, double speed, double maxMovementSpeed, double staminaPool,
                                  double fatigueResistance, double staminaRecovery, double durability,
                                  double sight, double hearing, double smell, double evasion, double acrobatics,
                                  double meleeAccuracy, double aim, double memoryPool, double reasoning,
                                  double shortMemory, double mentalHealthPool, double will, double balance,
                                  double stressResistance, double poisonResistance, double diseaseResistance,
                                  double bleedingResistance, double thermalResistance, double breathOutput,
                                  double dehydrationResistance, double starvationResistance,
                                  double foodPoisoningAlcoholResistance, double fatGainRate, double muscleGainRate,
                                  double intimidation, double diplomacy, double enfactuation, double command) {

    public static AttributesResponse from(PlayableCharacter character) {
        return new AttributesResponse(
                character.getStrength(),
                character.getSpeed(),
                character.getMaxMovementSpeed(),
                character.getStaminaPool(),
                character.getFatigueResistance(),
                character.getStaminaRecovery(),
                character.getDurability(),
                character.getSight(),
                character.getHearing(),
                character.getSmell(),
                character.getEvasion(),
                character.getAcrobatics(),
                character.getMeleeAccuracy(),
                character.getAim(),
                character.getMemoryPool(),
                character.getReasoning(),
                character.getShortMemory(),
                character.getMentalHealthPool(),
                character.getWill(),
                character.getBalance(),
                character.getStressResistance(),
                character.getPoisonResistance(),
                character.getDiseaseResistance(),
                character.getBleedingResistance(),
                character.getThermalResistance(),
                character.getBreathOutput(),
                character.getDehydrationResistance(),
                character.getStarvationResistance(),
                character.getFoodPoisoningAlcoholResistance(),
                character.getFatGainRate(),
                character.getMuscleGainRate(),
                character.getIntimidation(),
                character.getDiplomacy(),
                character.getEnfactuation(),
                character.getCommand());
    }
}
