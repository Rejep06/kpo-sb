package com.gozon.payments.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozon.payments.outbox.OutboxMessageEntity;
import com.gozon.payments.outbox.OutboxMessagePublisher;
import com.gozon.shared.messaging.MessageTypes;
import com.gozon.shared.messaging.PaymentResult;
import com.gozon.shared.messaging.RabbitMQTopology;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter: Outbox -> RabbitMQ.
 */
@Component
public class RabbitPaymentsOutboxPublisher implements OutboxMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitPaymentsOutboxPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxMessageEntity message) throws Exception {
        if (!MessageTypes.PAYMENT_RESULT.equals(message.getType())) {
            throw new IllegalArgumentException("Unsupported outbox type: " + message.getType());
        }

        PaymentResult payload = objectMapper.readValue(message.getPayload(), PaymentResult.class);

        rabbitTemplate.convertAndSend(
                RabbitMQTopology.ORDERS_EVENTS_EXCHANGE,
                RabbitMQTopology.PAYMENT_RESULT_ROUTING_KEY,
                payload,
                amqpMessage -> {
                    amqpMessage.getMessageProperties().setMessageId(payload.messageId().toString());
                    amqpMessage.getMessageProperties().setHeader("x-event-type", message.getType());
                    amqpMessage.getMessageProperties().setHeader("x-aggregate-id", message.getAggregateId().toString());
                    return amqpMessage;
                }
        );
    }
}
