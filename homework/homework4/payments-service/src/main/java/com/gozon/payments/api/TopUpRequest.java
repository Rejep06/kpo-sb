package com.gozon.payments.api;

import jakarta.validation.constraints.Min;

public record TopUpRequest(
        @Min(1)
        long amount
) {}
