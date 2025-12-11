package hsebank.finance.core.domain.interfaces;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.enums.OperationType;
import java.time.LocalDateTime;
import java.util.UUID;

public interface IFinancialFactory {
    BankAccount createBankAccount(String name, double initialBalance);

    Category createCategory(OperationType type, String name);

    Operation createOperation(OperationType type, UUID bankAccountId, double amount, LocalDateTime date,
                              UUID categoryId, String description);
}
