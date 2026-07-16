package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyTrainingAndConditioningEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyTrainingAndConditioningJpaRepository
        extends JpaRepository<BodyTrainingAndConditioningEntity, Long> {
}
