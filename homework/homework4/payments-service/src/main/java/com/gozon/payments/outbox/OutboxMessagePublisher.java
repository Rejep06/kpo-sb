package com.gozon.payments.outbox;

public interface OutboxMessagePublisher {
    void publish(OutboxMessageEntity message) throws Exception;
}
