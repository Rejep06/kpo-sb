package com.gozon.orders.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Publishes order status changes to Redis Pub/Sub.
 *
 * Using afterCommit ensures the websocket notification matches the committed DB state.
 */
@Component
public class OrderStatusEventPublisher {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final String channel;

    public OrderStatusEventPublisher(
            StringRedisTemplate redis,
            ObjectMapper objectMapper,
            @Value("${orders.realtime.redis-channel:orders.status}") String channel
    ) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.channel = channel;
    }

    public void publishAfterCommit(OrderStatusChangedEvent event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish(event);
                }
            });
            return;
        }
        publish(event);
    }

    private void publish(OrderStatusChangedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redis.convertAndSend(channel, json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize OrderStatusChangedEvent", e);
        }
    }
}
