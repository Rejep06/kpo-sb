package com.gozon.payments.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Payment attempt/operation for an order.
 * Unique per orderId to guarantee "debit at most once per order" (effectively exactly once).
 */
@Entity
@Table(
        name = "payment_operation",
        uniqueConstraints = @UniqueConstraint(name = "uk_payment_operation_order_id", columnNames = "order_id")
)
public class PaymentOperationEntity {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private PaymentOperationStatus status;

    @Column(name = "failure_reason", length = 128)
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PaymentOperationEntity() {
        // for JPA
    }

    private PaymentOperationEntity(UUID id, UUID orderId, String userId, long amount, PaymentOperationStatus status, String failureReason) {
        this.id = Objects.requireNonNull(id);
        this.orderId = Objects.requireNonNull(orderId);
        this.userId = Objects.requireNonNull(userId);
        this.amount = amount;
        this.status = Objects.requireNonNull(status);
        this.failureReason = failureReason;
        this.createdAt = Instant.now();
    }

    public static PaymentOperationEntity success(UUID orderId, String userId, long amount) {
        return new PaymentOperationEntity(UUID.randomUUID(), orderId, userId, amount, PaymentOperationStatus.SUCCESS, null);
    }

    public static PaymentOperationEntity failed(UUID orderId, String userId, long amount, String reason) {
        return new PaymentOperationEntity(UUID.randomUUID(), orderId, userId, amount, PaymentOperationStatus.FAILED, reason);
    }

    public UUID getOrderId() {
        return orderId;
    }

    public PaymentOperationStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public long getAmount() {
        return amount;
    }

    public String getUserId() {
        return userId;
    }
}
