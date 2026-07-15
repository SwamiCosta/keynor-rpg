package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.MindLaboursLevelEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindLaboursLevelId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MindLaboursLevelJpaRepository extends JpaRepository<MindLaboursLevelEntity, MindLaboursLevelId> {

    List<MindLaboursLevelEntity> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
