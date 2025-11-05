package hsebank.finance.core.services;

import hsebank.finance.core.domain.entities.BankAccount;
import hsebank.finance.core.domain.entities.Operation;
import hsebank.finance.core.domain.enums.OperationType;
import hsebank.finance.core.domain.interfaces.IRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BalanceService {
    private final IRepository<BankAccount> accountRepository;
    private final IRepository<Operation> operationRepository;

    public BalanceService(IRepository<BankAccount> accountRepository, IRepository<Operation> operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    public void applyOperation(Operation operation) {
        BankAccount account = accountRepository.findById(operation.getBankAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));

        if (operation.getType() == OperationType.INCOME) {
            account.deposit(operation.getAmount());
        } else {
            account.withdraw(operation.getAmount());
        }

        accountRepository.save(account);
    }

    public void revertOperation(Operation operation) {
        BankAccount account = accountRepository.findById(operation.getBankAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));

        // Обратная операция
        if (operation.getType() == OperationType.INCOME) {
            account.withdraw(operation.getAmount());
        } else {
            account.deposit(operation.getAmount());
        }

        accountRepository.save(account);
    }

    public double recalculateBalance(UUID accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));

        // Рассчитываем баланс на основе всех операций
        double calculatedBalance = operationRepository.findAll().stream()
                .filter(op -> op.getBankAccountId().equals(accountId))
                .mapToDouble(op -> op.getType() == OperationType.INCOME ? op.getAmount() : -op.getAmount())
                .sum();

        // Обновляем баланс счета, если он отличается
        if (Math.abs(account.getBalance() - calculatedBalance) > 0.01) {
            account.update(account.getName(), calculatedBalance);
            accountRepository.save(account);
        }

        return calculatedBalance;
    }

    public BalanceRecalculationResult recalculateAllBalances() {
        var accounts = accountRepository.findAll();
        int correctedAccounts = 0;
        double totalCorrection = 0;

        for (BankAccount account : accounts) {
            double oldBalance = account.getBalance();
            double newBalance = recalculateBalance(account.getId());

            if (Math.abs(oldBalance - newBalance) > 0.01) {
                correctedAccounts++;
                totalCorrection += Math.abs(oldBalance - newBalance);
            }
        }

        return new BalanceRecalculationResult(accounts.size(), correctedAccounts, totalCorrection);
    }

    public static class BalanceRecalculationResult {
        public final int totalAccounts;
        public final int correctedAccounts;
        public final double totalCorrection;

        public BalanceRecalculationResult(int totalAccounts, int correctedAccounts, double totalCorrection) {
            this.totalAccounts = totalAccounts;
            this.correctedAccounts = correctedAccounts;
            this.totalCorrection = totalCorrection;
        }
    }
}