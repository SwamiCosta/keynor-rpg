package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.CharacterPointBudgetEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.CharacterPointBudgetId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterPointBudgetJpaRepository
        extends JpaRepository<CharacterPointBudgetEntity, CharacterPointBudgetId> {

    List<CharacterPointBudgetEntity> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
