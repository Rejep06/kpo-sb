package com.gozon.payments.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozon.payments.outbox.OutboxMessageEntity;
import com.gozon.shared.messaging.MessageTypes;
import com.gozon.shared.messaging.PaymentResult;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Factory for payment outbox messages.
 */
@Component
public class PaymentsOutboxFactory {

    private final ObjectMapper objectMapper;

    public PaymentsOutboxFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxMessageEntity paymentResult(UUID orderId, PaymentDecision decision) {
        UUID messageId = UUID.randomUUID();
        PaymentResult event = new PaymentResult(messageId, orderId, decision.status(), decision.failureReason());
        try {
            String json = objectMapper.writeValueAsString(event);
            return OutboxMessageEntity.pending(
                    messageId,
                    "PaymentOperation",
                    orderId,
                    MessageTypes.PAYMENT_RESULT,
                    json
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize PaymentResult", e);
        }
    }
}
