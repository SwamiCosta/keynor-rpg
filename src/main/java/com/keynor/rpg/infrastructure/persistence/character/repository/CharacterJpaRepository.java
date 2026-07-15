package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterJpaRepository extends JpaRepository<CharacterEntity, Long> {
}
