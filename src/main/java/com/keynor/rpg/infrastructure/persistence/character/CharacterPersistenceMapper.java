package com.keynor.rpg.infrastructure.persistence.character;

import com.keynor.rpg.domain.model.BloodSystem;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.BodyComponentState;
import com.keynor.rpg.domain.model.BodyComposition;
import com.keynor.rpg.domain.model.BodyStructure;
import com.keynor.rpg.domain.model.CardiacSystem;
import com.keynor.rpg.domain.model.DigestiveSystem;
import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.GeneralPersonality;
import com.keynor.rpg.domain.model.Genetics;
import com.keynor.rpg.domain.model.HormonalGlandularSystem;
import com.keynor.rpg.domain.model.Job;
import com.keynor.rpg.domain.model.Knowledge;
import com.keynor.rpg.domain.model.Labours;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.NeuralSystem;
import com.keynor.rpg.domain.model.Personality;
import com.keynor.rpg.domain.model.PulmonarySystem;
import com.keynor.rpg.domain.model.SensorialOrgans;
import com.keynor.rpg.domain.model.Trait;
import com.keynor.rpg.domain.model.Values;
import com.keynor.rpg.domain.model.Weapon;
import com.keynor.rpg.domain.model.WeaponProficiencies;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyBloodSystemEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyCardiacSystemEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyCompositionEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyComponentEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyDigestiveSystemEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyGeneticsEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyHormonalGlandularSystemEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyNeuralSystemEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyPulmonarySystemEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodySensorialOrgansEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyStructureEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.BodyTrainingAndConditioningEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindEruditionLevelEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindGeneralPersonalityEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindLaboursLevelEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindSelectedTraitEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindValuesEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindWeaponProficiencyEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pure conversion between the domain model ({@code PlayableCharacter} and its data groups) and
 * the flat JPA entities in {@code infrastructure.persistence.character.entity}. No framework
 * types (JPA, Spring) ever appear on the domain side of these methods.
 */
@Component
public class CharacterPersistenceMapper {

    // ---------------------------------------------------------------------------------------
    // Domain -> Entity (save path)
    // ---------------------------------------------------------------------------------------

    public BodyGeneticsEntity toEntity(Long characterId, Genetics genetics) {
        return new BodyGeneticsEntity(characterId, genetics.getEndomorphy(), genetics.getMesomorphy(),
                genetics.getEctomorphy(), genetics.getHeight(), genetics.getLimbRatio());
    }

    public BodyCompositionEntity toEntity(Long characterId, BodyComposition composition) {
        return new BodyCompositionEntity(characterId, composition.getBodyFat(), composition.getMuscleMass(),
                composition.getDominantFiberType(), composition.getMuscleDistribution(),
                composition.getFlexibility(), composition.getBoneDensity(),
                composition.getTendonsAndLigaments());
    }

    public BodyBloodSystemEntity toEntity(Long characterId, BloodSystem bloodSystem) {
        return new BodyBloodSystemEntity(characterId, bloodSystem.getOxygenCarryingCapacity(),
                bloodSystem.getBloodThickness());
    }

    public BodyCardiacSystemEntity toEntity(Long characterId, CardiacSystem cardiacSystem) {
        return new BodyCardiacSystemEntity(characterId, cardiacSystem.getCardiacOutput(),
                cardiacSystem.getAstralVentriculum(), cardiacSystem.getAstralAtrium());
    }

    public BodyPulmonarySystemEntity toEntity(Long characterId, PulmonarySystem pulmonarySystem) {
        return new BodyPulmonarySystemEntity(characterId, pulmonarySystem.getPulmonaryCapacity());
    }

    public BodyNeuralSystemEntity toEntity(Long characterId, NeuralSystem neuralSystem) {
        return new BodyNeuralSystemEntity(characterId, neuralSystem.getNeuralDrive(),
                neuralSystem.getNeuromuscularEfficiency(), neuralSystem.getCerebralCapacity(),
                neuralSystem.getSynapsisQuality(), neuralSystem.getHippocampus(), neuralSystem.getThalamus(),
                neuralSystem.getHypothalamus(), neuralSystem.getAmygdalaAndCingulum(),
                neuralSystem.getImmunity(), neuralSystem.getAgility(), neuralSystem.getPrecision(),
                neuralSystem.getNoeticPlexus(), neuralSystem.getPhaxicCerebelum());
    }

    public BodyHormonalGlandularSystemEntity toEntity(Long characterId, HormonalGlandularSystem hormonalSystem) {
        return new BodyHormonalGlandularSystemEntity(characterId, hormonalSystem.getThyroid(),
                hormonalSystem.getAdrenalGlands(), hormonalSystem.getPredominantMorphicHormone(),
                hormonalSystem.getSubtleEpiphysealGland());
    }

