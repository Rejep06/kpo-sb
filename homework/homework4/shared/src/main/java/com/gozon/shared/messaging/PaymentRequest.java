package com.gozon.shared.messaging;

import java.util.UUID;

/**
 * Command message: request to pay for an order (Orders -> Payments).
 * amount is stored in minor units (e.g., cents / kopeks).
 */
public record PaymentRequest(
        UUID messageId,
        UUID orderId,
        String userId,
        long amount
) {}
