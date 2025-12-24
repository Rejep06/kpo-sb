package com.gozon.orders.api;

import com.gozon.orders.application.OrderCommandService;
import com.gozon.orders.application.OrderQueryService;
import com.gozon.orders.domain.OrderEntity;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderCommandService commandService;
    private final OrderQueryService queryService;

    public OrderController(OrderCommandService commandService, OrderQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderEntity order = commandService.createOrder(userId, request.amount(), request.description());
        return OrderResponse.from(order);
    }

    @GetMapping
    public List<OrderResponse> listOrders(@RequestHeader("X-User-Id") String userId) {
        return queryService.listOrders(userId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID orderId
    ) {
        return OrderResponse.from(queryService.getOrder(userId, orderId));
    }
}
