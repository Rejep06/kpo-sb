package hsebank.finance.application;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinanceCLI implements CommandLineRunner {

    private final CLIMenuService menuService;
    private final AccountManagementService accountManagementService;
    private final CategoryManager categoryManagementService;
    private final OperationManager operationManager;
    private final AnalyticsManager analyticsManager;
    private final ExportManager exportManager;
    private final ImportManager importManager;
    private final SystemToolsManager systemToolsManager;
    private final PatternDemoManager patternDemoManager;

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
                case "4" -> analyticsManager.showAnalyticsMenu();
                case "5" -> exportManager.showExportMenu();
                case "6" -> importManager.showImportMenu();
                case "7" -> systemToolsManager.showSystemToolsMenu();
                case "8" -> patternDemoManager.demonstratePatterns();
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
                case "1" -> accountManagementService.createAccount();
                case "2" -> accountManagementService.showAllAccounts();
                case "3" -> accountManagementService.findAccountById();
                case "4" -> accountManagementService.updateAccount();
                case "5" -> accountManagementService.deleteAccount();
                case "6" -> accountManagementService.recalculateAccountBalance();
                case "0" -> {
                    return;
                }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void categoryManagement() {
        while (true) {
            menuService.showMessage("\n CATEGORY MANAGEMENT:");
            menuService.showMessage("1. Create Category");
            menuService.showMessage("2. List All Categories");
            menuService.showMessage("3. Find Category by ID");
            menuService.showMessage("4. Update Category");
            menuService.showMessage("5. Delete Category");
            menuService.showMessage("0. Back");

            String choice = menuService.getInput("\nSelect option: ");

            switch (choice) {
                case "1" -> categoryManagementService.createCategory();
                case "2" -> categoryManagementService.showAllCategories();
                case "3" -> categoryManagementService.findCategoryById();
                case "4" -> categoryManagementService.updateCategory();
                case "5" -> categoryManagementService.deleteCategory();
                case "0" -> {
                    return;
                }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void operationManagement() {
        while (true) {
            menuService.showMessage("\n OPERATION MANAGEMENT:");
            menuService.showMessage("1. Create Operation");
            menuService.showMessage("2. List All Operations");
            menuService.showMessage("3. Find Operation by ID");
            menuService.showMessage("4. Update Operation");
            menuService.showMessage("5. Delete Operation");
            menuService.showMessage("0. Back");

            String choice = menuService.getInput("\nSelect option: ");

            switch (choice) {
                case "1" -> operationManager.createOperation();
                case "2" -> operationManager.showAllOperations();
                case "3" -> operationManager.findOperationById();
                case "4" -> operationManager.updateOperation();
                case "5" -> operationManager.deleteOperation();
                case "0" -> {
                    return;
                }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }
}