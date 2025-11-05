package HSEBank.finance.Core.patterns.factory;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.IFinancialFactory;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FinancialFactory implements IFinancialFactory {
    @Override
    public BankAccount createBankAccount(String name, double initialBalance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Account name cannot be empty");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        return new BankAccount(UUID.randomUUID(), name, initialBalance);
    }

    @Override
    public Category createCategory(OperationType type, String name) {
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        return new Category(UUID.randomUUID(), type, name);
    }

    @Override
    public Operation createOperation(OperationType type, UUID bankAccountId, double amount, LocalDateTime date,
                                     UUID categoryId, String description) {
        return new Operation(UUID.randomUUID(), type, bankAccountId, amount, date, categoryId, description);
    }
}