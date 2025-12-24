package com.gozon.payments.messaging;

import com.gozon.shared.messaging.PaymentRequest;
import com.gozon.shared.messaging.RabbitMQTopology;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ consumer for PaymentRequest commands (Orders -> Payments).
 */
@Component
public class PaymentRequestListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentRequestListener.class);

    private final PaymentRequestProcessor processor;

    public PaymentRequestListener(PaymentRequestProcessor processor) {
        this.processor = processor;
    }

    @RabbitListener(queues = RabbitMQTopology.PAYMENT_REQUEST_QUEUE)
    public void onMessage(PaymentRequest request, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            processor.process(request);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Failed to process payment request message; will requeue. error={}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }
}
