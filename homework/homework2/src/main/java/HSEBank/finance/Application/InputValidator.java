package HSEBank.finance.Application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InputValidator {
    private final CLIMenuService menuService;

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
                UUID.fromString(input); // Validate UUID format
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        while (true) {
            try {
                String input = menuService.getInput(prompt);
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                menuService.showError("Invalid date format. Please use yyyy-MM-dd HH:mm");
            }
        }
    }
}