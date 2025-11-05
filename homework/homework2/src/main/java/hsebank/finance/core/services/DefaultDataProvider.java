package hsebank.finance.core.services;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.interfaces.DataProvider;
import hsebank.finance.core.domain.interfaces.IRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;


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