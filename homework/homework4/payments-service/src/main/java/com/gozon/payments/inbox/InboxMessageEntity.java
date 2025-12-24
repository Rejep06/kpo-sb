package com.gozon.payments.inbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Transactional Inbox row (idempotency).
 */
@Entity
@Table(name = "inbox_message")
public class InboxMessageEntity {

    @Id
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "type", nullable = false, length = 64)
    private String type;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    protected InboxMessageEntity() {
        // for JPA
    }

    public InboxMessageEntity(UUID messageId, String type, String payload, Instant receivedAt) {
        this.messageId = messageId;
        this.type = type;
        this.payload = payload;
        this.receivedAt = receivedAt;
    }

    public UUID getMessageId() {
        return messageId;
    }
}
