package com.gozon.orders.repo;

import com.gozon.orders.domain.OrderEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<OrderEntity> findByIdAndUserId(UUID id, String userId);
}
