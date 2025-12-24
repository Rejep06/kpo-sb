package com.gozon.payments.application;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String userId) {
        super("Account already exists for userId=" + userId);
    }
}
