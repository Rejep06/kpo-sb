package HSEBank.finance.Application;

import HSEBank.finance.Core.domain.entities.BankAccount;
import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.IBankAccountService;
import HSEBank.finance.Core.domain.interfaces.ICategoryService;
import HSEBank.finance.Core.domain.interfaces.IOperationService;
import HSEBank.finance.Core.patterns.commands.*;
import HSEBank.finance.Core.patterns.decorator.TimingDecorator;
import HSEBank.finance.Core.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Component
public class FinanceCLI implements CommandLineRunner {

    private final CLIMenuService menuService;
    private final IBankAccountService accountService;
    private final ICategoryService categoryService;
    private final IOperationService operationService;
    private final AnalyticsFacade analyticsFacade;
    private final ExportService exportService;
    private final DataImportService importService;
    private final BalanceService balanceService;
    private final InputValidator inputValidator;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FinanceCLI(CLIMenuService menuService,
                      IBankAccountService accountService,
                      ICategoryService categoryService,
                      IOperationService operationService,
                      AnalyticsFacade analyticsFacade,
                      ExportService exportService,
                      DataImportService importService,
                      BalanceService balanceService,
                      InputValidator inputValidator) {
        this.menuService = menuService;
        this.accountService = accountService;
        this.categoryService = categoryService;
        this.operationService = operationService;
        this.analyticsFacade = analyticsFacade;
        this.exportService = exportService;
        this.importService = importService;
        this.balanceService = balanceService;
        this.inputValidator = inputValidator;
    }

