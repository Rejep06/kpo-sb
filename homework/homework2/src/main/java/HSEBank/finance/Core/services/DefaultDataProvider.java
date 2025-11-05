package HSEBank.finance.Core.services;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.interfaces.DataProvider;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DefaultDataProvider implements DataProvider {
    private final IRepository<BankAccount> accountRepository;
    private final IRepository<Category> categoryRepository;
    private final IRepository<Operation> operationRepository;

    public DefaultDataProvider(IRepository<BankAccount> accountRepository,
                               IRepository<Category> categoryRepository,
                               IRepository<Operation> operationRepository) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public List<BankAccount> getAccounts(UUID accountId) {
        if (accountId != null) {
            return accountRepository.findById(accountId)
                    .map(List::of)
                    .orElse(List.of());
        }
        return accountRepository.findAll();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Operation> getOperations(UUID accountId) {
        if (accountId != null) {
            return operationRepository.findAll().stream()
                    .filter(op -> op.getBankAccountId().equals(accountId))
                    .toList();
        }
        return operationRepository.findAll();
    }
}