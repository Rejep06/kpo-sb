package HSEBank.finance.Core.patterns.commands;

import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.IOperationService;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class UpdateOperationCommand extends BaseCommand {
    private final IOperationService operationService;
    private final UUID operationId;
    private final OperationType newType;
    private final UUID newBankAccountId;
    private final double newAmount;
    private final LocalDateTime newDate;
    private final UUID newCategoryId;
    private final String newDescription;

    private final OperationType oldType;
    private final UUID oldBankAccountId;
    private final double oldAmount;
    private final LocalDateTime oldDate;
    private final UUID oldCategoryId;
    private final String oldDescription;

    private Operation updatedOperation;

    public UpdateOperationCommand(IOperationService operationService, UUID operationId,
                                  OperationType newType, UUID newBankAccountId, double newAmount,
                                  LocalDateTime newDate, UUID newCategoryId, String newDescription) {
        super("Update operation: " + operationId);
        this.operationService = operationService;
        this.operationId = operationId;
        this.newType = newType;
        this.newBankAccountId = newBankAccountId;
        this.newAmount = newAmount;
        this.newDate = newDate;
        this.newCategoryId = newCategoryId;
        this.newDescription = newDescription;

        Operation oldOperation = operationService.getOperation(operationId);
        this.oldType = oldOperation.getType();
        this.oldBankAccountId = oldOperation.getBankAccountId();
        this.oldAmount = oldOperation.getAmount();
        this.oldDate = oldOperation.getDate();
        this.oldCategoryId = oldOperation.getCategoryId();
        this.oldDescription = oldOperation.getDescription();
    }

    @Override
    public void execute() {
        updatedOperation = operationService.updateOperation(
                operationId, newType, newBankAccountId, newAmount, newDate, newCategoryId, newDescription
        );
    }

    @Override
    public void undo() {
        operationService.updateOperation(
                operationId, oldType, oldBankAccountId, oldAmount, oldDate, oldCategoryId, oldDescription
        );
    }
}