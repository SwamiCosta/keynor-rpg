package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyPulmonarySystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyPulmonarySystemJpaRepository extends JpaRepository<BodyPulmonarySystemEntity, Long> {
}
