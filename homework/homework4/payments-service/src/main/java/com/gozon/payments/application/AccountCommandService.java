package com.gozon.payments.application;

public interface AccountCommandService {
    void createAccount(String userId);

    void topUp(String userId, long amount);
}
