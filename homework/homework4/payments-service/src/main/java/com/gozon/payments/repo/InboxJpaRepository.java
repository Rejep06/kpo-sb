package com.gozon.payments.repo;

import com.gozon.payments.inbox.InboxMessageEntity;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InboxJpaRepository extends JpaRepository<InboxMessageEntity, UUID> {

    @Modifying
    @Query(
            value = """
                    INSERT INTO inbox_message(message_id, type, payload, received_at, processed_at)
                    VALUES (:messageId, :type, :payload, :receivedAt, NULL)
                    ON CONFLICT (message_id) DO NOTHING
                    """,
            nativeQuery = true
    )
    int insertIfAbsent(
            @Param("messageId") UUID messageId,
            @Param("type") String type,
            @Param("payload") String payload,
            @Param("receivedAt") Instant receivedAt
    );

    @Modifying
    @Query(
            value = """
                    UPDATE inbox_message
                    SET processed_at = :processedAt
                    WHERE message_id = :messageId
                    """,
            nativeQuery = true
    )
    int markProcessed(@Param("messageId") UUID messageId, @Param("processedAt") Instant processedAt);
}
