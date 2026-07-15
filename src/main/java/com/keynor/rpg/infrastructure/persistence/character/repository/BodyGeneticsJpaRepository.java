package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyGeneticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyGeneticsJpaRepository extends JpaRepository<BodyGeneticsEntity, Long> {
}
