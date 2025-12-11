package hsebank.finance.core.services;

import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.enums.OperationType;
import hsebank.finance.core.domain.interfaces.IFinancialFactory;
import hsebank.finance.core.domain.interfaces.IOperationService;
import hsebank.finance.core.domain.interfaces.IRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;


@Service
public class OperationFacade implements IOperationService {
    private final IFinancialFactory factory;
    private final IRepository<Operation> operationRepository;
    private final BalanceService balanceService;

    public OperationFacade(IFinancialFactory factory,
                           IRepository<Operation> operationRepository,
                           BalanceService balanceService) {
        this.factory = factory;
        this.operationRepository = operationRepository;
        this.balanceService = balanceService;
    }

    @Override
    public Operation addOperation(OperationType type, UUID bankAccountId, double amount,
                                  LocalDateTime date, UUID categoryId, String description) {
        Operation operation = factory.createOperation(type, bankAccountId, amount, date, categoryId, description);
        balanceService.applyOperation(operation);
        return operationRepository.save(operation);
    }

    @Override
    public Operation getOperation(UUID id) {
        return operationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operation not found with ID: " + id));
    }

    @Override
    public Operation updateOperation(UUID id, OperationType type, UUID bankAccountId, double amount,
                                     LocalDateTime date, UUID categoryId, String description) {
        Operation operation = getOperation(id);

        balanceService.revertOperation(operation);

        operation.update(type, bankAccountId, amount, date, categoryId, description);

        balanceService.applyOperation(operation);

        return operationRepository.save(operation);
    }

    @Override
    public void deleteOperation(UUID id) {
        Operation operation = getOperation(id);
        balanceService.revertOperation(operation);
        operationRepository.delete(id);
    }

    @Override
    public List<Operation> getAllOperations() {
        return operationRepository.findAll();
    }

    @Override
    public List<Operation> getOperationsByAccount(UUID accountId) {
        return operationRepository.findAll().stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .toList();
    }

    @Override
    public List<Operation> getOperationsInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return operationRepository.findAll().stream()
                .filter(op -> !op.getDate().isBefore(startDate) && !op.getDate().isAfter(endDate))
                .toList();
    }
}