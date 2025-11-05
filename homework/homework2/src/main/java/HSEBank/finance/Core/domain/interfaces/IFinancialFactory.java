package HSEBank.finance.Core.domain.interfaces;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import java.time.LocalDateTime;
import java.util.UUID;

public interface IFinancialFactory {
    BankAccount createBankAccount(String name, double initialBalance);
    Category createCategory(OperationType type, String name);
    Operation createOperation(OperationType type, UUID bankAccountId, double amount, LocalDateTime date, UUID categoryId, String description);
}
