package hsebank.finance.core.domain.interfaces;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Category;
import hsebank.finance.core.domain.entities.Operation;

public interface FinancialVisitor {
    void visit(BankAccount account);

    void visit(Category category);

    void visit(Operation operation);
}