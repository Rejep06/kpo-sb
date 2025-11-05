package HSEBank.finance.Core.patterns.commands;

import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.IOperationService;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AddOperationCommand extends BaseCommand {
    private final OperationType type;
    private final IOperationService operationService;
    private final UUID bankAccountId;
    private final double amount;
    private final LocalDateTime date;
    private final UUID categoryId;
    private final String description;
    private Operation operation;
    private UUID operationId;

    public AddOperationCommand(OperationType type, IOperationService operationService, UUID bankAccountId, double amount,
                               LocalDateTime date, UUID categoryId, String description) {
        super("Add operation for account: " + bankAccountId);
        this.type = type;
        this.operationService = operationService;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.date = date;
        this.categoryId = categoryId;
        this.description = description;
    }

    @Override
    public String validate() {
        if (bankAccountId == null) {
            return "Bank account ID cannot be null";
        }
        if (amount <= 0) {
            return "Amount must be positive";
        }
        if (categoryId == null) {
            return "Category ID cannot be null";
        }
        if (date == null) {
            return "Date cannot be null";
        }
        return null;
    }

    @Override
    public void execute() {
        operation = operationService.addOperation(type, bankAccountId, amount, date, categoryId, description);
        operationId = operation.getId();
    }

    @Override
    public void undo() {
        if (operationId != null) {
            operationService.deleteOperation(operationId);
        }
    }
}