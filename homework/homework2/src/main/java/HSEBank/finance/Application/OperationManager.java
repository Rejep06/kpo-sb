package HSEBank.finance.Application;

import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.IOperationService;
import HSEBank.finance.Core.patterns.commands.AddOperationCommand;
import HSEBank.finance.Core.patterns.commands.DeleteOperationCommand;
import HSEBank.finance.Core.patterns.commands.UpdateOperationCommand;
import HSEBank.finance.Core.patterns.decorator.TimingDecorator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OperationManager {
    private final CLIMenuService menuService;
    private final IOperationService operationService;

    public void manageOperations() {
        while (true) {
            showOperationMenu();
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

    private void showOperationMenu() {
        menuService.showMessage("\n OPERATION MANAGEMENT:");
        menuService.showMessage("1. Create Operation");
        menuService.showMessage("2. List All Operations");
        menuService.showMessage("3. Find Operation by ID");
        menuService.showMessage("4. ️ Update Operation");
        menuService.showMessage("5. Delete Operation");
        menuService.showMessage("0. ️ Back");
    }

    public void createOperation() {
        try {
            menuService.showMessage("Select operation type:");
            menuService.showMessage("1. Income");
            menuService.showMessage("2. Expense");
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
                menuService.showMessage(" No operations found");
                return;
            }

            menuService.showMessage("\n OPERATIONS LIST:");
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

            menuService.showMessage("\n OPERATION DETAILS:");
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
            menuService.showMessage("1. Income");
            menuService.showMessage("2. Expense");
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
}