package com.gozon.shared.messaging;

import java.util.UUID;

/**
 * Event message: payment result (Payments -> Orders).
 */
public record PaymentResult(
        UUID messageId,
        UUID orderId,
        PaymentResultStatus status,
        String failureReason
) {}
