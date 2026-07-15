package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyCompositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyCompositionJpaRepository extends JpaRepository<BodyCompositionEntity, Long> {
}