    public BodyDigestiveSystemEntity toEntity(Long characterId, DigestiveSystem digestiveSystem) {
        return new BodyDigestiveSystemEntity(characterId, digestiveSystem.getDigestiveAbsorption(),
                digestiveSystem.getImpurityCleaning(), digestiveSystem.getKetosisEfficiency());
    }

    public BodySensorialOrgansEntity toEntity(Long characterId, SensorialOrgans sensorialOrgans) {
        return new BodySensorialOrgansEntity(characterId, sensorialOrgans.getEyesSensitivity(),
                sensorialOrgans.getEarsSensitivity(), sensorialOrgans.getNoseSensitivity());
    }

    public BodyStructureEntity toEntity(Long characterId, BodyStructure bodyStructure) {
        return new BodyStructureEntity(characterId, bodyStructure.getSkinThickness(),
                bodyStructure.getShapeAesthetics(), bodyStructure.getCellularHealth());
    }

    public BodyTrainingAndConditioningEntity toEntity(Long characterId,
                                                        com.keynor.rpg.domain.model.TrainingAndConditioning t) {
        return new BodyTrainingAndConditioningEntity(characterId, t.getVigor(), t.getReflexes(), t.getIntensity(),
                t.getCoordination(), t.getResilience(), t.getFighting(), t.getWeaponPracticing(), t.getShooting());
    }

    public MindValuesEntity toEntity(Long characterId, Values values) {
        return new MindValuesEntity(characterId, values.getEgo(), values.getLoyalty(), values.getOrganization(),
                values.getFreedom(), values.getSociety(), values.getDivinity(), values.getTruth(),
                values.getKnowledge(), values.getNature(), values.getMorality(), values.getTradition(),
                values.getJustice(), values.getProgress(), values.getPeace());
    }

    public MindGeneralPersonalityEntity toEntity(Long characterId, GeneralPersonality generalPersonality) {
        return new MindGeneralPersonalityEntity(characterId, generalPersonality.getVanity(),
                generalPersonality.getFocus());
    }

    /** Flattens the wound tree (via {@link Body#woundState()}) into one row per node. */
    public List<BodyComponentEntity> toWoundStateEntities(Long characterId, Body body) {
        List<BodyComponentEntity> entities = new ArrayList<>();
        for (BodyComponentState state : body.woundState()) {
            entities.add(new BodyComponentEntity(characterId, state.name(), state.currentHitPoints(),
                    state.irreversibleDamage()));
        }
        return entities;
    }

    public List<MindEruditionLevelEntity> toEruditionEntities(Long characterId, Erudition erudition) {
        List<MindEruditionLevelEntity> entities = new ArrayList<>();
        for (Map.Entry<Knowledge, Integer> entry : erudition.getLevels().entrySet()) {
            if (entry.getValue() != 0) {
                entities.add(new MindEruditionLevelEntity(characterId, entry.getKey().name(), entry.getValue()));
            }
        }
        return entities;
    }

    public List<MindLaboursLevelEntity> toLaboursEntities(Long characterId, Labours labours) {
        List<MindLaboursLevelEntity> entities = new ArrayList<>();
        for (Map.Entry<Job, Integer> entry : labours.getLevels().entrySet()) {
            if (entry.getValue() != 0) {
                entities.add(new MindLaboursLevelEntity(characterId, entry.getKey().name(), entry.getValue()));
            }
        }
        return entities;
    }

    public List<MindWeaponProficiencyEntity> toWeaponProficiencyEntities(Long characterId,
                                                                           WeaponProficiencies weaponProficiencies) {
        List<MindWeaponProficiencyEntity> entities = new ArrayList<>();
        for (Map.Entry<Weapon, Integer> entry : weaponProficiencies.getLevels().entrySet()) {
            if (entry.getValue() != 0) {
                entities.add(new MindWeaponProficiencyEntity(characterId, entry.getKey().name(), entry.getValue()));
            }
        }
        return entities;
    }

    public List<MindSelectedTraitEntity> toSelectedTraitEntities(Long characterId, Personality personality) {
        List<MindSelectedTraitEntity> entities = new ArrayList<>();
        for (Trait trait : personality.getSelectedTraits()) {
            entities.add(new MindSelectedTraitEntity(characterId, trait.name()));
        }
        return entities;
    }

    // ---------------------------------------------------------------------------------------
    // Entity -> Domain (load path)
    // ---------------------------------------------------------------------------------------

    public Genetics toDomain(BodyGeneticsEntity entity) {
        return new Genetics(entity.getEndomorphy(), entity.getMesomorphy(), entity.getEctomorphy(),
                entity.getHeight(), entity.getLimbRatio());
    }

    public BodyComposition toDomain(BodyCompositionEntity entity) {
        return new BodyComposition(entity.getBodyFat(), entity.getMuscleMass(), entity.getDominantFiberType(),
                entity.getMuscleDistribution(), entity.getFlexibility(), entity.getBoneDensity(),
                entity.getTendonsAndLigaments());
    }

