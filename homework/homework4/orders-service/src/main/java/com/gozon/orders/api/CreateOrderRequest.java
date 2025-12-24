package com.gozon.orders.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @Min(1)
        long amount,
        @NotBlank
        String description
) {}
