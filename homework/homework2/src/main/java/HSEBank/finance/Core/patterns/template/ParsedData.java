package HSEBank.finance.Core.patterns.template;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ParsedData {
    private final List<BankAccount> accounts;
    private final List<Category> categories;
    private final List<Operation> operations;

    public ParsedData() {
        this.accounts = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.operations = new ArrayList<>();
    }

    public void addAccount(BankAccount account) {
        this.accounts.add(account);
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
    }
}