    @Override
    public void run(String... args) {
        menuService.showWelcomeMessage();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = menuService.showMainMenu();

            switch (choice) {
                case "1" -> accountManagement();
                case "2" -> categoryManagement();
                case "3" -> operationManagement();
                case "4" -> analyticsMenu();
                case "5" -> exportMenu();
                case "6" -> importMenu();
                case "7" -> systemToolsMenu();
                case "8" -> demonstratePatterns();
                case "0" -> {
                    menuService.showMessage("\n👋 Goodbye!");
                    return;
                }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void accountManagement() {
        while (true) {
            String choice = menuService.showAccountManagementMenu();

            switch (choice) {
                case "1" -> createAccount();
                case "2" -> showAllAccounts();
                case "3" -> findAccountById();
                case "4" -> updateAccount();
                case "5" -> deleteAccount();
                case "6" -> recalculateAccountBalance();
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void createAccount() {
        try {
            String name = menuService.getInput("Enter account name: ");
            if (!inputValidator.isValidName(name)) { // Добавить inputValidator в FinanceCLI
                menuService.showError("Account name cannot be empty");
                return;
            }

            double balance = inputValidator.getValidatedDouble("Enter initial balance: ", 0.0, 1_000_000.0);

            var createCmd = new CreateAccountCommand(accountService, name, balance);

            String validationError = createCmd.validate();
            if (validationError != null) {
                menuService.showError(validationError);
                return;
            }

            new TimingDecorator(createCmd).execute();
            BankAccount account = createCmd.getCreatedAccount();
            menuService.showSuccess("Account created: " + account.getName() + " (ID: " + account.getId() + ")");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showAllAccounts() {
        try {
            List<BankAccount> accounts = accountService.getAllAccounts();
            if (accounts.isEmpty()) {
                menuService.showMessage("📭 No accounts found");
                return;
            }

            menuService.showMessage("\n📋 ACCOUNTS LIST:");
            menuService.showMessage("┌────────────────────────────────────┬────────────────────┬────────────┐");
            menuService.showMessage("│ ID                                 │ Name               │ Balance    │");
            menuService.showMessage("├────────────────────────────────────┼────────────────────┼────────────┤");

            for (BankAccount account : accounts) {
                String line = String.format("│ %-34s │ %-18s │ %10.2f │",
                        account.getId(),
                        account.getName(),
                        account.getBalance());
                menuService.showMessage(line);
            }
            menuService.showMessage("└────────────────────────────────────┴────────────────────┴────────────┘");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void findAccountById() {
        try {
            String id = menuService.getInput("Enter account ID: ");
            BankAccount account = accountService.getAccount(UUID.fromString(id));

            if (account == null) {
                menuService.showError("Account not found");
                return;
            }

            menuService.showMessage("\n🔍 ACCOUNT DETAILS:");
            menuService.showMessage("ID: " + account.getId());
            menuService.showMessage("Name: " + account.getName());
            menuService.showMessage("Balance: " + account.getBalance());

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void updateAccount() {
        try {
            String id = menuService.getInput("Enter account ID to update: ");
            String name = menuService.getInput("Enter new account name: ");
            String balanceInput = menuService.getInput("Enter new balance: ");
            double balance = Double.parseDouble(balanceInput);

            var updateCmd = new UpdateAccountCommand(accountService, UUID.fromString(id), name, balance);
            new TimingDecorator(updateCmd).execute();

            menuService.showSuccess("Account updated successfully");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void deleteAccount() {
        try {
            String id = menuService.getInput("Enter account ID to delete: ");
            accountService.deleteAccount(UUID.fromString(id));
            menuService.showSuccess("Account deleted successfully");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void recalculateAccountBalance() {
        try {
            String accountId = menuService.getInput("Enter account ID to recalculate: ");
            double newBalance = balanceService.recalculateBalance(UUID.fromString(accountId));
            menuService.showSuccess("Balance recalculated: " + newBalance);
        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void categoryManagement() {
        while (true) {
            menuService.showMessage("\n📁 CATEGORY MANAGEMENT:");
            menuService.showMessage("1. 📝 Create Category");
            menuService.showMessage("2. 📋 List All Categories");
            menuService.showMessage("3. 🔍 Find Category by ID");
            menuService.showMessage("4. ✏️ Update Category");
            menuService.showMessage("5. 🗑️ Delete Category");
            menuService.showMessage("0. ↩️ Back");

            String choice = menuService.getInput("\nSelect option: ");

            switch (choice) {
                case "1" -> createCategory();
                case "2" -> showAllCategories();
                case "3" -> findCategoryById();
                case "4" -> updateCategory();
                case "5" -> deleteCategory();
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void createCategory() {
        try {
            menuService.showMessage("Select category type:");
            menuService.showMessage("1. 💰 Income");
            menuService.showMessage("2. 💸 Expense");
            String typeChoice = menuService.getInput("Choice: ");

            OperationType type = typeChoice.equals("1") ? OperationType.INCOME : OperationType.EXPENSE;
            String name = menuService.getInput("Enter category name: ");

            Category category = categoryService.createCategory(type, name);
            menuService.showSuccess("Category created: " + category.getName() + " (ID: " + category.getId() + ")");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            if (categories.isEmpty()) {
                menuService.showMessage("📭 No categories found");
                return;
            }

            menuService.showMessage("\n📋 CATEGORIES LIST:");
            menuService.showMessage("┌────────────────────────────────────┬────────────┬────────────────────┐");
            menuService.showMessage("│ ID                                 │ Type       │ Name               │");
            menuService.showMessage("├────────────────────────────────────┼────────────┼────────────────────┤");

            for (Category category : categories) {
                String typeEmoji = category.getOperationType() == OperationType.INCOME ? "💰" : "💸";
                String line = String.format("│ %-34s │ %-4s%-6s │ %-18s │",
                        category.getId(),
                        typeEmoji,
                        category.getOperationType(),
                        category.getName());
                menuService.showMessage(line);
            }
            menuService.showMessage("└────────────────────────────────────┴────────────┴────────────────────┘");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void findCategoryById() {
        try {
            String id = menuService.getInput("Enter category ID: ");
            Category category = categoryService.getCategory(UUID.fromString(id));

            menuService.showMessage("\n🔍 CATEGORY DETAILS:");
            menuService.showMessage("ID: " + category.getId());
            menuService.showMessage("Type: " + category.getOperationType());
            menuService.showMessage("Name: " + category.getName());

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void updateCategory() {
        try {
            String id = menuService.getInput("Enter category ID to update: ");

            menuService.showMessage("Select new category type:");
            menuService.showMessage("1. 💰 Income");
            menuService.showMessage("2. 💸 Expense");
            String typeChoice = menuService.getInput("Choice: ");

            OperationType type = typeChoice.equals("1") ? OperationType.INCOME : OperationType.EXPENSE;
            String name = menuService.getInput("Enter new category name: ");

            var updateCmd = new UpdateCategoryCommand(categoryService, UUID.fromString(id), type, name);
            new TimingDecorator(updateCmd).execute();

            menuService.showSuccess("Category updated successfully");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void deleteCategory() {
        try {
            String id = menuService.getInput("Enter category ID to delete: ");
            categoryService.deleteCategory(UUID.fromString(id));
            menuService.showSuccess("Category deleted successfully");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void operationManagement() {
        while (true) {
            menuService.showMessage("\n💰 OPERATION MANAGEMENT:");
            menuService.showMessage("1. 📝 Create Operation");
            menuService.showMessage("2. 📋 List All Operations");
            menuService.showMessage("3. 🔍 Find Operation by ID");
            menuService.showMessage("4. ✏️ Update Operation");
            menuService.showMessage("5. 🗑️ Delete Operation");
            menuService.showMessage("0. ↩️ Back");

            String choice = menuService.getInput("\nSelect option: ");

            switch (choice) {
                case "1" -> createOperation();
                case "2" -> showAllOperations();
                case "3" -> findOperationById();
                case "4" -> updateOperation();
                case "5" -> deleteOperation();
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void createOperation() {
        try {
            menuService.showMessage("Select operation type:");
            menuService.showMessage("1. 💰 Income");
            menuService.showMessage("2. 💸 Expense");
            String typeChoice = menuService.getInput("Choice: ");

            OperationType type = typeChoice.equals("1") ? OperationType.INCOME : OperationType.EXPENSE;
            String accountId = menuService.getInput("Enter account ID: ");
            String amountInput = menuService.getInput("Enter amount: ");
            double amount = Double.parseDouble(amountInput);
            String categoryId = menuService.getInput("Enter category ID: ");
            String description = menuService.getInput("Enter description: ");

            LocalDateTime date = LocalDateTime.now();

            var addOpCmd = new AddOperationCommand(type, operationService, UUID.fromString(accountId),
                    amount, date, UUID.fromString(categoryId), description);
            new TimingDecorator(addOpCmd).execute();

            Operation operation = addOpCmd.getOperation();
            menuService.showSuccess("Operation created: " + operation.getId());

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showAllOperations() {
        try {
            List<Operation> operations = operationService.getAllOperations();
            if (operations.isEmpty()) {
                menuService.showMessage("📭 No operations found");
                return;
            }

            menuService.showMessage("\n📋 OPERATIONS LIST:");
            menuService.showMessage("┌────────────────────────────────────┬────────────┬────────────────────┬────────────┬────────────────────┐");
            menuService.showMessage("│ ID                                 │ Type       │ Account ID         │ Amount     │ Date               │");
            menuService.showMessage("├────────────────────────────────────┼────────────┼────────────────────┼────────────┼────────────────────┤");

            for (Operation operation : operations) {
                String typeEmoji = operation.getType() == OperationType.INCOME ? "💰" : "💸";
                String line = String.format("│ %-34s │ %-4s%-6s │ %-18s │ %10.2f │ %-18s │",
                        operation.getId(),
                        typeEmoji,
                        operation.getType(),
                        operation.getBankAccountId(),
                        operation.getAmount(),
                        operation.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                menuService.showMessage(line);
            }
            menuService.showMessage("└────────────────────────────────────┴────────────┴────────────────────┴────────────┴────────────────────┘");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void findOperationById() {
        try {
            String id = menuService.getInput("Enter operation ID: ");
            Operation operation = operationService.getOperation(UUID.fromString(id));

            menuService.showMessage("\n🔍 OPERATION DETAILS:");
            menuService.showMessage("ID: " + operation.getId());
            menuService.showMessage("Type: " + operation.getType());
            menuService.showMessage("Account ID: " + operation.getBankAccountId());
            menuService.showMessage("Amount: " + operation.getAmount());
            menuService.showMessage("Date: " + operation.getDate());
            menuService.showMessage("Category ID: " + operation.getCategoryId());
            menuService.showMessage("Description: " + operation.getDescription());

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void updateOperation() {
        try {
            String id = menuService.getInput("Enter operation ID to update: ");

            menuService.showMessage("Select new operation type:");
            menuService.showMessage("1. 💰 Income");
            menuService.showMessage("2. 💸 Expense");
            String typeChoice = menuService.getInput("Choice: ");

            OperationType type = typeChoice.equals("1") ? OperationType.INCOME : OperationType.EXPENSE;
            String accountId = menuService.getInput("Enter new account ID: ");
            String amountInput = menuService.getInput("Enter new amount: ");
            double amount = Double.parseDouble(amountInput);
            String categoryId = menuService.getInput("Enter new category ID: ");
            String description = menuService.getInput("Enter new description: ");

            LocalDateTime date = LocalDateTime.now();

            var updateOpCmd = new UpdateOperationCommand(operationService, UUID.fromString(id), type,
                    UUID.fromString(accountId), amount, date, UUID.fromString(categoryId), description);
            new TimingDecorator(updateOpCmd).execute();

            menuService.showSuccess("Operation updated successfully");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void deleteOperation() {
        try {
            String id = menuService.getInput("Enter operation ID to delete: ");
            var deleteOpCmd = new DeleteOperationCommand(operationService, UUID.fromString(id));
            new TimingDecorator(deleteOpCmd).execute();

            menuService.showSuccess("Operation deleted successfully");

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }



    private void analyticsMenu() {
        while (true) {
            menuService.showMessage("\n📈 ANALYTICS & REPORTS:");
            menuService.showMessage("1. 📊 Balance Difference (Income - Expense)");
            menuService.showMessage("2. 🏷️ Incomes by Category");
            menuService.showMessage("3. 🏷️ Expenses by Category");
            menuService.showMessage("4. 📋 Analytics Summary");
            menuService.showMessage("0. ↩️ Back");

            String choice = menuService.getInput("\nSelect option: ");

            switch (choice) {
                case "1" -> showBalanceDifference();
                case "2" -> showIncomesByCategory();
                case "3" -> showExpensesByCategory();
                case "4" -> showAnalyticsSummary();
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void showBalanceDifference() {
        try {
            LocalDateTime startDate = getDateInput("Enter start date (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDate = getDateInput("Enter end date (yyyy-MM-dd HH:mm): ");

            double difference = analyticsFacade.calculateBalanceDifference(startDate, endDate);
            menuService.showMessage(String.format("\n📊 BALANCE DIFFERENCE: %.2f", difference));
            menuService.showMessage("Period: " + startDate.format(dateFormatter) + " - " + endDate.format(dateFormatter));

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showIncomesByCategory() {
        try {
            LocalDateTime startDate = getDateInput("Enter start date (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDate = getDateInput("Enter end date (yyyy-MM-dd HH:mm): ");

            var incomesByCategory = analyticsFacade.groupIncomesByCategory(startDate, endDate);
            menuService.showMessage("\n💰 INCOMES BY CATEGORY:");
            incomesByCategory.forEach((category, amount) ->
                    menuService.showMessage(String.format("  %s: %.2f", category, amount)));

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showExpensesByCategory() {
        try {
            LocalDateTime startDate = getDateInput("Enter start date (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDate = getDateInput("Enter end date (yyyy-MM-dd HH:mm): ");

            var expensesByCategory = analyticsFacade.groupExpensesByCategory(startDate, endDate);
            menuService.showMessage("\n💸 EXPENSES BY CATEGORY:");
            expensesByCategory.forEach((category, amount) ->
                    menuService.showMessage(String.format("  %s: %.2f", category, amount)));

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showAnalyticsSummary() {
        try {
            LocalDateTime startDate = getDateInput("Enter start date (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDate = getDateInput("Enter end date (yyyy-MM-dd HH:mm): ");

            var summary = analyticsFacade.getAnalyticsSummary(startDate, endDate);
            menuService.showMessage("\n📋 ANALYTICS SUMMARY:");
            menuService.showMessage(String.format("Total Income: %.2f", summary.totalIncome));
            menuService.showMessage(String.format("Total Expense: %.2f", summary.totalExpense));
            menuService.showMessage(String.format("Balance Difference: %.2f", summary.totalIncome - summary.totalExpense));
            menuService.showMessage("Operations Count: " + summary.operationCount);
            menuService.showMessage(String.format("Average Income: %.2f", summary.averageIncome));
            menuService.showMessage(String.format("Average Expense: %.2f", summary.averageExpense));

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private LocalDateTime getDateInput(String prompt) {
        while (true) {
            try {
                String dateInput = menuService.getInput(prompt);
                return LocalDateTime.parse(dateInput, dateFormatter);
            } catch (DateTimeParseException e) {
                menuService.showError("Invalid date format. Please use yyyy-MM-dd HH:mm");
            }
        }
    }

    private void exportMenu() {
        while (true) {
            menuService.showMessage("\n📤 DATA EXPORT:");
            menuService.showMessage("1. 📄 Export to JSON");
            menuService.showMessage("2. 📊 Export to CSV");
            menuService.showMessage("3. 📋 Export to YAML");
            menuService.showMessage("0. ↩️ Back");

            String choice = menuService.getInput("\nSelect option: ");

            switch (choice) {
                case "1" -> exportData("json");
                case "2" -> exportData("csv");
                case "3" -> exportData("yaml");
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void exportData(String format) {
        try {
            String accountIdInput = menuService.getInput("Enter account ID (or press Enter for all accounts): ");
            UUID accountId = accountIdInput.isEmpty() ? null : UUID.fromString(accountIdInput);

            String fileName = "exported_data." + format;
            var exportCommand = new ExportDataCommand(exportService, format, fileName, accountId);
            new TimingDecorator(exportCommand).execute();

            menuService.showSuccess("Data exported successfully to: " + fileName);

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void importMenu() {
        while (true) {
            String choice = menuService.showImportMenu();

            switch (choice) {
                case "1" -> importData("json");
                case "2" -> importData("csv");
                case "3" -> importData("yaml");
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void importData(String format) {
        try {
            String filePath = menuService.getInput("Enter file path: ");
            String result = importService.importData(format, filePath);
            menuService.showSuccess(result);
        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void systemToolsMenu() {
        while (true) {
            String choice = menuService.showSystemToolsMenu();

            switch (choice) {
                case "1" -> recalculateAllBalances();
                case "2" -> showCacheStatistics();
                case "3" -> clearCache();
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void recalculateAllBalances() {
        try {
            var result = balanceService.recalculateAllBalances();
            menuService.showSuccess(String.format(
                    "Recalculation complete: %d accounts processed, %d corrected, total correction: %.2f",
                    result.totalAccounts, result.correctedAccounts, result.totalCorrection
            ));
        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showCacheStatistics() {
//        double accountHitRatio = accountRepositoryProxy.getCacheHitRatio();
//        double categoryHitRatio = categoryRepositoryProxy.getCacheHitRatio();
//        double operationHitRatio = operationRepositoryProxy.getCacheHitRatio();

        menuService.showMessage("\n📊 CACHE STATISTICS:");
//        menuService.showMessage(String.format("Account Cache Hit Ratio: %.2f%%", accountHitRatio * 100));
//        menuService.showMessage(String.format("Category Cache Hit Ratio: %.2f%%", categoryHitRatio * 100));
//        menuService.showMessage(String.format("Operation Cache Hit Ratio: %.2f%%", operationHitRatio * 100));
    }

    private void clearCache() {
//        accountRepositoryProxy.clearCache();
//        categoryRepositoryProxy.clearCache();
//        operationRepositoryProxy.clearCache();
        menuService.showSuccess("All caches cleared");
    }

    private void demonstratePatterns() {
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