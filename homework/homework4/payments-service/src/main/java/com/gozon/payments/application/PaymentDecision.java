package com.gozon.payments.application;

import com.gozon.shared.messaging.PaymentResultStatus;

public record PaymentDecision(
        PaymentResultStatus status,
        String failureReason,
        boolean balanceChanged
) {
    public static PaymentDecision success(boolean balanceChanged) {
        return new PaymentDecision(PaymentResultStatus.SUCCESS, null, balanceChanged);
    }

    public static PaymentDecision failed(String reason) {
        return new PaymentDecision(PaymentResultStatus.FAILED, reason, false);
    }
}
