package HSEBank.finance.Application;

import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.IOperationService;
import HSEBank.finance.Core.patterns.commands.AddOperationCommand;
import HSEBank.finance.Core.patterns.commands.DeleteOperationCommand;
import HSEBank.finance.Core.patterns.commands.UpdateOperationCommand;
import HSEBank.finance.Core.patterns.decorator.TimingDecorator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationManager {
    private final IOperationService operationService;
    private final CLIMenuService menuService;
    private final InputValidator inputValidator;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void createOperation() {
        try {
            OperationType type = inputValidator.getValidatedOperationType("Select operation type:");
            String accountId = inputValidator.getValidatedUUID("Enter account ID: ");
            double amount = inputValidator.getValidatedDouble("Enter amount: ", 0.01, 1_000_000.0);
            String categoryId = inputValidator.getValidatedUUID("Enter category ID: ");
            String description = menuService.getInput("Enter description: ");

            LocalDateTime date = LocalDateTime.now();

            var addOpCmd = new AddOperationCommand(type, operationService, UUID.fromString(accountId),
                    amount, date, UUID.fromString(categoryId), description);
            new TimingDecorator(addOpCmd).execute();

            Operation operation = addOpCmd.getOperation();
            menuService.showSuccess("Operation created: " + operation.getId());

        } catch (Exception e) {
            menuService.showError("Operation creation failed: " + e.getMessage());
        }
    }

    public void showAllOperations() {
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
                        operation.getDate().format(dateFormatter));
                menuService.showMessage(line);
            }
            menuService.showMessage("└────────────────────────────────────┴────────────┴────────────────────┴────────────┴────────────────────┘");

        } catch (Exception e) {
            menuService.showError("Failed to load operations: " + e.getMessage());
        }
    }

    public void findOperationById() {
        try {
            String id = inputValidator.getValidatedUUID("Enter operation ID: ");
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
            menuService.showError("Operation not found: " + e.getMessage());
        }
    }

    public void updateOperation() {
        try {
            String id = inputValidator.getValidatedUUID("Enter operation ID to update: ");
            OperationType type = inputValidator.getValidatedOperationType("Select new operation type:");
            String accountId = inputValidator.getValidatedUUID("Enter new account ID: ");
            double amount = inputValidator.getValidatedDouble("Enter new amount: ", 0.01, 1_000_000.0);
            String categoryId = inputValidator.getValidatedUUID("Enter new category ID: ");
            String description = menuService.getInput("Enter new description: ");

            LocalDateTime date = LocalDateTime.now();

            var updateOpCmd = new UpdateOperationCommand(operationService, UUID.fromString(id), type,
                    UUID.fromString(accountId), amount, date, UUID.fromString(categoryId), description);
            new TimingDecorator(updateOpCmd).execute();

            menuService.showSuccess("Operation updated successfully");

        } catch (Exception e) {
            menuService.showError("Operation update failed: " + e.getMessage());
        }
    }

    public void deleteOperation() {
        try {
            String id = inputValidator.getValidatedUUID("Enter operation ID to delete: ");
            var deleteOpCmd = new DeleteOperationCommand(operationService, UUID.fromString(id));
            new TimingDecorator(deleteOpCmd).execute();

            menuService.showSuccess("Operation deleted successfully");

        } catch (Exception e) {
            menuService.showError("Operation deletion failed: " + e.getMessage());
        }
    }
}