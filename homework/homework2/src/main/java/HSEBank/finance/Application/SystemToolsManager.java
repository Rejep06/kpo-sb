package HSEBank.finance.Application;

import HSEBank.finance.Core.services.BalanceService;
import HSEBank.finance.Infrastructure.data.cache.RepositoryProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemToolsManager {
    private final CLIMenuService menuService;
    private final BalanceService balanceService;
    private final CacheMonitorService cacheMonitorService;

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
        try {
            var stats = cacheMonitorService.getCacheStatistics();
            menuService.showMessage("\n CACHE STATISTICS:");

            stats.forEach((type, stat) -> {
                if (stat instanceof RepositoryProxy.CacheStats cacheStat) {
                    menuService.showMessage(String.format(
                            "%s: Hits=%d, Misses=%d, Size=%d, Hit Ratio=%.2f%%",
                            type.toUpperCase(), cacheStat.hits, cacheStat.misses,
                            cacheStat.size, cacheStat.hitRatio * 100
                    ));
                }
            });

        } catch (Exception e) {
            menuService.showError("Failed to get cache statistics: " + e.getMessage());
        }
    }

    private void clearCache() {
        try {
            cacheMonitorService.clearAllCaches();
            menuService.showSuccess("All caches cleared successfully");
        } catch (Exception e) {
            menuService.showError("Failed to clear cache: " + e.getMessage());
        }
    }
}