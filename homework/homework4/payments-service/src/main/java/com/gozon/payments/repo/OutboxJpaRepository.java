package com.gozon.payments.repo;

import com.gozon.payments.outbox.OutboxMessageEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxJpaRepository extends JpaRepository<OutboxMessageEntity, UUID> {

    @Query(
            value = """
                    SELECT * FROM outbox_message
                    WHERE status = 'PENDING'
                       OR (status = 'IN_PROGRESS' AND locked_at < :staleBefore)
                    ORDER BY created_at
                    LIMIT :limit
                    FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<OutboxMessageEntity> lockNextBatchForPublishing(
            @Param("limit") int limit,
            @Param("staleBefore") Instant staleBefore
    );
}
