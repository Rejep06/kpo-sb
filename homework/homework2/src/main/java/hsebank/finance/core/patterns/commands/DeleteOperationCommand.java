package hsebank.finance.core.patterns.commands;

import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.interfaces.IOperationService;
import java.util.UUID;
import lombok.Getter;


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