package com.gozon.payments.repo;

import com.gozon.payments.domain.PaymentOperationEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOperationJpaRepository extends JpaRepository<PaymentOperationEntity, UUID> {
    Optional<PaymentOperationEntity> findByOrderId(UUID orderId);
}
