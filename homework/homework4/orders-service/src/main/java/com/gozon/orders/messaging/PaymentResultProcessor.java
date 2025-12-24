package com.gozon.orders.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozon.orders.application.OrderCommandService;
import com.gozon.orders.domain.OrderStatus;
import com.gozon.orders.repo.InboxJpaRepository;
import com.gozon.shared.messaging.MessageTypes;
import com.gozon.shared.messaging.PaymentResult;
import com.gozon.shared.messaging.PaymentResultStatus;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Transactional Inbox processing for payment results (idempotent).
 */
@Service
public class PaymentResultProcessor {

    private final InboxJpaRepository inboxRepository;
    private final OrderCommandService orderCommandService;
    private final TransactionTemplate tx;
    private final ObjectMapper objectMapper;

    public PaymentResultProcessor(
            InboxJpaRepository inboxRepository,
            OrderCommandService orderCommandService,
            TransactionTemplate tx,
            ObjectMapper objectMapper
    ) {
        this.inboxRepository = inboxRepository;
        this.orderCommandService = orderCommandService;
        this.tx = tx;
        this.objectMapper = objectMapper;
    }

    public void process(PaymentResult result) {
        tx.executeWithoutResult(status -> {
            String payloadJson = toJson(result);
            int inserted = inboxRepository.insertIfAbsent(
                    result.messageId(),
                    MessageTypes.PAYMENT_RESULT,
                    payloadJson,
                    Instant.now()
            );

            if (inserted == 0) {
                // duplicate delivery
                return;
            }

            OrderStatus newStatus = (result.status() == PaymentResultStatus.SUCCESS)
                    ? OrderStatus.FINISHED
                    : OrderStatus.CANCELLED;

            orderCommandService.applyPaymentResult(result.orderId(), newStatus);

            inboxRepository.markProcessed(result.messageId(), Instant.now());
        });
    }

    private String toJson(PaymentResult result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize PaymentResult", e);
        }
    }
}
