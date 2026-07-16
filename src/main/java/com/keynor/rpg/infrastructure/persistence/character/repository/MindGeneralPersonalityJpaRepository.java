package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.MindGeneralPersonalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MindGeneralPersonalityJpaRepository extends JpaRepository<MindGeneralPersonalityEntity, Long> {
}
