package com.gozon.shared.messaging;

/**
 * Message type names for Outbox/Inbox. Kept in a shared module to avoid typos between services.
 */
public final class MessageTypes {
    private MessageTypes() {}

    public static final String PAYMENT_REQUESTED = "PaymentRequested";
    public static final String PAYMENT_RESULT = "PaymentResult";
}
