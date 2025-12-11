package hsebank.finance.core.services;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.interfaces.IBankAccountService;
import hsebank.finance.core.domain.interfaces.IFinancialFactory;
import hsebank.finance.core.domain.interfaces.IRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;


@Service
public class BankAccountFacade implements IBankAccountService {
    private final IFinancialFactory factory;
    private final IRepository<BankAccount> accountRepository;

    public BankAccountFacade(IFinancialFactory factory, IRepository<BankAccount> accountRepository) {
        this.factory = factory;
        this.accountRepository = accountRepository;
    }

    @Override
    public BankAccount createAccount(String name, double initialBalance) {
        BankAccount account = factory.createBankAccount(name, initialBalance);
        return accountRepository.save(account);
    }

    @Override
    public BankAccount getAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
    }

    @Override
    public BankAccount updateAccount(UUID id, String name, double balance) {
        BankAccount account = getAccount(id);
        account.update(name, balance);
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(UUID id) {
        if (!accountRepository.exists(id)) {
            throw new IllegalArgumentException("Account not found with ID: " + id);
        }
        accountRepository.delete(id);
    }

    @Override
    public List<BankAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public boolean accountExists(UUID id) {
        return accountRepository.exists(id);
    }
}