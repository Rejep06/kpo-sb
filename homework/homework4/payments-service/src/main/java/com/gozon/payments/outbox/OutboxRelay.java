package com.gozon.payments.outbox;

import com.gozon.payments.repo.OutboxJpaRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    private final OutboxJpaRepository outboxRepository;
    private final OutboxMessagePublisher publisher;
    private final TransactionTemplate tx;

    private final int batchSize;
    private final Duration lockTimeout;

    public OutboxRelay(
            OutboxJpaRepository outboxRepository,
            OutboxMessagePublisher publisher,
            TransactionTemplate tx,
            @Value("${outbox.publish.batch-size:50}") int batchSize,
            @Value("${outbox.publish.lock-timeout-seconds:30}") long lockTimeoutSeconds
    ) {
        this.outboxRepository = outboxRepository;
        this.publisher = publisher;
        this.tx = tx;
        this.batchSize = batchSize;
        this.lockTimeout = Duration.ofSeconds(lockTimeoutSeconds);
    }

    @Scheduled(fixedDelayString = "${outbox.publish.fixed-delay:1000}")
    public void publishBatch() {
        List<OutboxMessageEntity> batch = tx.execute(status -> {
            Instant staleBefore = Instant.now().minus(lockTimeout);
            List<OutboxMessageEntity> locked = outboxRepository.lockNextBatchForPublishing(batchSize, staleBefore);
            locked.forEach(OutboxMessageEntity::markInProgress);
            outboxRepository.saveAll(locked);
            return locked;
        });

        if (batch == null || batch.isEmpty()) {
            return;
        }

        for (OutboxMessageEntity msg : batch) {
            UUID id = msg.getId();
            try {
                publisher.publish(msg);
                tx.executeWithoutResult(status -> {
                    OutboxMessageEntity reloaded = outboxRepository.findById(id).orElseThrow();
                    reloaded.markSent();
                    outboxRepository.save(reloaded);
                });
            } catch (Exception e) {
                log.warn("Failed to publish outbox message {}: {}", id, e.getMessage());
                tx.executeWithoutResult(status -> {
                    OutboxMessageEntity reloaded = outboxRepository.findById(id).orElseThrow();
                    reloaded.markPendingWithError(e.getMessage());
                    outboxRepository.save(reloaded);
                });
            }
        }
    }
}
