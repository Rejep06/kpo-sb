package hsebank.finance.application;

import hsebank.finance.core.services.DataImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportManager {
    private final CLIMenuService menuService;
    private final DataImportService importService;

    public void showImportMenu() {
        while (true) {
            String choice = menuService.showImportMenu();

            switch (choice) {
                case "1" -> importData("json");
                case "2" -> importData("csv");
                case "3" -> importData("yaml");
                case "0" -> {
                    return;
                }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void importData(String format) {
        try {
            String filePath = menuService.getInput("Enter file path: ");
            String result = importService.importData(format, filePath);
            menuService.showSuccess(result);
        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }
}