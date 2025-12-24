package com.gozon.orders.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root: Order.
 *
 * GRASP Creator: factory method newOrder(...) creates new Order instance.
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OrderStatus status;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrderEntity() {
        // for JPA
    }

    private OrderEntity(UUID id, String userId, long amount, String description, OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.amount = amount;
        this.description = Objects.requireNonNull(description);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static OrderEntity newOrder(String userId, long amount, String description) {
        Instant now = Instant.now();
        return new OrderEntity(UUID.randomUUID(), userId, amount, description, OrderStatus.NEW, now, now);
    }

    public void markPaid() {
        if (this.status != OrderStatus.FINISHED) {
            this.status = OrderStatus.FINISHED;
            this.updatedAt = Instant.now();
        }
    }

    public void markCancelled() {
        if (this.status != OrderStatus.CANCELLED) {
            this.status = OrderStatus.CANCELLED;
            this.updatedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public long getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
