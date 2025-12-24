package com.gozon.payments.domain;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root: Account (1 per user).
 *
 * Money stored in minor units (kopeks/cents) as long.
 */
@Entity
@Table(name = "accounts", uniqueConstraints = @UniqueConstraint(name = "uk_accounts_user_id", columnNames = "user_id"))
public class AccountEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "balance", nullable = false)
    private long balance;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected AccountEntity() {
        // for JPA
    }

    private AccountEntity(UUID id, String userId, long balance) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.balance = balance;
    }

    public static AccountEntity newAccount(String userId) {
        return new AccountEntity(UUID.randomUUID(), userId, 0L);
    }

    public void topUp(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Top-up amount must be > 0");
        this.balance = Math.addExact(this.balance, amount);
    }

    public void debit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Debit amount must be > 0");
        if (this.balance < amount) throw new InsufficientFundsException(userId, balance, amount);
        this.balance = Math.subtractExact(this.balance, amount);
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public long getBalance() {
        return balance;
    }
}
