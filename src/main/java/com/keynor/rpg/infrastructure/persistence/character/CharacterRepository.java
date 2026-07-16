package com.keynor.rpg.infrastructure.persistence.character;

import com.keynor.rpg.domain.model.AttributePointBudget;
import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.BodyComponentState;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.PhysicalTraits;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.infrastructure.persistence.character.entity.CharacterEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.CharacterPointBudgetEntity;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyBloodSystemJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyCardiacSystemJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyComponentJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyCompositionJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyDigestiveSystemJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyGeneticsJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyHormonalGlandularSystemJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyNeuralSystemJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyPulmonarySystemJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodySensorialOrgansJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyStructureJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.BodyTrainingAndConditioningJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.CharacterJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.CharacterPointBudgetJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.MindEruditionLevelJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.MindGeneralPersonalityJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.MindLaboursLevelJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.MindSelectedTraitJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.MindValuesJpaRepository;
import com.keynor.rpg.infrastructure.persistence.character.repository.MindWeaponProficiencyJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Concrete, direct repository for {@link PlayableCharacter} — deliberately not a port/adapter
 * pair. Called straight from the application layer. Orchestrates the ~20 flat per-group JPA
 * repositories via {@link CharacterPersistenceMapper}; no JPA relationship mapping
 * (@OneToOne/@OneToMany) is used, by design, to keep each table's read/write path simple and
 * independent as new attributes keep being added.
 *
 * <p>Derived/computed attributes are never persisted — see {@code db/schema.sql}'s header
 * comment. {@code save}/{@code findById} round-trip only the raw inputs, wound-tree damage
 * state, and point-budget spend counts.
 */
@Repository
public class CharacterRepository {

    private final CharacterJpaRepository characterJpaRepository;
    private final CharacterPointBudgetJpaRepository pointBudgetJpaRepository;
    private final BodyGeneticsJpaRepository bodyGeneticsJpaRepository;
    private final BodyCompositionJpaRepository bodyCompositionJpaRepository;
    private final BodyBloodSystemJpaRepository bodyBloodSystemJpaRepository;
    private final BodyCardiacSystemJpaRepository bodyCardiacSystemJpaRepository;
    private final BodyPulmonarySystemJpaRepository bodyPulmonarySystemJpaRepository;
    private final BodyNeuralSystemJpaRepository bodyNeuralSystemJpaRepository;
    private final BodyHormonalGlandularSystemJpaRepository bodyHormonalGlandularSystemJpaRepository;
    private final BodyDigestiveSystemJpaRepository bodyDigestiveSystemJpaRepository;
    private final BodySensorialOrgansJpaRepository bodySensorialOrgansJpaRepository;
    private final BodyStructureJpaRepository bodyStructureJpaRepository;
    private final BodyTrainingAndConditioningJpaRepository bodyTrainingAndConditioningJpaRepository;
    private final BodyComponentJpaRepository bodyComponentJpaRepository;
    private final MindValuesJpaRepository mindValuesJpaRepository;
    private final MindGeneralPersonalityJpaRepository mindGeneralPersonalityJpaRepository;
    private final MindEruditionLevelJpaRepository mindEruditionLevelJpaRepository;
    private final MindLaboursLevelJpaRepository mindLaboursLevelJpaRepository;
    private final MindWeaponProficiencyJpaRepository mindWeaponProficiencyJpaRepository;
    private final MindSelectedTraitJpaRepository mindSelectedTraitJpaRepository;
    private final CharacterPersistenceMapper mapper;

