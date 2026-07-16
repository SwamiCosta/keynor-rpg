package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.MindWeaponProficiencyEntity;
import com.keynor.rpg.infrastructure.persistence.character.entity.MindWeaponProficiencyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MindWeaponProficiencyJpaRepository
        extends JpaRepository<MindWeaponProficiencyEntity, MindWeaponProficiencyId> {

    List<MindWeaponProficiencyEntity> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
