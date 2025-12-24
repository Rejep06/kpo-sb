package com.gozon.orders.messaging;

import com.gozon.shared.messaging.PaymentResult;
import com.gozon.shared.messaging.RabbitMQTopology;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ consumer for PaymentResult events (Payments -> Orders).
 */
@Component
public class PaymentResultListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultListener.class);

    private final PaymentResultProcessor processor;

    public PaymentResultListener(PaymentResultProcessor processor) {
        this.processor = processor;
    }

    @RabbitListener(queues = RabbitMQTopology.PAYMENT_RESULT_QUEUE)
    public void onMessage(PaymentResult result, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            processor.process(result);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("Failed to process payment result message; will requeue. error={}", e.getMessage(), e);
            channel.basicNack(tag, false, true);
        }
    }
}
