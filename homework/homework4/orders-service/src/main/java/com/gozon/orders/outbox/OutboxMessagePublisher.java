package com.gozon.orders.outbox;

/**
 * Abstraction for outbox publishing (DIP).
 */
public interface OutboxMessagePublisher {
    void publish(OutboxMessageEntity message) throws Exception;
}
