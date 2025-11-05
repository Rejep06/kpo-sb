package HSEBank.finance.Application;

import HSEBank.finance.Core.patterns.commands.ExportDataCommand;
import HSEBank.finance.Core.patterns.decorator.TimingDecorator;
import HSEBank.finance.Core.services.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExportManager {
    private final CLIMenuService menuService;
    private final ExportService exportService;

    public void showExportMenu() {
        while (true) {
            showExportOptions();
            String choice = menuService.getInput("\nSelect option: ");

            switch (choice) {
                case "1" -> exportData("json");
                case "2" -> exportData("csv");
                case "3" -> exportData("yaml");
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void showExportOptions() {
        menuService.showMessage("\n DATA EXPORT:");
        menuService.showMessage("1. Export to JSON");
        menuService.showMessage("2. Export to CSV");
        menuService.showMessage("3. Export to YAML");
        menuService.showMessage("0. Back");
    }

    private void exportData(String format) {
        try {
            String accountIdInput = menuService.getInput("Enter account ID (or press Enter for all accounts): ");
            UUID accountId = accountIdInput.isEmpty() ? null : UUID.fromString(accountIdInput);

            String fileName = "exported_data." + format;
            var exportCommand = new ExportDataCommand(exportService, format, fileName, accountId);
            new TimingDecorator(exportCommand).execute();

            menuService.showSuccess("Data exported successfully to: " + fileName);

        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }
}