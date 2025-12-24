package com.gozon.orders.realtime;

import com.gozon.orders.domain.OrderStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * Event for real-time order status tracking (sent to Redis Pub/Sub and then to WebSocket clients).
 */
public record OrderStatusChangedEvent(
        String type,
        UUID orderId,
        String userId,
        OrderStatus status,
        Instant updatedAt
) {
    public static OrderStatusChangedEvent of(UUID orderId, String userId, OrderStatus status, Instant updatedAt) {
        return new OrderStatusChangedEvent("ORDER_STATUS", orderId, userId, status, updatedAt);
    }
}
