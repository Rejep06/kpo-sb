package HSEBank.finance.Application;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.IBankAccountService;
import HSEBank.finance.Core.domain.interfaces.ICategoryService;
import HSEBank.finance.Core.domain.interfaces.IOperationService;
import HSEBank.finance.Core.patterns.commands.CreateAccountCommand;
import HSEBank.finance.Core.patterns.commands.UpdateAccountCommand;
import HSEBank.finance.Core.patterns.commands.UpdateCategoryCommand;
import HSEBank.finance.Core.patterns.decorator.TimingDecorator;
import HSEBank.finance.Core.services.AnalyticsFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PatternDemoManager {
    private final CLIMenuService menuService;
    private final IBankAccountService accountService;
    private final ICategoryService categoryService;
    private final IOperationService operationService;
    private final AnalyticsFacade analyticsFacade;

    public void demonstratePatterns() {
        menuService.showMessage("\n🎯 PATTERN DEMONSTRATIONS:");
        menuService.showMessage("1. Command Pattern Demo");
        menuService.showMessage("2. Decorator Pattern Demo");
        menuService.showMessage("3. Full System Demo");

        String choice = menuService.getInput("\nSelect demo: ");

        switch (choice) {
            case "1" -> demonstrateCommands();
            case "2" -> demonstrateDecorator();
            case "3" -> runFullDemo();
            default -> menuService.showError("Invalid choice!");
        }
    }

    private void demonstrateCommands() {
        menuService.showMessage("\n⚡ COMMAND PATTERN DEMO");

        // Create account using command
        var createCmd = new CreateAccountCommand(accountService, "Command Demo Account", 1000.0);
        new TimingDecorator(createCmd).execute();

        BankAccount account = createCmd.getCreatedAccount();
        menuService.showMessage("✅ Created account: " + account.getName());

        // Update account using command
        var updateCmd = new UpdateAccountCommand(accountService, account.getId(), "Updated Demo Account", 1500.0);
        new TimingDecorator(updateCmd).execute();
        menuService.showMessage("✅ Updated account balance: " + updateCmd.getUpdatedAccount().getBalance());
    }

    private void demonstrateDecorator() {
        menuService.showMessage("\n🎭 DECORATOR PATTERN DEMO");

        // Create a category first for the demo
        Category demoCategory = categoryService.createCategory(OperationType.EXPENSE, "Demo Category");

        // Update category with timing
        var updateCatCmd = new UpdateCategoryCommand(categoryService, demoCategory.getId(),
                OperationType.EXPENSE, "Updated Demo Category");
        var timedCommand = new TimingDecorator(updateCatCmd);

        menuService.showMessage("Executing timed command...");
        timedCommand.execute();

        // Clean up
        categoryService.deleteCategory(demoCategory.getId());
    }

    private void runFullDemo() {
        menuService.showMessage("\n🚀 FULL SYSTEM DEMO");

        // Create demo data
        BankAccount demoAccount = accountService.createAccount("Demo Account", 5000.0);
        Category incomeCategory = categoryService.createCategory(OperationType.INCOME, "Demo Income");
        Category expenseCategory = categoryService.createCategory(OperationType.EXPENSE, "Demo Expense");

        // Add operations
        operationService.addOperation(OperationType.INCOME, demoAccount.getId(), 1000.0,
                LocalDateTime.now(), incomeCategory.getId(), "Salary");
        operationService.addOperation(OperationType.EXPENSE, demoAccount.getId(), 500.0,
                LocalDateTime.now(), expenseCategory.getId(), "Shopping");

        menuService.showSuccess("Demo data created successfully!");
        menuService.showMessage("Account: " + demoAccount.getName());
        menuService.showMessage("Categories: " + incomeCategory.getName() + ", " + expenseCategory.getName());

        // Show analytics
        var summary = analyticsFacade.getAnalyticsSummary(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );
        menuService.showMessage("Demo Analytics - Balance Difference: " + (summary.totalIncome - summary.totalExpense));
    }
}