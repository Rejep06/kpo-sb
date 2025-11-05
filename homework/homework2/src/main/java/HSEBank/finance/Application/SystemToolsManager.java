package HSEBank.finance.Application;

import HSEBank.finance.Core.services.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemToolsManager {
    private final CLIMenuService menuService;
    private final BalanceService balanceService;

    public void showSystemToolsMenu() {
        while (true) {
            String choice = menuService.showSystemToolsMenu();

            switch (choice) {
                case "1" -> recalculateAllBalances();
                case "2" -> showCacheStatistics();
                case "3" -> clearCache();
                case "0" -> { return; }
                default -> menuService.showError("Invalid choice!");
            }
        }
    }

    private void recalculateAllBalances() {
        try {
            var result = balanceService.recalculateAllBalances();
            menuService.showSuccess(String.format(
                    "Recalculation complete: %d accounts processed, %d corrected, total correction: %.2f",
                    result.totalAccounts, result.correctedAccounts, result.totalCorrection
            ));
        } catch (Exception e) {
            menuService.showError(e.getMessage());
        }
    }

    private void showCacheStatistics() {
        // Реализация статистики кэша может быть добавлена позже
        menuService.showMessage("\n CACHE STATISTICS:");
        menuService.showMessage("Cache statistics feature coming soon...");
    }

    private void clearCache() {
        // Реализация очистки кэша может быть добавлена позже
        menuService.showSuccess("Cache clearing feature coming soon...");
    }
}