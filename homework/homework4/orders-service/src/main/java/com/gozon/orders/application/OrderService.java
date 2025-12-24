package com.gozon.orders.application;

import com.gozon.orders.domain.OrderEntity;
import com.gozon.orders.domain.OrderStatus;
import com.gozon.orders.realtime.OrderStatusChangedEvent;
import com.gozon.orders.realtime.OrderStatusEventPublisher;
import com.gozon.orders.outbox.OutboxMessageEntity;
import com.gozon.orders.repo.OrderJpaRepository;
import com.gozon.orders.repo.OutboxJpaRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service / use-case orchestrator.
 *
 * SOLID:
 * - SRP: orchestration only, persistence in repositories, message creation in factory.
 * - DIP: depends on abstractions (repositories are interfaces).
 */
@Service
public class OrderService implements OrderCommandService, OrderQueryService {

    private final OrderJpaRepository orderRepository;
    private final OutboxJpaRepository outboxRepository;
    private final OrderOutboxFactory outboxFactory;
    private final CacheManager cacheManager;
    private final OrderStatusEventPublisher statusEventPublisher;

    public OrderService(
            OrderJpaRepository orderRepository,
            OutboxJpaRepository outboxRepository,
            OrderOutboxFactory outboxFactory,
            CacheManager cacheManager,
            OrderStatusEventPublisher statusEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.outboxFactory = outboxFactory;
        this.cacheManager = cacheManager;
        this.statusEventPublisher = statusEventPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "ordersByUser", key = "#userId")
    public OrderEntity createOrder(String userId, long amount, String description) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidOrderRequestException("userId must be provided");
        }
        if (amount <= 0) {
            throw new InvalidOrderRequestException("amount must be > 0 (minor units)");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidOrderRequestException("description must be provided");
        }

        OrderEntity order = OrderEntity.newOrder(userId, amount, description);
        orderRepository.save(order);

        // Transactional Outbox (same DB tx)
        OutboxMessageEntity outbox = outboxFactory.paymentRequested(order);
        outboxRepository.save(outbox);

        // Real-time: publish initial state after commit (client can connect right after creation).
        statusEventPublisher.publishAfterCommit(
                OrderStatusChangedEvent.of(order.getId(), order.getUserId(), order.getStatus(), order.getUpdatedAt())
        );

        // Best-effort eviction for single-order cache (new key)
        evictOrderCache(userId, order.getId());

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "ordersByUser", key = "#userId")
    public List<OrderEntity> listOrders(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orderById", key = "#userId + ':' + #orderId")
    public OrderEntity getOrder(String userId, UUID orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    @Transactional
    public void applyPaymentResult(UUID orderId, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        OrderStatus oldStatus = order.getStatus();

        if (newStatus == OrderStatus.FINISHED) {
            order.markPaid();
        } else if (newStatus == OrderStatus.CANCELLED) {
            order.markCancelled();
        }
        orderRepository.save(order);

        // Real-time: notify only if state actually changed (idempotent).
        if (oldStatus != order.getStatus()) {
            statusEventPublisher.publishAfterCommit(
                    OrderStatusChangedEvent.of(order.getId(), order.getUserId(), order.getStatus(), order.getUpdatedAt())
            );
        }

        // Programmatic cache eviction (because we need userId from the loaded order)
        String userId = order.getUserId();
        evictOrdersListCache(userId);
        evictOrderCache(userId, orderId);
    }

    private void evictOrdersListCache(String userId) {
        Cache cache = cacheManager.getCache("ordersByUser");
        if (cache != null) {
            cache.evict(userId);
        }
    }

    private void evictOrderCache(String userId, UUID orderId) {
        Cache cache = cacheManager.getCache("orderById");
        if (cache != null) {
            cache.evict(userId + ":" + orderId);
        }
    }
}
