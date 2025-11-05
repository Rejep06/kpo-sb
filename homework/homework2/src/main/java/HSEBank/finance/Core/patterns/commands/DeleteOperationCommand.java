package HSEBank.finance.Core.patterns.commands;

import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.interfaces.IOperationService;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteOperationCommand extends BaseCommand {
    private final IOperationService operationService;
    private final UUID operationId;
    private Operation deletedOperation;

    public DeleteOperationCommand(IOperationService operationService, UUID operationId) {
        super("Delete operation: " + operationId);
        this.operationService = operationService;
        this.operationId = operationId;
    }

    @Override
    public void execute() {
        deletedOperation = operationService.getOperation(operationId);
        operationService.deleteOperation(operationId);
    }

    @Override
    public void undo() {
        if (deletedOperation != null) {
            operationService.addOperation(
                    deletedOperation.getType(),
                    deletedOperation.getBankAccountId(),
                    deletedOperation.getAmount(),
                    deletedOperation.getDate(),
                    deletedOperation.getCategoryId(),
                    deletedOperation.getDescription()
            );
        }
    }
}