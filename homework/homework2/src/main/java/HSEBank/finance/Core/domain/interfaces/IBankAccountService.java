package HSEBank.finance.Core.domain.interfaces;

import HSEBank.finance.Core.domain.entities.BankAccount;

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