    public CharacterRepository(CharacterJpaRepository characterJpaRepository,
                                CharacterPointBudgetJpaRepository pointBudgetJpaRepository,
                                BodyGeneticsJpaRepository bodyGeneticsJpaRepository,
                                BodyCompositionJpaRepository bodyCompositionJpaRepository,
                                BodyBloodSystemJpaRepository bodyBloodSystemJpaRepository,
                                BodyCardiacSystemJpaRepository bodyCardiacSystemJpaRepository,
                                BodyPulmonarySystemJpaRepository bodyPulmonarySystemJpaRepository,
                                BodyNeuralSystemJpaRepository bodyNeuralSystemJpaRepository,
                                BodyHormonalGlandularSystemJpaRepository bodyHormonalGlandularSystemJpaRepository,
                                BodyDigestiveSystemJpaRepository bodyDigestiveSystemJpaRepository,
                                BodySensorialOrgansJpaRepository bodySensorialOrgansJpaRepository,
                                BodyStructureJpaRepository bodyStructureJpaRepository,
                                BodyTrainingAndConditioningJpaRepository bodyTrainingAndConditioningJpaRepository,
                                BodyComponentJpaRepository bodyComponentJpaRepository,
                                MindValuesJpaRepository mindValuesJpaRepository,
                                MindGeneralPersonalityJpaRepository mindGeneralPersonalityJpaRepository,
                                MindEruditionLevelJpaRepository mindEruditionLevelJpaRepository,
                                MindLaboursLevelJpaRepository mindLaboursLevelJpaRepository,
                                MindWeaponProficiencyJpaRepository mindWeaponProficiencyJpaRepository,
                                MindSelectedTraitJpaRepository mindSelectedTraitJpaRepository,
                                CharacterPersistenceMapper mapper) {
        this.characterJpaRepository = characterJpaRepository;
        this.pointBudgetJpaRepository = pointBudgetJpaRepository;
        this.bodyGeneticsJpaRepository = bodyGeneticsJpaRepository;
        this.bodyCompositionJpaRepository = bodyCompositionJpaRepository;
        this.bodyBloodSystemJpaRepository = bodyBloodSystemJpaRepository;
        this.bodyCardiacSystemJpaRepository = bodyCardiacSystemJpaRepository;
        this.bodyPulmonarySystemJpaRepository = bodyPulmonarySystemJpaRepository;
        this.bodyNeuralSystemJpaRepository = bodyNeuralSystemJpaRepository;
        this.bodyHormonalGlandularSystemJpaRepository = bodyHormonalGlandularSystemJpaRepository;
        this.bodyDigestiveSystemJpaRepository = bodyDigestiveSystemJpaRepository;
        this.bodySensorialOrgansJpaRepository = bodySensorialOrgansJpaRepository;
        this.bodyStructureJpaRepository = bodyStructureJpaRepository;
        this.bodyTrainingAndConditioningJpaRepository = bodyTrainingAndConditioningJpaRepository;
        this.bodyComponentJpaRepository = bodyComponentJpaRepository;
        this.mindValuesJpaRepository = mindValuesJpaRepository;
        this.mindGeneralPersonalityJpaRepository = mindGeneralPersonalityJpaRepository;
        this.mindEruditionLevelJpaRepository = mindEruditionLevelJpaRepository;
        this.mindLaboursLevelJpaRepository = mindLaboursLevelJpaRepository;
        this.mindWeaponProficiencyJpaRepository = mindWeaponProficiencyJpaRepository;
        this.mindSelectedTraitJpaRepository = mindSelectedTraitJpaRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Long save(PlayableCharacter character) {
        OffsetDateTime now = OffsetDateTime.now();
        Long id = character.getId();

        if (id == null) {
            CharacterEntity saved = characterJpaRepository.save(
                    new CharacterEntity(null, character.getName(), character.getLoreReference(), now, now));
            id = saved.getId();
            character.assignId(id);
        } else {
            CharacterEntity existing = characterJpaRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("Character " + character.getId() + " not found"));
            existing.setName(character.getName());
            existing.setLoreReference(character.getLoreReference());
            existing.setUpdatedAt(now);
            characterJpaRepository.save(existing);
        }

        Body body = character.getBody();
        Mind mind = character.getMind();

        bodyGeneticsJpaRepository.save(mapper.toEntity(id, body.getBiomechanics().getGenetics()));
        bodyCompositionJpaRepository.save(mapper.toEntity(id, body.getBiomechanics().getBodyComposition()));
        bodyBloodSystemJpaRepository.save(mapper.toEntity(id, body.getBodySystems().getBloodSystem()));
        bodyCardiacSystemJpaRepository.save(mapper.toEntity(id, body.getBodySystems().getCardiacSystem()));
        bodyPulmonarySystemJpaRepository.save(mapper.toEntity(id, body.getBodySystems().getPulmonarySystem()));
        bodyNeuralSystemJpaRepository.save(mapper.toEntity(id, body.getBodySystems().getNeuralSystem()));
        bodyHormonalGlandularSystemJpaRepository.save(
                mapper.toEntity(id, body.getBodySystems().getHormonalGlandularSystem()));
        bodyDigestiveSystemJpaRepository.save(mapper.toEntity(id, body.getBodySystems().getDigestiveSystem()));
        bodySensorialOrgansJpaRepository.save(mapper.toEntity(id, body.getPhysicalTraits().getSensorialOrgans()));
        bodyStructureJpaRepository.save(mapper.toEntity(id, body.getPhysicalTraits().getBodyStructure()));
        bodyTrainingAndConditioningJpaRepository.save(
                mapper.toEntity(id, body.getPhysicalTraits().getTrainingAndConditioning()));

        pointBudgetJpaRepository.deleteByCharacterId(id);
        pointBudgetJpaRepository.save(new CharacterPointBudgetEntity(id, "GENETIC",
                body.getGeneticPoints().getTotalPoints(), body.getGeneticPoints().getSpentPoints()));
        pointBudgetJpaRepository.save(new CharacterPointBudgetEntity(id, "TRAINING",
                body.getTrainingPoints().getTotalPoints(), body.getTrainingPoints().getSpentPoints()));
        pointBudgetJpaRepository.save(new CharacterPointBudgetEntity(id, "EVENT",
                mind.getEventPoints().getTotalPoints(), mind.getEventPoints().getSpentPoints()));

        bodyComponentJpaRepository.deleteByCharacterId(id);
        bodyComponentJpaRepository.saveAll(mapper.toWoundStateEntities(id, body));

        mindValuesJpaRepository.save(mapper.toEntity(id, mind.getValues()));
        mindGeneralPersonalityJpaRepository.save(mapper.toEntity(id, mind.getGeneralPersonality()));

        mindEruditionLevelJpaRepository.deleteByCharacterId(id);
        mindEruditionLevelJpaRepository.saveAll(mapper.toEruditionEntities(id, mind.getErudition()));

        mindLaboursLevelJpaRepository.deleteByCharacterId(id);
        mindLaboursLevelJpaRepository.saveAll(mapper.toLaboursEntities(id, mind.getLabours()));

        mindWeaponProficiencyJpaRepository.deleteByCharacterId(id);
        mindWeaponProficiencyJpaRepository.saveAll(
                mapper.toWeaponProficiencyEntities(id, mind.getWeaponProficiencies()));

        mindSelectedTraitJpaRepository.deleteByCharacterId(id);
        mindSelectedTraitJpaRepository.saveAll(mapper.toSelectedTraitEntities(id, mind.getPersonality()));

        return id;
    }

