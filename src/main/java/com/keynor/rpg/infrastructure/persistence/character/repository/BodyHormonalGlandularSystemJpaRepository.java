package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyHormonalGlandularSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyHormonalGlandularSystemJpaRepository
        extends JpaRepository<BodyHormonalGlandularSystemEntity, Long> {
}
