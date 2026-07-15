package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * One row per wound-tree node, keyed by ({@code characterId}, {@code name}). Only the two
 * genuinely stateful fields are stored — see {@code db/schema.sql}'s comment on this table for
 * why every structural field (max HP, cascade relation, parent/child shape, etc.) is
 * intentionally absent: {@code Body.reconstruct()} always rebuilds them procedurally from the
 * human template.
 */
@Entity
@Table(name = "body_components")
public class BodyComponentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "character_id", nullable = false)
    private Long characterId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "current_hit_points", nullable = false)
    private int currentHitPoints;

    @Column(name = "irreversible_damage", nullable = false)
    private int irreversibleDamage;

    protected BodyComponentEntity() {
    }

    public BodyComponentEntity(Long characterId, String name, int currentHitPoints, int irreversibleDamage) {
        this.characterId = characterId;
        this.name = name;
        this.currentHitPoints = currentHitPoints;
        this.irreversibleDamage = irreversibleDamage;
    }

    public Long getId() {
        return id;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public String getName() {
        return name;
    }

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public int getIrreversibleDamage() {
        return irreversibleDamage;
    }
}
