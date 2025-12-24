package com.gozon.payments.application;

import com.gozon.payments.domain.AccountEntity;
import com.gozon.payments.domain.InsufficientFundsException;
import com.gozon.payments.domain.PaymentOperationEntity;
import com.gozon.payments.domain.PaymentOperationStatus;
import com.gozon.payments.repo.AccountJpaRepository;
import com.gozon.payments.repo.PaymentOperationJpaRepository;
import com.gozon.shared.messaging.PaymentRequest;
import org.springframework.stereotype.Service;

/**
 * Core business logic for charging an order.
 * Idempotency is achieved by a unique constraint on order_id in payment_operation table.
 */
@Service
public class PaymentProcessingService {

    public static final String REASON_ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
    public static final String REASON_INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";

    private final AccountJpaRepository accountRepository;
    private final PaymentOperationJpaRepository paymentOperationRepository;

    public PaymentProcessingService(AccountJpaRepository accountRepository, PaymentOperationJpaRepository paymentOperationRepository) {
        this.accountRepository = accountRepository;
        this.paymentOperationRepository = paymentOperationRepository;
    }

    public PaymentDecision process(PaymentRequest request) {
        // If already processed for this order -> idempotent answer
        var existing = paymentOperationRepository.findByOrderId(request.orderId());
        if (existing.isPresent()) {
            var op = existing.get();
            if (op.getStatus() == PaymentOperationStatus.SUCCESS) {
                return PaymentDecision.success(false);
            }
            return PaymentDecision.failed(op.getFailureReason() == null ? "FAILED" : op.getFailureReason());
        }

        // Account lookup
        AccountEntity account = accountRepository.findByUserId(request.userId()).orElse(null);
        if (account == null) {
            paymentOperationRepository.save(PaymentOperationEntity.failed(
                    request.orderId(), request.userId(), request.amount(), REASON_ACCOUNT_NOT_FOUND
            ));
            return PaymentDecision.failed(REASON_ACCOUNT_NOT_FOUND);
        }

        // Debit
        try {
            account.debit(request.amount());
            accountRepository.save(account);
            paymentOperationRepository.save(PaymentOperationEntity.success(
                    request.orderId(), request.userId(), request.amount()
            ));
            return PaymentDecision.success(true);
        } catch (InsufficientFundsException e) {
            paymentOperationRepository.save(PaymentOperationEntity.failed(
                    request.orderId(), request.userId(), request.amount(), REASON_INSUFFICIENT_FUNDS
            ));
            return PaymentDecision.failed(REASON_INSUFFICIENT_FUNDS);
        }
    }
}
