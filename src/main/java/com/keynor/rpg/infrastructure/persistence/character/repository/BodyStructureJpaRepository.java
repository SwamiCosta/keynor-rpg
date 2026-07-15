package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyStructureJpaRepository extends JpaRepository<BodyStructureEntity, Long> {
}
