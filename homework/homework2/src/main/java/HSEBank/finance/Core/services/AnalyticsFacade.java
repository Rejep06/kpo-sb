package HSEBank.finance.Core.services;

import HSEBank.finance.Core.domain.entities.Category;
import HSEBank.finance.Core.domain.entities.Operation;
import HSEBank.finance.Core.domain.enums.OperationType;
import HSEBank.finance.Core.domain.interfaces.IRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsFacade {
    private final IRepository<Operation> operationRepository;
    private final IRepository<Category> categoryRepository;

    public AnalyticsFacade(IRepository<Operation> operationRepository,
                           IRepository<Category> categoryRepository) {
        this.operationRepository = operationRepository;
        this.categoryRepository = categoryRepository;
    }

    // a. Подсчет разницы доходов и расходов за выбранный период
    public double calculateBalanceDifference(LocalDateTime startDate, LocalDateTime endDate) {
        List<Operation> operations = getOperationsInPeriod(startDate, endDate);

        double totalIncome = operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();

        double totalExpense = operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();

        return totalIncome - totalExpense;
    }

    // b. Группировка доходов и расходов по категориям
    public Map<String, Double> groupIncomesByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        return groupOperationsByCategory(OperationType.INCOME, startDate, endDate);
    }

    public Map<String, Double> groupExpensesByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        return groupOperationsByCategory(OperationType.EXPENSE, startDate, endDate);
    }

    private Map<String, Double> groupOperationsByCategory(OperationType type, LocalDateTime startDate, LocalDateTime endDate) {
        return getOperationsInPeriod(startDate, endDate).stream()
                .filter(op -> op.getType() == type)
                .collect(Collectors.groupingBy(
                        op -> categoryRepository.findById(op.getCategoryId())
                                .map(Category::getName)
                                .orElse("Unknown"),
                        Collectors.summingDouble(Operation::getAmount)
                ));
    }

    // c. Дополнительная аналитика
    public AnalyticsSummary getAnalyticsSummary(LocalDateTime startDate, LocalDateTime endDate) {
        List<Operation> operations = getOperationsInPeriod(startDate, endDate);

        double totalIncome = operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();

        double totalExpense = operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();

        long operationCount = operations.size();
        double averageIncome = operations.stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .average()
                .orElse(0.0);

        double averageExpense = operations.stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .average()
                .orElse(0.0);

        return new AnalyticsSummary(totalIncome, totalExpense, operationCount, averageIncome, averageExpense);
    }

    public double getTotalIncome(LocalDateTime startDate, LocalDateTime endDate) {
        return getOperationsInPeriod(startDate, endDate).stream()
                .filter(op -> op.getType() == OperationType.INCOME)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    public double getTotalExpense(LocalDateTime startDate, LocalDateTime endDate) {
        return getOperationsInPeriod(startDate, endDate).stream()
                .filter(op -> op.getType() == OperationType.EXPENSE)
                .mapToDouble(Operation::getAmount)
                .sum();
    }

    private List<Operation> getOperationsInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return operationRepository.findAll().stream()
                .filter(op -> !op.getDate().isBefore(startDate) && !op.getDate().isAfter(endDate))
                .toList();
    }

    public static class AnalyticsSummary {
        public final double totalIncome;
        public final double totalExpense;
        public final long operationCount;
        public final double averageIncome;
        public final double averageExpense;

        public AnalyticsSummary(double totalIncome, double totalExpense, long operationCount,
                                double averageIncome, double averageExpense) {
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.operationCount = operationCount;
            this.averageIncome = averageIncome;
            this.averageExpense = averageExpense;
        }
    }
}