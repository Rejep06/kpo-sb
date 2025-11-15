package hsebank.finance.application;

import hsebank.finance.core.domain.enums.OperationType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InputValidator {
    private final CLIMenuService menuService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public double getValidatedDouble(String prompt, double min, double max) {
        while (true) {
            try {
                String input = menuService.getInput(prompt);
                double value = Double.parseDouble(input);

                if (value < min) {
                    menuService.showError("Value must be at least " + min);
                    continue;
                }
                if (value > max) {
                    menuService.showError("Value must be at most " + max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                menuService.showError("Please enter a valid number");
            }
        }
    }

    public String getValidatedUUID(String prompt) {
        while (true) {
            try {
                String input = menuService.getInput(prompt);
                UUID.fromString(input);
                return input;
            } catch (IllegalArgumentException e) {
                menuService.showError("Please enter a valid UUID format");
            }
        }
    }

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public LocalDateTime getValidatedDateTime(String prompt) {
        while (true) {
            try {
                String input = menuService.getInput(prompt);
                return LocalDateTime.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                menuService.showError("Invalid date format. Please use yyyy-MM-dd HH:mm");
            }
        }
    }

    public OperationType getValidatedOperationType(String prompt) {
        while (true) {
            try {
                menuService.showMessage(prompt);
                menuService.showMessage("1. Income");
                menuService.showMessage("2. Expense");
                String choice = menuService.getInput("Choice: ");

                return switch (choice) {
                    case "1" -> OperationType.INCOME;
                    case "2" -> OperationType.EXPENSE;
                    default -> throw new IllegalArgumentException("Invalid choice");
                };
            } catch (IllegalArgumentException e) {
                menuService.showError("Please select 1 for Income or 2 for Expense");
            }
        }
    }
}