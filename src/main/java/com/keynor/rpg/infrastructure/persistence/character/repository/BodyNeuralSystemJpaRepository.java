package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyNeuralSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyNeuralSystemJpaRepository extends JpaRepository<BodyNeuralSystemEntity, Long> {
}
