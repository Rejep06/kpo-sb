package com.gozon.orders.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozon.orders.domain.OrderEntity;
import com.gozon.orders.repo.OrderJpaRepository;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket endpoint for real-time order status.
 *
 * Client connects after order creation using:
 * ws://<gateway>/ws/orders?userId=...&orderId=...
 */
@Component
public class OrderStatusWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusWebSocketHandler.class);

    private final OrderJpaRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final OrderWsSessionRegistry registry;

    public OrderStatusWebSocketHandler(
            OrderJpaRepository orderRepository,
            ObjectMapper objectMapper,
            OrderWsSessionRegistry registry
    ) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
        this.registry = registry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WsParams params = parseParams(session.getUri());
        if (params == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        UUID orderId = params.orderId;
        String userId = params.userId;

        OrderEntity order = orderRepository.findByIdAndUserId(orderId, userId).orElse(null);
        if (order == null) {
            // Policy violation: user is not owner or order doesn't exist
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Order not found for user"));
            return;
        }

        // Save for cleanup
        session.getAttributes().put("orderId", orderId);

        registry.register(orderId, session);

        // Send current state immediately (so client won't miss updates)
        OrderStatusChangedEvent event = OrderStatusChangedEvent.of(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getUpdatedAt()
        );
        session.sendMessage(new TextMessage(toJson(event)));

        log.debug("WS connected: sessionId={}, orderId={}, userId={}", session.getId(), orderId, userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UUID orderId = (UUID) session.getAttributes().get("orderId");
        if (orderId != null) {
            registry.unregister(orderId, session);
        }
        log.debug("WS closed: sessionId={}, status={}", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        UUID orderId = (UUID) session.getAttributes().get("orderId");
        if (orderId != null) {
            registry.unregister(orderId, session);
        }
        log.debug("WS transport error: sessionId={}, err={}", session.getId(), exception.getMessage());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // This WebSocket is server-push only. Ignore client messages.
        // Keeping the handler prevents closing the connection when a browser sends pings.
        String payload = message.getPayload();
        if (payload != null && payload.length() > 0) {
            log.trace("WS incoming ignored: {}", payload);
        }
    }

    private WsParams parseParams(URI uri) {
        if (uri == null) {
            return null;
        }
        var query = UriComponentsBuilder.fromUri(uri).build().getQueryParams();

        List<String> userIds = query.get("userId");
        List<String> orderIds = query.get("orderId");

        if (userIds == null || userIds.isEmpty() || orderIds == null || orderIds.isEmpty()) {
            return null;
        }

        String userId = userIds.get(0);
        String orderIdRaw = orderIds.get(0);

        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            UUID orderId = UUID.fromString(orderIdRaw);
            return new WsParams(userId, orderId);
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // should never happen
            return new String(("{\"type\":\"ERROR\",\"message\":\"serialization_failed\"}").getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }
    }

    private record WsParams(String userId, UUID orderId) {}
}
