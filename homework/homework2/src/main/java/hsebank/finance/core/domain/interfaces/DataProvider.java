package hsebank.finance.core.domain.interfaces;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;
import java.util.List;
import java.util.UUID;

public interface DataProvider {
    List<BankAccount> getAccounts(UUID accountId);

    List<Category> getAllCategories();

    List<Operation> getOperations(UUID accountId);
}