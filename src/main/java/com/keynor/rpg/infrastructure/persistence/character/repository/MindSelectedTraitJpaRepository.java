package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.MindSelectedTraitEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindSelectedTraitId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MindSelectedTraitJpaRepository
        extends JpaRepository<MindSelectedTraitEntity, MindSelectedTraitId> {

    List<MindSelectedTraitEntity> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