    public BloodSystem toDomain(BodyBloodSystemEntity entity) {
        return new BloodSystem(entity.getOxygenCarryingCapacity(), entity.getBloodThickness());
    }

    public CardiacSystem toDomain(BodyCardiacSystemEntity entity) {
        return new CardiacSystem(entity.getCardiacOutput(), entity.getAstralVentriculum(),
                entity.getAstralAtrium());
    }

    public PulmonarySystem toDomain(BodyPulmonarySystemEntity entity) {
        return new PulmonarySystem(entity.getPulmonaryCapacity());
    }

    public NeuralSystem toDomain(BodyNeuralSystemEntity entity) {
        return new NeuralSystem(entity.getNeuralDrive(), entity.getNeuromuscularEfficiency(),
                entity.getCerebralCapacity(), entity.getSynapsisQuality(), entity.getHippocampus(),
                entity.getThalamus(), entity.getHypothalamus(), entity.getAmygdalaAndCingulum(),
                entity.getImmunity(), entity.getAgility(), entity.getPrecision(), entity.getNoeticPlexus(),
                entity.getPhaxicCerebelum());
    }

    public HormonalGlandularSystem toDomain(BodyHormonalGlandularSystemEntity entity) {
        return new HormonalGlandularSystem(entity.getThyroid(), entity.getAdrenalGlands(),
                entity.getPredominantMorphicHormone(), entity.getSubtleEpiphysealGland());
    }

    public DigestiveSystem toDomain(BodyDigestiveSystemEntity entity) {
        return new DigestiveSystem(entity.getDigestiveAbsorption(), entity.getImpurityCleaning(),
                entity.getKetosisEfficiency());
    }

    public SensorialOrgans toDomain(BodySensorialOrgansEntity entity) {
        return new SensorialOrgans(entity.getEyesSensitivity(), entity.getEarsSensitivity(),
                entity.getNoseSensitivity());
    }

    public BodyStructure toDomain(BodyStructureEntity entity) {
        return new BodyStructure(entity.getSkinThickness(), entity.getShapeAesthetics(),
                entity.getCellularHealth());
    }

    public com.keynor.rpg.domain.model.TrainingAndConditioning toDomain(BodyTrainingAndConditioningEntity entity) {
        return new com.keynor.rpg.domain.model.TrainingAndConditioning(entity.getVigor(), entity.getReflexes(),
                entity.getIntensity(), entity.getCoordination(), entity.getResilience(), entity.getFighting(),
                entity.getWeaponPracticing(), entity.getShooting());
    }

    public Values toDomain(MindValuesEntity entity) {
        return new Values(entity.getEgo(), entity.getLoyalty(), entity.getOrganization(), entity.getFreedom(),
                entity.getSociety(), entity.getDivinity(), entity.getTruth(), entity.getKnowledge(),
                entity.getNature(), entity.getMorality(), entity.getTradition(), entity.getJustice(),
                entity.getProgress(), entity.getPeace());
    }

    public GeneralPersonality toDomain(MindGeneralPersonalityEntity entity) {
        return new GeneralPersonality(entity.getVanity(), entity.getFocus());
    }

    public List<BodyComponentState> toWoundState(List<BodyComponentEntity> entities) {
        List<BodyComponentState> states = new ArrayList<>();
        for (BodyComponentEntity entity : entities) {
            states.add(new BodyComponentState(entity.getName(), entity.getCurrentHitPoints(),
                    entity.getIrreversibleDamage()));
        }
        return states;
    }

    public Erudition toErudition(List<MindEruditionLevelEntity> entities) {
        Map<Knowledge, Integer> levels = new java.util.HashMap<>();
        for (MindEruditionLevelEntity entity : entities) {
            levels.put(Knowledge.valueOf(entity.getKnowledge()), entity.getLevel());
        }
        return new Erudition(levels);
    }

    public Labours toLabours(List<MindLaboursLevelEntity> entities) {
        Map<Job, Integer> levels = new java.util.HashMap<>();
        for (MindLaboursLevelEntity entity : entities) {
            levels.put(Job.valueOf(entity.getJob()), entity.getLevel());
        }
        return new Labours(levels);
    }

    public WeaponProficiencies toWeaponProficiencies(List<MindWeaponProficiencyEntity> entities) {
        Map<Weapon, Integer> levels = new java.util.HashMap<>();
        for (MindWeaponProficiencyEntity entity : entities) {
            levels.put(Weapon.valueOf(entity.getWeapon()), entity.getLevel());
        }
        return new WeaponProficiencies(levels);
    }

    public Personality toPersonality(List<MindSelectedTraitEntity> entities) {
        java.util.Set<Trait> traits = new java.util.LinkedHashSet<>();
        for (MindSelectedTraitEntity entity : entities) {
            traits.add(Trait.valueOf(entity.getTrait()));
        }
        return new Personality(traits);
    }
}
