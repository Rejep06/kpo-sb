package com.gozon.orders.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Receives status updates from Redis Pub/Sub and pushes them to websocket clients
 * connected to the current Orders Service instance.
 */
@Component
public class OrderStatusRedisSubscriber implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusRedisSubscriber.class);

    private final ObjectMapper objectMapper;
    private final OrderWsSessionRegistry registry;
    private final String channel;

    public OrderStatusRedisSubscriber(
            ObjectMapper objectMapper,
            OrderWsSessionRegistry registry,
            @Value("${orders.realtime.redis-channel:orders.status}") String channel
    ) {
        this.objectMapper = objectMapper;
        this.registry = registry;
        this.channel = channel;
    }

    public String channel() {
        return channel;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            OrderStatusChangedEvent event = objectMapper.readValue(json, OrderStatusChangedEvent.class);
            registry.broadcast(event.orderId(), json);
        } catch (Exception e) {
            log.debug("Failed to process Redis message: {}", e.getMessage());
        }
    }
}
