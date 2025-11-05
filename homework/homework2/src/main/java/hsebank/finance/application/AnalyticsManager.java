package hsebank.finance.application;


import hsebank.finance.core.services.AnalyticsFacade;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsManager {
    private final CLIMenuService menuService;
    private final AnalyticsFacade analyticsFacade;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void showAnalyticsMenu() {
        while (true) {
            showAnalyticsOptions();
            String choice = menuService.getInput("\nSelect option: ");

            switch (choice) {
                case "1" -> showBalanceDifference();
                case "2" -> showIncomesByCategory();
                case "3" -> showExpensesByCategory();
                case "4" -> showAnalyticsSummary();
                case "0" -> {
                    return;
                }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void showAnalyticsOptions() {
        menuService.showMessage("\n ANALYTICS & REPORTS:");
        menuService.showMessage("1. Balance Difference (Income - Expense)");
        menuService.showMessage("2. Incomes by Category");
        menuService.showMessage("3. Expenses by Category");
        menuService.showMessage("4. Analytics Summary");
        menuService.showMessage("0. Back");
    }

    private void showBalanceDifference() {
        try {
            LocalDateTime startDate = getDateInput("Enter start date (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDate = getDateInput("Enter end date (yyyy-MM-dd HH:mm): ");

            double difference = analyticsFacade.calculateBalanceDifference(startDate, endDate);
            menuService.showMessage(String.format("\n📊 BALANCE DIFFERENCE: %.2f", difference));
            menuService.showMessage("Period: " + startDate.format(dateFormatter) + " - "
                    + endDate.format(dateFormatter));

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showIncomesByCategory() {
        try {
            LocalDateTime startDate = getDateInput("Enter start date (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDate = getDateInput("Enter end date (yyyy-MM-dd HH:mm): ");

            var incomesByCategory = analyticsFacade.groupIncomesByCategory(startDate, endDate);
            menuService.showMessage("\n INCOMES BY CATEGORY:");
            incomesByCategory.forEach((category, amount) ->
                    menuService.showMessage(String.format("  %s: %.2f", category, amount)));

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showExpensesByCategory() {
        try {
            LocalDateTime startDate = getDateInput("Enter start date (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDate = getDateInput("Enter end date (yyyy-MM-dd HH:mm): ");

            var expensesByCategory = analyticsFacade.groupExpensesByCategory(startDate, endDate);
            menuService.showMessage("\n EXPENSES BY CATEGORY:");
            expensesByCategory.forEach((category, amount) ->
                    menuService.showMessage(String.format("  %s: %.2f", category, amount)));

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showAnalyticsSummary() {
        try {
            LocalDateTime startDate = getDateInput("Enter start date (yyyy-MM-dd HH:mm): ");
            LocalDateTime endDate = getDateInput("Enter end date (yyyy-MM-dd HH:mm): ");

            var summary = analyticsFacade.getAnalyticsSummary(startDate, endDate);
            menuService.showMessage("\n ANALYTICS SUMMARY:");
            menuService.showMessage(String.format("Total Income: %.2f", summary.totalIncome));
            menuService.showMessage(String.format("Total Expense: %.2f", summary.totalExpense));
            menuService.showMessage(String.format("Balance Difference: %.2f",
                    summary.totalIncome - summary.totalExpense));
            menuService.showMessage("Operations Count: " + summary.operationCount);
            menuService.showMessage(String.format("Average Income: %.2f", summary.averageIncome));
            menuService.showMessage(String.format("Average Expense: %.2f", summary.averageExpense));

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private LocalDateTime getDateInput(String prompt) {
        while (true) {
            try {
                String dateInput = menuService.getInput(prompt);
                return LocalDateTime.parse(dateInput, dateFormatter);
            } catch (DateTimeParseException e) {
                menuService.showError("Invalid date format. Please use yyyy-MM-dd HH:mm");
            }
        }
    }
}