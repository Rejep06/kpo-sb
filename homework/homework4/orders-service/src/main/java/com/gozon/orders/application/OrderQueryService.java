package com.gozon.orders.application;

import com.gozon.orders.domain.OrderEntity;
import java.util.List;
import java.util.UUID;

public interface OrderQueryService {
    List<OrderEntity> listOrders(String userId);

    OrderEntity getOrder(String userId, UUID orderId);
}
