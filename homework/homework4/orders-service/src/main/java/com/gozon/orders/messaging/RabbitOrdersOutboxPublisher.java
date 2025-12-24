package com.gozon.orders.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozon.orders.outbox.OutboxMessageEntity;
import com.gozon.orders.outbox.OutboxMessagePublisher;
import com.gozon.shared.messaging.MessageTypes;
import com.gozon.shared.messaging.PaymentRequest;
import com.gozon.shared.messaging.RabbitMQTopology;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter: Outbox -> RabbitMQ.
 */
@Component
public class RabbitOrdersOutboxPublisher implements OutboxMessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitOrdersOutboxPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxMessageEntity message) throws Exception {
        if (!MessageTypes.PAYMENT_REQUESTED.equals(message.getType())) {
            throw new IllegalArgumentException("Unsupported outbox type: " + message.getType());
        }

        PaymentRequest payload = objectMapper.readValue(message.getPayload(), PaymentRequest.class);

        rabbitTemplate.convertAndSend(
                RabbitMQTopology.PAYMENTS_COMMANDS_EXCHANGE,
                RabbitMQTopology.PAYMENT_REQUEST_ROUTING_KEY,
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
