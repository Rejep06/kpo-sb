package com.gozon.orders.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozon.orders.domain.OrderEntity;
import com.gozon.orders.outbox.OutboxMessageEntity;
import com.gozon.shared.messaging.MessageTypes;
import com.gozon.shared.messaging.PaymentRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Factory for outbox messages (GRASP Creator).
 * Keeps message serialization concerns out of the domain model.
 */
@Component
public class OrderOutboxFactory {

    private final ObjectMapper objectMapper;

    public OrderOutboxFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxMessageEntity paymentRequested(OrderEntity order) {
        UUID messageId = UUID.randomUUID();
        PaymentRequest payload = new PaymentRequest(messageId, order.getId(), order.getUserId(), order.getAmount());

        try {
            String json = objectMapper.writeValueAsString(payload);
            return OutboxMessageEntity.pending(
                    messageId,
                    "Order",
                    order.getId(),
                    MessageTypes.PAYMENT_REQUESTED,
                    json
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize PaymentRequest", e);
        }
    }
}
