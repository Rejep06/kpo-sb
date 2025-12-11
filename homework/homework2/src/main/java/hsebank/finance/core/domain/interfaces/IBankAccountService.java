package hsebank.finance.core.domain.interfaces;

import hsebank.finance.core.domain.entities.BankAccount;
import java.util.List;
import java.util.UUID;

public interface IBankAccountService {
    BankAccount createAccount(String name, double initialBalance);

    BankAccount getAccount(UUID id);

    BankAccount updateAccount(UUID id, String name, double balance);

    void deleteAccount(UUID id);

    List<BankAccount> getAllAccounts();

    boolean accountExists(UUID id);
}
