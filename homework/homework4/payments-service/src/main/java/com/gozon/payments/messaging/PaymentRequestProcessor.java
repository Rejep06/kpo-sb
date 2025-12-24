package com.gozon.payments.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozon.payments.application.AccountService;
import com.gozon.payments.application.PaymentDecision;
import com.gozon.payments.application.PaymentProcessingService;
import com.gozon.payments.application.PaymentsOutboxFactory;
import com.gozon.payments.repo.InboxJpaRepository;
import com.gozon.payments.repo.OutboxJpaRepository;
import com.gozon.shared.messaging.MessageTypes;
import com.gozon.shared.messaging.PaymentRequest;
import java.time.Instant;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Transactional Inbox + Outbox processing of PaymentRequest commands.
 */
@Service
public class PaymentRequestProcessor {

    private static final int MAX_RETRIES = 5;

    private final InboxJpaRepository inboxRepository;
    private final OutboxJpaRepository outboxRepository;
    private final PaymentProcessingService paymentProcessingService;
    private final PaymentsOutboxFactory outboxFactory;
    private final TransactionTemplate tx;
    private final ObjectMapper objectMapper;
    private final AccountService accountService;

    public PaymentRequestProcessor(
            InboxJpaRepository inboxRepository,
            OutboxJpaRepository outboxRepository,
            PaymentProcessingService paymentProcessingService,
            PaymentsOutboxFactory outboxFactory,
            TransactionTemplate tx,
            ObjectMapper objectMapper,
            AccountService accountService
    ) {
        this.inboxRepository = inboxRepository;
        this.outboxRepository = outboxRepository;
        this.paymentProcessingService = paymentProcessingService;
        this.outboxFactory = outboxFactory;
        this.tx = tx;
        this.objectMapper = objectMapper;
        this.accountService = accountService;
    }

    public void process(PaymentRequest request) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                boolean[] balanceChanged = new boolean[]{false};

                tx.executeWithoutResult(status -> {
                    String payloadJson = toJson(request);

                    int inserted = inboxRepository.insertIfAbsent(
                            request.messageId(),
                            MessageTypes.PAYMENT_REQUESTED,
                            payloadJson,
                            Instant.now()
                    );
                    if (inserted == 0) {
                        // duplicate delivery; already processed in the past
                        return;
                    }

                    PaymentDecision decision = paymentProcessingService.process(request);
                    balanceChanged[0] = decision.balanceChanged();

                    // Transactional outbox with the result event
                    outboxRepository.save(outboxFactory.paymentResult(request.orderId(), decision));

                    inboxRepository.markProcessed(request.messageId(), Instant.now());
                });

                if (balanceChanged[0]) {
                    // best-effort cache eviction (after commit)
                    accountService.evictBalanceCache(request.userId());
                }
                return;

            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES) throw e;
            }
        }
    }

    private String toJson(PaymentRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize PaymentRequest", e);
        }
    }
}
