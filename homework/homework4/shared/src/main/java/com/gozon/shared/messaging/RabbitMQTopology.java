package com.gozon.shared.messaging;

/**
 * RabbitMQ topology (exchanges, queues, routing keys).
 * Both services declare the same topology (idempotent on RabbitMQ side as long as definitions match).
 */
public final class RabbitMQTopology {
    private RabbitMQTopology() {}

    // Commands (Orders -> Payments)
    public static final String PAYMENTS_COMMANDS_EXCHANGE = "payments.commands";
    public static final String PAYMENT_REQUEST_ROUTING_KEY = "payment.requested";
    public static final String PAYMENT_REQUEST_QUEUE = "payments.payment-requested";

    // Events (Payments -> Orders)
    public static final String ORDERS_EVENTS_EXCHANGE = "orders.events";
    public static final String PAYMENT_RESULT_ROUTING_KEY = "payment.result";
    public static final String PAYMENT_RESULT_QUEUE = "orders.payment-result";
}
