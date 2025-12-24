package com.gozon.orders.messaging;

import com.gozon.shared.messaging.RabbitMQTopology;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Exchanges
    @Bean
    public DirectExchange paymentsCommandsExchange() {
        return new DirectExchange(RabbitMQTopology.PAYMENTS_COMMANDS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange ordersEventsExchange() {
        return new DirectExchange(RabbitMQTopology.ORDERS_EVENTS_EXCHANGE, true, false);
    }

    // Queue consumed by Orders Service (payment results)
    @Bean
    public Queue paymentResultQueue() {
        return new Queue(RabbitMQTopology.PAYMENT_RESULT_QUEUE, true);
    }

    @Bean
    public Binding paymentResultBinding(Queue paymentResultQueue, DirectExchange ordersEventsExchange) {
        return BindingBuilder
                .bind(paymentResultQueue)
                .to(ordersEventsExchange)
                .with(RabbitMQTopology.PAYMENT_RESULT_ROUTING_KEY);
    }
}
