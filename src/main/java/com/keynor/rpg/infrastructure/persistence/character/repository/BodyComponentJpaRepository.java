package com.keynor.rpg.infrastructure.persistence.character.repository;

import com.keynor.rpg.infrastructure.persistence.character.entity.BodyComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodyComponentJpaRepository extends JpaRepository<BodyComponentEntity, Long> {

    List<BodyComponentEntity> findByCharacterId(Long characterId);

    void deleteByCharacterId(Long characterId);
}
