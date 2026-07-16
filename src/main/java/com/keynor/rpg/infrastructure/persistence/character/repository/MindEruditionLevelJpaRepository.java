package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.MindEruditionLevelEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindEruditionLevelId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MindEruditionLevelJpaRepository
        extends JpaRepository<MindEruditionLevelEntity, MindEruditionLevelId> {

    List<MindEruditionLevelEntity> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
