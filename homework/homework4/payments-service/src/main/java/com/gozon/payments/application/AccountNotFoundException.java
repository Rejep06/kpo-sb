package com.gozon.payments.application;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String userId) {
        super("Account not found for userId=" + userId);
    }
}
