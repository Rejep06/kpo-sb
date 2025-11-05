package HSEBank.finance.Core.domain.interfaces;

import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IOperationService {
    Operation addOperation(OperationType type, UUID bankAccountId, double amount,
                           LocalDateTime date, UUID categoryId, String description);
    Operation getOperation(UUID id);
    Operation updateOperation(UUID id, OperationType type, UUID bankAccountId, double amount,
                              LocalDateTime date, UUID categoryId, String description);
    void deleteOperation(UUID id);
    List<Operation> getAllOperations();
    List<Operation> getOperationsByAccount(UUID accountId);
    List<Operation> getOperationsInPeriod(LocalDateTime startDate, LocalDateTime endDate);
}