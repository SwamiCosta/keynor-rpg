package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyDigestiveSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyDigestiveSystemJpaRepository extends JpaRepository<BodyDigestiveSystemEntity, Long> {
}
