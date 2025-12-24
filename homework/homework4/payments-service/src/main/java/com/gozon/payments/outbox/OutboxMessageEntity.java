package com.gozon.payments.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Transactional Outbox row.
 */
@Entity
@Table(name = "outbox_message")
public class OutboxMessageEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 64)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "type", nullable = false, length = 64)
    private String type;

    @Column(name = "payload", nullable = false, columnDefinition = "text")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private OutboxStatus status;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    protected OutboxMessageEntity() {
        // for JPA
    }

    private OutboxMessageEntity(UUID id, String aggregateType, UUID aggregateId, String type, String payload) {
        this.id = Objects.requireNonNull(id);
        this.aggregateType = Objects.requireNonNull(aggregateType);
        this.aggregateId = Objects.requireNonNull(aggregateId);
        this.type = Objects.requireNonNull(type);
        this.payload = Objects.requireNonNull(payload);
        this.status = OutboxStatus.PENDING;
        this.attempts = 0;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static OutboxMessageEntity pending(UUID id, String aggregateType, UUID aggregateId, String type, String payload) {
        return new OutboxMessageEntity(id, aggregateType, aggregateId, type, payload);
    }

    public void markInProgress() {
        this.status = OutboxStatus.IN_PROGRESS;
        this.lockedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = Instant.now();
        this.lockedAt = null;
        this.updatedAt = Instant.now();
    }

    public void markPendingWithError(String error) {
        this.status = OutboxStatus.PENDING;
        this.attempts = this.attempts + 1;
        this.lastError = error;
        this.lockedAt = null;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }
}
