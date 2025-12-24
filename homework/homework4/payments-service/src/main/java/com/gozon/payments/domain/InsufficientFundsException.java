package com.gozon.payments.domain;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String userId, long balance, long requested) {
        super("Insufficient funds for userId=" + userId + ". balance=" + balance + " requested=" + requested);
    }
}
