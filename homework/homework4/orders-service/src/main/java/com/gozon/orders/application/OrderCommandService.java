package com.gozon.orders.application;

import com.gozon.orders.domain.OrderEntity;
import com.gozon.orders.domain.OrderStatus;
import java.util.UUID;

public interface OrderCommandService {
    OrderEntity createOrder(String userId, long amount, String description);

    /**
     * Apply payment result for an order (idempotent).
     * Event does not carry userId, so the update is done by orderId.
     */
    void applyPaymentResult(UUID orderId, OrderStatus newStatus);
}
