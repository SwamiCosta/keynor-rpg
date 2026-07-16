package com.keynor.rpg.infrastructure.persistence.audit;

import com.keynor.rpg.domain.port.out.AuditLogger;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Normal port/adapter pair — unlike {@code CharacterRepository}, audit logging is a stable,
 * simple cross-cutting concern, not the fast-changing aggregate the persistence-layer hexagonal
 * exception was scoped to (see {@code keynor-rpg/CLAUDE.md}'s Architecture section).
 */
@Component
public class JpaAuditLogger implements AuditLogger {

    private final AuditLogJpaRepository repository;

    public JpaAuditLogger(AuditLogJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void log(String actor, String action, String entityType, String entityId) {
        repository.save(new AuditLogEntity(actor, action, entityType, entityId, OffsetDateTime.now()));
    }
}
