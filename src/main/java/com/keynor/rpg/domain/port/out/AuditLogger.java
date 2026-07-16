package com.keynor.rpg.domain.port.out;

/**
 * Records who performed a mutating action, for accountability even while this project has no
 * authentication system of its own yet — {@code actor} is whatever identity the caller
 * supplies (see {@code CharacterController}'s {@code X-Actor} header), not a verified identity.
 * Read-only use cases are not expected to log.
 */
public interface AuditLogger {

    void log(String actor, String action, String entityType, String entityId);
}
