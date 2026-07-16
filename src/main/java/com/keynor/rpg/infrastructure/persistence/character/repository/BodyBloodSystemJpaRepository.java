package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyBloodSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyBloodSystemJpaRepository extends JpaRepository<BodyBloodSystemEntity, Long> {
}
