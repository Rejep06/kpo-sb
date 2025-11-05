package HSEBank.finance.Core.domain.interfaces;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;

public interface FinancialVisitor {
    void visit(BankAccount account);
    void visit(Category category);
    void visit(Operation operation);
}