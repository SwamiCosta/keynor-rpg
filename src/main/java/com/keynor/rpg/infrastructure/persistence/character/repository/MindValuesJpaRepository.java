package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.MindValuesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MindValuesJpaRepository extends JpaRepository<MindValuesEntity, Long> {
}
