package com.gozon.orders.realtime;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * In-memory registry of WebSocket sessions for this Orders Service instance.
 *
 * For multi-instance backend delivery, status updates are broadcast through Redis Pub/Sub,
 * and every instance delivers to its own connected sessions.
 */
@Component
public class OrderWsSessionRegistry {

    private static final Logger log = LoggerFactory.getLogger(OrderWsSessionRegistry.class);

    private final ConcurrentHashMap<UUID, CopyOnWriteArraySet<WebSocketSession>> sessionsByOrder = new ConcurrentHashMap<>();

    public void register(UUID orderId, WebSocketSession session) {
        sessionsByOrder.computeIfAbsent(orderId, __ -> new CopyOnWriteArraySet<>()).add(session);
    }

    public void unregister(UUID orderId, WebSocketSession session) {
        Set<WebSocketSession> set = sessionsByOrder.get(orderId);
        if (set == null) {
            return;
        }
        set.remove(session);
        if (set.isEmpty()) {
            sessionsByOrder.remove(orderId);
        }
    }

    public void broadcast(UUID orderId, String jsonMessage) {
        Set<WebSocketSession> set = sessionsByOrder.get(orderId);
        if (set == null || set.isEmpty()) {
            return;
        }
        TextMessage msg = new TextMessage(jsonMessage);
        for (WebSocketSession s : set) {
            if (!s.isOpen()) {
                unregister(orderId, s);
                continue;
            }
            try {
                s.sendMessage(msg);
            } catch (IOException e) {
                log.debug("Failed to send WS message to session {}: {}", s.getId(), e.getMessage());
                unregister(orderId, s);
            }
        }
    }
}
