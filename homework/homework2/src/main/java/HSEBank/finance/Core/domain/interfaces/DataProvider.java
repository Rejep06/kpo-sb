package HSEBank.finance.Core.domain.interfaces;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;

import java.util.List;
import java.util.UUID;

public interface DataProvider {
    List<BankAccount> getAccounts(UUID accountId);
    List<Category> getAllCategories();
    List<Operation> getOperations(UUID accountId);
}