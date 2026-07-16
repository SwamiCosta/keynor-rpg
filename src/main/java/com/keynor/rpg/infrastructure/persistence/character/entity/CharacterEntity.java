package com.keynor.rpg.infrastructure.persistence.character.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * Flat JPA row for {@code characters} — deliberately not annotated onto the domain
 * {@code PlayableCharacter} class itself (domain layer stays framework-free). Mapping between
 * this entity and the domain model happens entirely in {@code CharacterPersistenceMapper}.
 */
@Entity
@Table(name = "characters")
public class CharacterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lore_reference")
    private String loreReference;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected CharacterEntity() {
    }

    public CharacterEntity(Long id, String name, String loreReference, OffsetDateTime createdAt,
                            OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.loreReference = loreReference;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoreReference() {
        return loreReference;
    }

    public void setLoreReference(String loreReference) {
        this.loreReference = loreReference;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