    @Transactional(readOnly = true)
    public Optional<PlayableCharacter> findById(Long id) {
        Optional<CharacterEntity> entity = characterJpaRepository.findById(id);
        if (entity.isEmpty()) {
            return Optional.empty();
        }

        Biomechanics biomechanics = new Biomechanics(
                mapper.toDomain(bodyGeneticsJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_genetics", id))),
                mapper.toDomain(bodyCompositionJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_composition", id))));

        BodySystems bodySystems = new BodySystems(
                mapper.toDomain(bodyBloodSystemJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_blood_system", id))),
                mapper.toDomain(bodyCardiacSystemJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_cardiac_system", id))),
                mapper.toDomain(bodyPulmonarySystemJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_pulmonary_system", id))),
                mapper.toDomain(bodyNeuralSystemJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_neural_system", id))),
                mapper.toDomain(bodyHormonalGlandularSystemJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_hormonal_glandular_system", id))),
                mapper.toDomain(bodyDigestiveSystemJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_digestive_system", id))));

        PhysicalTraits physicalTraits = new PhysicalTraits(
                mapper.toDomain(bodySensorialOrgansJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_sensorial_organs", id))),
                mapper.toDomain(bodyStructureJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_structure", id))),
                mapper.toDomain(bodyTrainingAndConditioningJpaRepository.findById(id)
                        .orElseThrow(() -> missing("body_training_and_conditioning", id))));

        Map<String, CharacterPointBudgetEntity> budgetsByType = pointBudgetJpaRepository.findByCharacterId(id)
                .stream()
                .collect(Collectors.toMap(CharacterPointBudgetEntity::getBudgetType, b -> b));
        AttributePointBudget geneticPoints = toBudget(budgetsByType.get("GENETIC"));
        AttributePointBudget trainingPoints = toBudget(budgetsByType.get("TRAINING"));
        AttributePointBudget eventPoints = toBudget(budgetsByType.get("EVENT"));

        List<BodyComponentState> woundState = mapper.toWoundState(bodyComponentJpaRepository.findByCharacterId(id));
        Body body = Body.reconstruct(biomechanics, bodySystems, physicalTraits, geneticPoints, trainingPoints,
                woundState);

        Mind mind = Mind.reconstruct(
                mapper.toDomain(mindValuesJpaRepository.findById(id).orElseThrow(() -> missing("mind_values", id))),
                mapper.toErudition(mindEruditionLevelJpaRepository.findByCharacterId(id)),
                mapper.toPersonality(mindSelectedTraitJpaRepository.findByCharacterId(id)),
                mapper.toLabours(mindLaboursLevelJpaRepository.findByCharacterId(id)),
                mapper.toDomain(mindGeneralPersonalityJpaRepository.findById(id)
                        .orElseThrow(() -> missing("mind_general_personality", id))),
                mapper.toWeaponProficiencies(mindWeaponProficiencyJpaRepository.findByCharacterId(id)),
                eventPoints);

        PlayableCharacter character = new PlayableCharacter(entity.get().getName(), body, mind);
        character.assignId(id);
        if (entity.get().getLoreReference() != null) {
            character.linkToLore(entity.get().getLoreReference());
        }
        return Optional.of(character);
    }

    private AttributePointBudget toBudget(CharacterPointBudgetEntity entity) {
        if (entity == null) {
            return new AttributePointBudget(20);
        }
        return new AttributePointBudget(entity.getTotalPoints(), entity.getSpentPoints());
    }

    private static IllegalStateException missing(String table, Long characterId) {
        return new IllegalStateException("Missing " + table + " row for character " + characterId);
    }
}
