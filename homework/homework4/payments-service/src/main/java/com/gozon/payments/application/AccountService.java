package com.gozon.payments.application;

import com.gozon.payments.domain.AccountEntity;
import com.gozon.payments.repo.AccountJpaRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Accounts use-cases. Uses optimistic locking on {@link AccountEntity} for concurrency safety.
 */
@Service
public class AccountService implements AccountCommandService, AccountQueryService {

    private static final int MAX_RETRIES = 5;

    private final AccountJpaRepository accountRepository;
    private final TransactionTemplate tx;
    private final CacheManager cacheManager;

    public AccountService(AccountJpaRepository accountRepository, TransactionTemplate tx, CacheManager cacheManager) {
        this.accountRepository = accountRepository;
        this.tx = tx;
        this.cacheManager = cacheManager;
    }

    @Override
    public void createAccount(String userId) {
        try {
            tx.executeWithoutResult(status -> {
                accountRepository.findByUserId(userId).ifPresent(a -> {
                    throw new AccountAlreadyExistsException(userId);
                });
                accountRepository.save(AccountEntity.newAccount(userId));
            });
        } catch (DataIntegrityViolationException e) {
            // in case of race
            throw new AccountAlreadyExistsException(userId);
        }
        evictBalanceCache(userId);
    }

    @Override
    public void topUp(String userId, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0 (minor units)");
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                tx.executeWithoutResult(status -> {
                    AccountEntity account = accountRepository.findByUserId(userId)
                            .orElseThrow(() -> new AccountNotFoundException(userId));
                    account.topUp(amount);
                    accountRepository.save(account);
                });
                evictBalanceCache(userId);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == MAX_RETRIES) throw e;
            }
        }
    }

    @Override
    @org.springframework.cache.annotation.Cacheable(cacheNames = "balanceByUser", key = "#userId")
    public long getBalance(String userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException(userId))
                .getBalance();
    }

    public void evictBalanceCache(String userId) {
        Cache cache = cacheManager.getCache("balanceByUser");
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
